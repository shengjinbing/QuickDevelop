package com.modesty.quickdevelop.network.provider;


import com.modesty.quickdevelop.SpUtils;
import com.modesty.quickdevelop.base.BaseApplication;
import com.modesty.quickdevelop.network.interceptor.NormalInterceptor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class OkHttpProvider {

    private final static long DEFAULT_CONNECT_TIMEOUT = 10;
    private final static long DEFAULT_WRITE_TIMEOUT = 30;
    private final static long DEFAULT_READ_TIMEOUT = 30;

    public static OkHttpClient getDefaultOkHttpClient() {
        return new OkHttpClient();
    }


    public static OkHttpClient getOkHttpClient() {
        return getOkHttpClient(new NormalInterceptor());
    }

    /*这里就说到了我们的两种缓存：

    一、无论有无网络我们都去获取缓存的数据（我们会设置一个缓存时间，在某一段时间内（例如60S）
    去获取缓存数据。超过60S我们就去网络重新请求数据）

    二、有网络的时候我们就去直接获取网络上面的数据。当没有网络的时候我们就去缓存获取数据。*/

    private static OkHttpClient getOkHttpClient(Interceptor cacheControl) {
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        //设置超时时间
        httpClientBuilder.connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS);
        //失败后是否重新连接
        httpClientBuilder.retryOnConnectionFailure(true);
        //设置拦截器
        httpClientBuilder.addInterceptor(cacheControl);
        //httpClientBuilder.addNetworkInterceptor(cacheControl);
        //设置缓存
        File httpCacheDirectory = new File(SpUtils.getCacheDirFile(), "OkHttpCache");
        httpClientBuilder.cache(new Cache(httpCacheDirectory, 100 * 1024 * 1024));
        return httpClientBuilder.build();
    }

}
