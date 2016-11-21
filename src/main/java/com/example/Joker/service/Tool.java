package com.example.Joker.service;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by CoderSong on 16/11/21.
 */
public class Tool {

    /**
     * 对字符串进行md5加密
     * @param str
     * @return
     */
    public static String stringToMD5(String str) {
        if (str != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(str.getBytes());
                return new BigInteger(1, md.digest()).toString(16);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
