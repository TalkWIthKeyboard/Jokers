package com.example.Joker.service.tool;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Map;

/**
 * Created by CoderSong on 16/11/21.
 */
public class Tool {

    /**
     * 对字符串进行md5加密，可能会缺少前导0
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
