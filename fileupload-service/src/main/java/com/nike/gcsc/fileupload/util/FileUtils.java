package com.nike.gcsc.fileupload.util;

import lombok.NonNull;

/**
 * @author roger yang
 * @date 11/04/2019
 */
public class FileUtils {
    private static final String SPLIT = "/";
    private static String profile;
    
    public static void setProfile(String p) {
        profile = p;
    }
    
    public static String getS3RealFilePath(@NonNull String sys, @NonNull String md5) {
        return sys.concat(SPLIT).concat(profile).concat(SPLIT).concat(md5);
    }
}
