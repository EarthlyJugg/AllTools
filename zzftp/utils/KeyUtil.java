package com.stardust.sdk.zzftp.utils;

public class KeyUtil {

    public static Build download() {
        return new Build();
    }

    public static class Build {

        private String loadUrl;
        private String saveFilePath;

        public Build load(String downloadUrl) {
            this.loadUrl = downloadUrl;
            return this;
        }


        public Build setFilePath(String saveFilePath) {
            this.saveFilePath = saveFilePath;
            return this;
        }

        public String create() {
            StringBuilder sb = new StringBuilder();
            sb.append(loadUrl);
            sb.append(saveFilePath);
            String md5Str = SecurityUtils.encode(sb.toString());
            return md5Str;
        }

    }

}
