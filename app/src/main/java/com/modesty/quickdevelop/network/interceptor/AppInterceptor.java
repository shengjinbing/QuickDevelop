package com.modesty.quickdevelop.network.interceptor;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 两个interceptor都有他们各自的优缺点：
 *
 * Application Interceptors
 *
 * 不需要关心由重定向、重试请求等造成的中间response产物。
 * 总会被调用一次，即使HTTP response是从缓存（cache）中获取到的。
 * 关注原始的request，而不关心注入的headers，比如If-None-Match。
 * interceptor可以被取消调用，不调用Chain.proceed()。
 * interceptor可以重试和多次调用Chain.proceed()。
 * Network Interceptors
 *
 * 可以操作由重定向、重试请求等造成的中间response产物。
 * 如果是从缓存中获取cached responses ，导致中断了network，是不会调用这个interceptor的。
 * 数据在整个network过程中都可以通过Network Interceptors监听。
 * 可以获取携带了request的Connection。
 *
 * 链接：https://www.jianshu.com/p/63051d67c464
 * Created by lixiang on 2021/1/7
 * Describe:
 */
public class AppInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.e("Interceptor", "app intercept:begin ");
        Request request = chain.request();
        Response response = chain.proceed(request);//请求
        Log.e("Interceptor", "app intercept:end; " + request.url()+", response ," +
                response.request().url()+" code: " + response.code());
        return response;
    }
}
