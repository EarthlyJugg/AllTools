package com.stardust.sdk.zzftp.utils;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.atomic.AtomicReference;

public class ApplicationUtil {
    private static final AtomicReference<Context> applicationCache = new AtomicReference<>();
    private static Application mApplication;

    public static Context getApplicationContext() {
        Application application;
        Context context = applicationCache.get();
        if (context == null) {
            if (mApplication == null) {
                try {
                    application = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication", new Class[0]).invoke(null, new Object[0]);
                } catch (Exception unused) {
                    try {
                        application = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication", new Class[0]).invoke(null, new Object[0]);
                    } catch (Exception unused2) {
                        application = null;
                    }
                }
                mApplication = application;
            }
            Context applicationContext = mApplication.getApplicationContext();
            applicationCache.set(applicationContext);
            return applicationContext;
        }
        return context;
    }
}