package com.stardust.sdk.zzftp.task;

import com.stardust.sdk.zzftp.ErrorCode;
import com.stardust.sdk.zzftp.calls.IFTPDownloadCall;
import com.stardust.sdk.zzftp.events.logevent.LoggerEvent;
import com.stardust.sdk.zzftp.utils.KeyUtil;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DownloadTask implements Runnable {
    private static final long SINLE_THREAD_MAX_FILE_LENGTH = 1;

    private final String servicePathName;
    private final String localPath;
    private final int bufferSize;
    private final IFTPDownloadCall listener;
    private final boolean append;
    private final FTPClient ftpClient;
    private final FTPFile ftpFile;

    public DownloadTask(FTPClient ftpClient, FTPFile ftpFile, boolean append, String servicePathName, String localPath, int bufferSize, IFTPDownloadCall listener) {
        this.servicePathName = servicePathName;
        this.localPath = localPath;
        this.ftpFile = ftpFile;
        this.bufferSize = bufferSize;
        this.listener = listener;
        this.append = append;
        this.ftpClient = ftpClient;
    }

    @Override
    public void run() {
        long size = ftpFile.getSize();
        String key = KeyUtil.download().load(localPath).setFilePath(servicePathName).create();

    }

    private void realDownload() {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            String remoteFilePath = new String(servicePathName.getBytes(), StandardCharsets.ISO_8859_1);
            FTPFile ftpFile = ftpClient.listFiles(remoteFilePath)[0];
            if (ftpFile == null) {
                if (listener != null) {
                    listener.onFailure(ErrorCode.FTP_DOWNLOAD_FILE_NOT_EXIST, String.format("文件:%s 不存在", servicePathName));
                }
                return;
            }

            String key = KeyUtil.download().load(localPath).setFilePath(servicePathName).create();
            File file = new File(localPath);
            long saveFileLength = 0;
            if (file.exists() && file.isFile()) {
                saveFileLength = file.length();
            }
            long size = ftpFile.getSize();

            LoggerEvent.out(servicePathName + " --> " + localPath);
            LoggerEvent.out("文件开始下载");
            if (listener != null) {
                listener.onStart(servicePathName);
            }

            outputStream = new FileOutputStream(localPath);
            inputStream = ftpClient.retrieveFileStream(remoteFilePath);
            byte[] buffer = new byte[bufferSize];
            int bytesRead = -1;
            long fileSize = ftpFile.getSize();  // 获取文件大小
            long totalBytesRead = 0;  // 已经读取的文件字节数

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                int percentCompleted = (int) (((float) totalBytesRead / fileSize) * 100);
                if (listener != null) {
                    listener.onProgress(percentCompleted);
                }
            }
//            if (listener != null) {
//                listener.onSuccess();
//            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFailure(ErrorCode.FTP_DOWNLOAD_EXCEPTION, String.format("下载异常:\n%s", e.toString()));
            }
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

        }
    }
}
