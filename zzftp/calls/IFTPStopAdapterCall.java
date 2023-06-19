package com.stardust.sdk.zzftp.calls;

public class IFTPStopAdapterCall implements IFTPStopCall {


    private IFTPStopCall iftpStopCall;

    public IFTPStopAdapterCall(IFTPStopCall iftpStopCall) {
        this.iftpStopCall = iftpStopCall;
    }

    @Override
    public void call(boolean isStop,String msg) {
        if (iftpStopCall != null) {
            iftpStopCall.call(isStop,msg);
        }
    }
}
