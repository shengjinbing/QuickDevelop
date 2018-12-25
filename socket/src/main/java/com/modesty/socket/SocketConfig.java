package com.modesty.socket;

import android.content.Context;

import com.modesty.socket.utils.SocketConstants;


/**
 * @author wangzhiyuan
 * @since 2018/6/27
 */

public final class SocketConfig {
    public static final String PROTO_VERSION = "1.0.0";

    private int port = SocketConstants.DEFAULT_PORT;
    private String host = SocketConstants.DEFAULT_HOST;
    private boolean debug = false;
    private int maxLocationCaches = 5;
    private Context appContext;

    private String uid;
    private String phone;
    private String token;

    private SocketConfig() {
    }

    public static SocketConfig instance() {
        return SingletonHolder.INSTANCE;
    }

    private final static class SingletonHolder {
        private static final SocketConfig INSTANCE = new SocketConfig();
    }

    public SocketConfig setInetAddress(String host, int port) {
        this.host = host;
        this.port = port;
        return this;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public SocketConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public SocketConfig setAppContext(Context appContext) {
        this.appContext = appContext.getApplicationContext();
        return this;
    }

    public Context getAppContext() {
        return appContext;
    }

    public int getMaxLocationCaches() {
        return maxLocationCaches;
    }

    public SocketConfig setMaxLocationCaches(int maxLocationCaches) {
        this.maxLocationCaches = maxLocationCaches;
        return this;
    }

    public SocketConfig setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public SocketConfig setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public SocketConfig setToken(String token) {
        this.token = token;
        return this;
    }

    public String getToken() {
        return token;
    }
}
