package com.modesty.quickdevelop.network;

import android.app.Application;
import android.content.Context;
import android.support.v4.util.ArraySet;

import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Interceptor;
import okhttp3.internal.tls.OkHostnameVerifier;

/**
 * <p>
 * This is a configuration class provided for clients.For better extension and customization,client can set customized parameters of network
 * based on their own application. However you should set these parameters before you get the global instance
 * of {@link okhttp3.OkHttpClient} by {@link OkHttpFactory} for the first time.<p/>
 * <p>
 * Some of the parameters have default values, so there is not a must to use this class.<p/>
 *
 * @author wangzhiyuan
 * @since 2018/1/3
 */

public final class NetConfig {
    private long readTimeout = NetConstants.READ_TIMEOUT;
    private long writeTimeout = NetConstants.WRITE_TIMEOUT;
    private long connectTimeout = NetConstants.CONNECT_TIMEOUT;

    private final Set<Interceptor> interceptors = new ArraySet<>();
    private final Set<Interceptor> networkInterceptors = new ArraySet<>();
    private final HostnameVerifier allowAllHostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    private String signaturePrefix = "com.elegant.network";
    private HostnameVerifier hostnameVerifier = OkHostnameVerifier.INSTANCE;
    private Map<String,Object> publicParams;
    private boolean isLoggable;
    private Context appContext;

    private NetConfig(){
    }

    private static final class SingletonHolder{
        private static final NetConfig INSTANCE = new NetConfig();
    }

    public static NetConfig instance(){
        return SingletonHolder.INSTANCE;
    }

    public synchronized long getReadTimeout() {
        return readTimeout;
    }

    public synchronized NetConfig setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public synchronized long getWriteTimeout() {
        return writeTimeout;
    }

    public synchronized NetConfig setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    public synchronized long getConnectTimeout() {
        return connectTimeout;
    }

    public synchronized NetConfig setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public synchronized HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public synchronized NetConfig setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    public synchronized NetConfig setSignaturePrefix(String prefix){
        this.signaturePrefix = prefix;
        return this;
    }

    public synchronized String getSignaturePrefix(){
        return signaturePrefix;
    }

    public synchronized Set<Interceptor> getInterceptors() {
        return interceptors;
    }

    public synchronized NetConfig addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
        return this;
    }

    public synchronized Set<Interceptor> getNetworkInterceptors(){
        return networkInterceptors;
    }

    public synchronized NetConfig addNetworkInterceptor(Interceptor networkInterceptor){
        networkInterceptors.add(networkInterceptor);
        return this;
    }

    public synchronized NetConfig setPublicParams(Map<String,Object> publicParams){
        this.publicParams = publicParams;
        return this;
    }

    public synchronized Map<String,Object> getPublicParams(){
        return publicParams;
    }

    public synchronized boolean isLoggable() {
        return isLoggable;
    }

    public synchronized void setLoggable(boolean loggable) {
        isLoggable = loggable;
    }

    public synchronized NetConfig setAppContext(Context appContext){
        if(appContext instanceof Application){
            this.appContext = appContext;
        }else{
            this.appContext = appContext.getApplicationContext();
        }
        return this;
    }

    public synchronized Context getAppContext(){
        return appContext;
    }
}
