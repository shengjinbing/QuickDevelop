package com.modesty.quickdevelop.network.interceptor;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * 1.“Location”字段属于响应字段，必须出现在响应报文里。但只有配合 301/302 状态码才有意义，它标记了服务器要求重定向的 URI，这里就是要求浏览器跳转到“index.html”。
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
        Log.e("Interceptor", "app intercept:begin ");
        Request request = chain.request();
        Response response = chain.proceed(request);//请求
        Log.e("Interceptor", "app head "+response.headers().toString());
        Log.e("Interceptor", "app intercept:end; " + request.url()+", response ," +
                response.request().url()+" code: " + response.code());
        return response;
       /* Request request = chain.request();
        Response response = chain.proceed(request);
        String cachetime = request.header("Cache-Time");
        int maxAge = 3200 * 24;//这里设置时间是缓存保留时间
        Log.d("BBBBB", cachetime + "9999999999999");
        return response.newBuilder()
                .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, max-age=" + maxAge)
                .build();*/
    }
}
