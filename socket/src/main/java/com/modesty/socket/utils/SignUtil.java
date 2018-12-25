package com.modesty.socket.utils;


import com.modesty.utils.digest.DigestUtils;

/**
 * @author wangzhiyuan
 * @since 2018/6/26
 */

public class SignUtil {
    private static final String MD5_PREFIX = "zhidaoautosocket";

    public static synchronized String createSign(String token) {
        String encryptString = MD5_PREFIX + token;
        return DigestUtils.md5Hex(encryptString);
    }

}
