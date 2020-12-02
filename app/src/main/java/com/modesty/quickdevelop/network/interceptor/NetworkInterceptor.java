package com.modesty.quickdevelop.network.interceptor;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * 1.addNetworkInterceptor（）来添加网络拦截器，因为网络拦截器的位子关系，所以可以拦截到request请求信息和resposne响应信息。
 * 2.addInterceptor 因为位子关系（请求还没组装），他只会在response被调用一次
 * Created by Administrator on 2017/6/23 0023.
 */

public class NetworkInterceptor implements Interceptor {


    /**
     * 服务端不支持缓存，可以自己修改response来增加缓存头里面的信息。
     * @param chain
     * @return
     * @throws IOException
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
