package com.stardust.sdk.zzftp;

import com.stardust.sdk.zzftp.calls.IFTPConnectCall;
import com.stardust.sdk.zzftp.calls.IFTPDownloadAdapterCall;
import com.stardust.sdk.zzftp.calls.IFTPDownloadCall;
import com.stardust.sdk.zzftp.calls.IFTPStopAdapterCall;
import com.stardust.sdk.zzftp.calls.IFTPStopCall;
import com.stardust.sdk.zzftp.events.logevent.LoggerEvent;
import com.stardust.sdk.zzftp.task.DownloadFileBean;
import com.stardust.sdk.zzftp.thread_pool_manager.ExecutorManager;
import com.stardust.sdk.zzftp.utils.KeyUtil;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FTPManager {
    private static final int port = 21;  //端口
    private static String charSet = "UTF-8";
    public static Map<String, DownloadFileBean> cecheMap = new ConcurrentHashMap<>();

    private FTPManager() {
    }

    private static void ftpConnect(String host, String username, String password, int bufferSize, IFTPConnectCall iftpConnectCall) {
        FTPClient ftpClient = new FTPClient();
        try {
//            FTPZZConfig ftpConfig = FTPZZ.getFTPConfig();
//            if (ftpConfig == null) {
//                if (iftpConnectCall != null) {
//                    iftpConnectCall.onFailure(ErrorCode.FTP_CONFIG_NULL, ErrorCode.FTP_CONFIG_NULL.getMsg());
//                }
//                return;
//            }
//            String host = ftpConfig.getHost();
//            String username = ftpConfig.getUsername();
//            String password = ftpConfig.getPassword();

            //设置超时时间以毫秒为单位使用时，从数据连接读。
            LoggerEvent.out("ftpConnect: 开始连接");
            ftpClient.setDefaultTimeout(10000);
            ftpClient.setConnectTimeout(10000);
            ftpClient.setDataTimeout(60000);  //设置超时时间以毫秒为单位使用时，从数据连接读。
            LoggerEvent.out("connecting to the ftp server " + host + ":" + port);
            //连接到FTP服务器
            ftpClient.connect(host, port);
            ftpClient.login(username, password);
            //取消服务器获取自身Ip地址和提交的host进行匹配，否则当不一致时报出以上异常。
            ftpClient.setRemoteVerificationEnabled(false);
            //是否开启被动模式
            ftpClient.enterLocalPassiveMode();
            //请求使用UTF-8编码,中文支持
            ftpClient.setControlEncoding("utf-8");
            //设置文件类型
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setReceiveBufferSize(bufferSize);
            ftpClient.setBufferSize(bufferSize);

            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                LoggerEvent.out("无法连接到ftp服务器，错误码为：" + reply);
                if (iftpConnectCall != null) {
                    iftpConnectCall.onFailure(ErrorCode.FTP_CONNECT_FAIL, "无法连接到ftp服务器，错误码为：" + reply);
                }
            } else {
                LoggerEvent.out("连接到ftp服务器");
                if (iftpConnectCall != null) {
                    iftpConnectCall.onSuccess(ftpClient);
                }
            }
            LoggerEvent.out("ftpConnect: 连接结束");
        } catch (Exception e) {
            LoggerEvent.error(e);
            if (iftpConnectCall != null) {
                iftpConnectCall.onFailure(ErrorCode.FTP_CONNECT_EXCEPTION, "连接ftp服务器异常：\n" + e.toString());
            }
        }
    }

    public static FTPTaskRequest createTask(String host, String username, String password, String servicePathName, String localPath, int bufferSize) {
        return createTask(host, username, password, servicePathName, localPath, false, bufferSize);
    }

    public static FTPTaskRequest createTask(String host, String username, String password, String servicePathName, String localPath, boolean append, int bufferSize) {
        FTPTaskRequest taskBuild = new FTPTaskRequest(host, username, password, servicePathName, localPath, append, bufferSize);
        return taskBuild;
    }

    public static void load(FTPTaskRequest ftpTaskRequest, IFTPDownloadCall listener) {
        FTPTaskRequest request = ftpTaskRequest;
        if (request == null) {
            if (listener != null) {
                listener.onFailure(ErrorCode.FTP_DOWNLOAD_PARAMETER_EXCEPTION, ErrorCode.FTP_DOWNLOAD_PARAMETER_EXCEPTION.getMsg());
            }
            return;
        }
        downloadFile(
                request.host,
                request.username,
                request.password,
                request.servicePathName,
                request.localPath,
                request.append,
                request.bufferSize,
                listener);
    }

    public static void stop(FTPTaskRequest ftpTaskRequest, IFTPStopCall stopCall) {
        FTPTaskRequest request = ftpTaskRequest;
        IFTPStopAdapterCall adapterCall = new IFTPStopAdapterCall(stopCall);
        if (request == null) {
            adapterCall.call(false, "参数异常");
            return;
        }

        String key = KeyUtil.download().load(request.localPath).setFilePath(request.servicePathName).create();
        LoggerEvent.out("stop: " + key);
        DownloadFileBean downloadFileBean = cecheMap.get(key);
        if (downloadFileBean == null) {
            adapterCall.call(false, "找不到对应任务");
        } else {
            downloadFileBean.setIsStop(true);
            adapterCall.call(true, "任务暂停成功");
            cecheMap.remove(key);
        }

    }

    /**
     * 下载文件
     *
     * @param servicePathName FTP服务器文件目录 *
     * @param localPath       下载后的文件路径 *
     * @param append          是否覆盖本地保存有的文件（）
     */
    private static void downloadFile(String host, String username, String password, String servicePathName
            , String localPath, boolean append, int bufferSize, IFTPDownloadCall listener) {
        ExecutorManager.getInstance().request(() -> {
            LoggerEvent.out("downloadFile: ");
            String key = KeyUtil.download().load(localPath).setFilePath(servicePathName).create();
            DownloadFileBean downloadFileBean = cecheMap.get(key);
            if (downloadFileBean != null) {
                if (listener != null) {
                    listener.onFailure(ErrorCode.FTP_DOWNLOAD_Repeat_FAIL, "任务重复下载");
                }
                return;
            }
            DownloadFileBean iftpConnectCall = new DownloadFileBean(servicePathName, localPath, bufferSize, append, new IFTPDownloadAdapterCall(listener) {
                @Override
                public void onSuccess(int type, String msg) {
                    super.onSuccess(type, msg);
                    cecheMap.remove(key);
                }

                @Override
                public void onFailure(ErrorCode code, String errorMsg) {
                    super.onFailure(code, errorMsg);
                    cecheMap.remove(key);
                }
            });
            LoggerEvent.out("downloadFile: " + key);
            cecheMap.put(key, iftpConnectCall);
            ftpConnect(host, username, password, bufferSize, iftpConnectCall);
        });
    }


}
