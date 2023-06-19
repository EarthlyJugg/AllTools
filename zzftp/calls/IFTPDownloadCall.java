package com.stardust.sdk.zzftp.calls;

import com.stardust.sdk.zzftp.ErrorCode;

/**
 * 连接FTF状态回调
 */
public interface IFTPDownloadCall {


    /**
     * 开始下载
     */
    void onStart(String downloadPath);

    /**
     * 下载成功回调
     * @param type 1为下载成功，2为文件已经存在，3为任务暂停
     */
    void onSuccess(int type,String msg);

    /**
     * 下载进度 0-100（代表百分比）
     * @param progress
     */
    void onProgress(int progress);

    /**
     * 下载失败
     *
     */
    void onFailure(ErrorCode code, String errorMsg);

}
