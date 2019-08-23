package com.nike.gcsc.auth.utils;

import java.security.MessageDigest;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Md5Utils {

	public static String SECRET_KEY = "Fyh80fgMls29Cu6r";
	
    private static byte[] md5(String s) {
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(s.getBytes("UTF-8"));
            byte[] messageDigest = algorithm.digest();
            return messageDigest;
        } catch (Exception e) {
            log.error("MD5 Error...", e);
        }
        return null;
    }

    private static final String toHex(byte hash[]) {
        if (hash == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer(hash.length * 2);
        int i;

        for (i = 0; i < hash.length; i++) {
            if ((hash[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString(hash[i] & 0xff, 16));
        }
        return buf.toString();
    }

    public static String hash(String s) {
        try {
            return new String(toHex(md5(s)).getBytes("UTF-8"), "UTF-8");
        } catch (Exception e) {
            log.error("not supported charset...{}", e);
            return s;
        }
    }
    
    /**
     * Password according to user name, password, salt encryption
     * @param username 
     * @param password 
     * @param salt 
     * @return
     */
    public static String encryptPassword(String username, String password, String salt) {
        return Md5Utils.hash(username + password + salt);
    }
    
    /**
     * Password according to the password, salt encryption
     * @param password 
     * @param salt 
     * @return
     */
    public static String encryptPassword(String password, String salt) {
        return Md5Utils.hash(password + salt);
    }
    
    
}