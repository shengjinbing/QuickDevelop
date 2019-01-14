package com.modesty.quickdevelop.network.interceptor;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 1.第一种拦截器addNetworkInterceptor，无论有无网络我们都先获取缓存的数据。
 * 2.网络拦截器可以操作重定向和失败重连的返回值
 * 3.根据第一张图，我们可以以看出，这句换的意思是，取缓存中的数据就不会去还行Chain.proceed()
 *   所以就不能执行网络拦截器
 * 4.意思是通过网络拦截器可以观察到所有通过网络传输的数据
 * 5.根据第二张图我们可以看出，请求服务连接的拦截器先于网络拦截器执行，所以在进行网络拦截器执行时，
 *   就可以看到Request中服务器请求连接信息，因为应用拦截器是获取不到对应的连接信息的。
 * Created by Administrator on 2017/6/23 0023.
 */

/*
 要做缓存用的是okhttp的功能，主要利用的是拦截器。这里一定要看清楚okhtt添加拦截器有两种。
 看清楚啊，很多时候这样的小的设置可能然我们浪费一天的时间的。
 有1.addInterceptor ,和2.addNetworkInterceptor这两种。他们的区别简单的说下，不知道也没关系，
 addNetworkInterceptor添加的是网络拦截器，他会在在request和resposne是分别被调用一次，
 addinterceptor添加的是aplication拦截器，他只会在response被调用一次。
*/

public class NetworkInterceptor implements Interceptor {


    /**
     * 一、无论有无网路都添加缓存。
     * 目前的情况是我们这个要addNetworkInterceptor
     * 这样才有效。经过本人测试（chan）测试有效.
     * 60S后如果没有网络将获取不到数据，显示连接失败
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.d("BBBBB", chain.request().toString() + "1111111111222222456");
        Request request = chain.request();
        Response response = chain.proceed(request);
        String cachetime = request.header("Cache-Time");
        int maxAge = 3200 * 24;//这里设置时间是缓存保留时间
        Log.d("BBBBB", cachetime + "9999999999999");
        return response.newBuilder()
                .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, max-age=" + maxAge)
                .build();
    }
}
