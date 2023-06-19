package com.stardust.sdk.zzftp.config;

public class FTPZZConfig {

    private String host;
    private String username;
    private String password;

    public FTPZZConfig() {

    }

    public FTPZZConfig(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static class Build {
        private String host;
        private String username;
        private String password;

        public Build() {
        }

        public Build setHost(String host) {
            this.host = host;
            return this;
        }

        public Build setUsername(String username) {
            this.username = username;
            return this;
        }

        public Build setPassword(String password) {
            this.password = password;
            return this;
        }

        public FTPZZConfig build() {
            return new FTPZZConfig(host, username, password);
        }
    }
}
