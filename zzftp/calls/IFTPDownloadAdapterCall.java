package com.stardust.sdk.zzftp.calls;

import com.stardust.sdk.zzftp.ErrorCode;

/**
 * 连接FTF状态回调
 */
public class IFTPDownloadAdapterCall implements IFTPDownloadCall{

    private IFTPDownloadCall iftpDownloadCall;

    public IFTPDownloadAdapterCall(IFTPDownloadCall iftpDownloadCall) {
        this.iftpDownloadCall = iftpDownloadCall;
    }

    @Override
    public void onStart(String downloadPath) {
        if (iftpDownloadCall != null) {
            iftpDownloadCall.onStart(downloadPath);
        }
    }

    @Override
    public void onSuccess(int type, String msg) {
        if (iftpDownloadCall != null) {
            iftpDownloadCall.onSuccess(type, msg);
        }
    }

    @Override
    public void onProgress(int progress) {
        if (iftpDownloadCall != null) {
            iftpDownloadCall.onProgress(progress);
        }
    }

    @Override
    public void onFailure(ErrorCode code, String errorMsg) {
        if (iftpDownloadCall != null) {
            iftpDownloadCall.onFailure(code, errorMsg);
        }
    }
}
