package com.stardust.sdk.zzftp;

public class FTPTaskRequest {
    public String host;
    public String username;
    public String password;
    public String servicePathName;
    public String localPath;
    public boolean append;
    public int bufferSize;

    public FTPTaskRequest(String host,
                          String username,
                          String password,
                          String servicePathName,
                          String localPath,
                          boolean append,
                          int bufferSize) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.servicePathName = servicePathName;
        this.localPath = localPath;
        this.append = append;
        this.bufferSize = bufferSize;
    }
}
