package com.stardust.sdk.zzftp.config;

import android.content.Context;

import com.stardust.sdk.zzftp.utils.ApplicationUtil;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class FTPZZ {

    private static FTPZZConfig _SConfig = null;
    private static Context _context;
    private static AtomicBoolean _isInit = new AtomicBoolean(false);

    private FTPZZ() {

    }

    public static void init(Context context, FTPZZConfig config) {
        _context = context.getApplicationContext();
        _SConfig = config;
        if (_isInit.compareAndSet(false, true)) {
            initData();
        }
    }

    private static void initData() {
        if (_context == null) {
            _context = ApplicationUtil.getApplicationContext();
        }
        if (_context == null) return;
        File filesDir = _context.getFilesDir();
        File file = new File(filesDir, "zzftps");
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static FTPZZConfig getFTPConfig() {
        return _SConfig;
    }
}
