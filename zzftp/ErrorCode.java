package com.stardust.sdk.zzftp;

public enum ErrorCode {

    FTP_CONFIG_NULL(-999, "FTPZZ未配置FTPZZConifg"),
    FTP_CONNECT_FAIL(-1, "FTP连接失败"),
    FTP_CONNECT_EXCEPTION(-2, "FTP连接异常"),
    FTP_DOWNLOAD_FILE_NOT_EXIST(-3, "FTP下载文件不存在"),
    FTP_DOWNLOAD_FAIL(-4, "FTP下载失败"),
    FTP_DOWNLOAD_EXCEPTION(-5, "FTP下载异常"),
    FTP_LOCATION_SAVE_FILE_EXCEPTION(-6, "保存到本地的文件异常"),
    FTP_DOWNLOAD_PARAMETER_EXCEPTION(-7, "请求参数异常"),
    FTP_DOWNLOAD_Repeat_FAIL(-8, "FTP任务重复下载"),
    ;

    private int code;
    private String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
