package com.stardust.sdk.zzftp.calls;

import com.stardust.sdk.zzftp.ErrorCode;

import org.apache.commons.net.ftp.FTPClient;

/**
 * 连接FTF状态回调
 */
public interface IFTPConnectCall {

    void onSuccess(FTPClient ftpClient);

    void onFailure(ErrorCode code, String errorMsg);

}
