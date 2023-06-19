package com.stardust.sdk.zzftp.task;

import com.stardust.sdk.zzftp.ErrorCode;
import com.stardust.sdk.zzftp.calls.IFTPConnectCall;
import com.stardust.sdk.zzftp.calls.IFTPDownloadCall;
import com.stardust.sdk.zzftp.events.logevent.LoggerEvent;
import com.stardust.sdk.zzftp.thread_pool_manager.ExecutorManager;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class DownloadFileBean implements Runnable, IFTPConnectCall {

    private final String servicePathName;
    private final String localPath;
    private final int bufferSize;
    private final IFTPDownloadCall listener;
    private final boolean append;
    private FTPClient ftpClient;
    private AtomicBoolean isStop = new AtomicBoolean(false);
    private boolean isFinished = false;

    public DownloadFileBean(String servicePathName, String localPath, int bufferSize, boolean append, IFTPDownloadCall listener) {
        this.servicePathName = servicePathName;
        this.localPath = localPath;
        this.bufferSize = bufferSize;
        this.append = append;
        this.listener = listener;
    }

    @Override
    public void onSuccess(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
        ExecutorManager.getInstance().request(this);
    }

    @Override
    public void onFailure(ErrorCode code, String errorMsg) {
        LoggerEvent.out("错误码:" + code + "\n" + errorMsg);
        listener.onFailure(code, errorMsg);
    }

    public void setIsStop(boolean isStop) {
        this.isStop.set(isStop);
    }

    private static String getFileName(String filePath) {
        Exception th = null;
        try {
            File file = new File(filePath);
            return file.getName();
        } catch (Exception e) {
            LoggerEvent.out(e.toString());
            th = e;
        }
        throw new RuntimeException("获取远程文件名操作异常:" + ((th != null) ? th.toString() : ""));
    }

    public boolean isFinished() {
        return isFinished;
    }

    //生成文件夹
    private static void makeRootDirectory(String filePath) throws Exception {
        String[] pathList = filePath.split("/");
        if (pathList.length == 0) {
            throw new IOException("文件保存路径异常:" + filePath);
        }
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
    }

    @Override
    public void run() {
        try {
            String remoteFilePath = new String(servicePathName.getBytes(), StandardCharsets.ISO_8859_1);
            FTPFile ftpFile = ftpClient.listFiles(remoteFilePath)[0];
            if (ftpFile == null) {
                listener.onFailure(ErrorCode.FTP_DOWNLOAD_FILE_NOT_EXIST, String.format("文件:%s 不存在", servicePathName));
                return;
            }

            File file = new File(localPath);
            if (file.exists() && !file.isFile()) {
                listener.onFailure(ErrorCode.FTP_LOCATION_SAVE_FILE_EXCEPTION, ErrorCode.FTP_LOCATION_SAVE_FILE_EXCEPTION.getMsg());
                return;
            }
            long saveFileLength = file.length();
            long size = ftpFile.getSize();
//            String key = KeyUtil.download().load(localPath).setFilePath(servicePathName).create();
            makeRootDirectory(localPath);
//            //TODO 这里的if else 可以优化一下
            if (file.exists()) {
                if (saveFileLength >= size) {//已经保存了
                    if (append) {//覆盖原有的
                        file.delete();
                        realDownload(0);
                    } else {
                        //不覆盖
                        listener.onSuccess(2, "文件已经存在");
                        return;
                    }
                } else {//断点下载
                    realDownload(saveFileLength);
                }
            } else {//文件不存在，则是新下载
                realDownload(0);
            }
            //TODO 优化后的
//            if (saveFileLength >= size && !append) {
//                return;
//            }
//            if (append) {
//                saveFileLength = 0;
//            }
//            realDownload(saveFileLength);
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailure(ErrorCode.FTP_DOWNLOAD_EXCEPTION, String.format("下载异常:\n%s", e.toString()));
        } finally {
            ftpDisconnect(ftpClient);
            isFinished = true;
        }
    }

    /**
     * 断开ftp服务器连接
     */
    private void ftpDisconnect(FTPClient ftpClient) {
        // 判断空指针
        if (ftpClient == null) {
            return;
        }
        // 断开ftp服务器连接
        try {
            ftpClient.logout();
            ftpClient.disconnect();
            LoggerEvent.out("断开连接");
        } catch (Exception e) {
            LoggerEvent.out("Error occurred while disconnecting from ftp server.");
        }
    }

    public void realDownload(long restartOffset) {
        LoggerEvent.out( "realDownload: " + restartOffset);
        RandomAccessFile outputStream = null;
        InputStream inputStream = null;
        try {
            String remoteFilePath = new String(servicePathName.getBytes(), StandardCharsets.ISO_8859_1);
            FTPFile ftpFile = ftpClient.listFiles(remoteFilePath)[0];
            if (ftpFile == null) {
                listener.onFailure(ErrorCode.FTP_DOWNLOAD_FILE_NOT_EXIST, String.format("文件:%s 不存在", servicePathName));
                return;
            }

            LoggerEvent.out(servicePathName + " --> " + localPath);
            LoggerEvent.out("文件开始下载");
            listener.onStart(servicePathName);

            makeRootDirectory(localPath);
//            outputStream = new FileOutputStream(localPath);
            outputStream = new RandomAccessFile(localPath, "rwd");
            outputStream.seek(restartOffset);
            ftpClient.setRestartOffset(restartOffset);
            inputStream = ftpClient.retrieveFileStream(remoteFilePath);
            byte[] buffer = new byte[bufferSize];
            int bytesRead = -1;
            long fileSize = ftpFile.getSize();  // 获取文件大小
            long totalBytesRead = restartOffset;  // 已经读取的文件字节数
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                if (isStop.get()) return;
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                int percentCompleted = (int) (((float) totalBytesRead / fileSize) * 100);
                listener.onProgress(percentCompleted);
            }
            if (!isStop.get()) {
                listener.onSuccess(1, "下载成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailure(ErrorCode.FTP_DOWNLOAD_EXCEPTION, String.format("下载异常:\n%s", e.toString()));
        } finally {
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    LoggerEvent.error(e);
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LoggerEvent.error(e);
                }
            }
            ftpDisconnect(ftpClient);
            isFinished = true;
        }
    }

}