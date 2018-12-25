package com.modesty.socket.utils;

/**
 * @author wangzhiyuan
 * @since 2018/6/27
 */

public final class LoginStatusUtil {

    private static volatile boolean loginStatus;

    private LoginStatusUtil(){}

    public synchronized static void setStatus(boolean loginStatus){
        LoginStatusUtil.loginStatus = loginStatus;
    }

    public static boolean isLogin(){
        return loginStatus;
    }

}
