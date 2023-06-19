package com.stardust.sdk.zzftp.events.logevent;

import android.util.Log;

public class LoggerEvent {

    public static void out(String msg) {
        Log.d("LoggerEvent_log", "out: "+msg);
    }
    public static void error(Throwable throwable){

    }


}
