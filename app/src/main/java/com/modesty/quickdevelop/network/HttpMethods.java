package com.modesty.quickdevelop.network;


import android.content.Context;
import android.support.annotation.NonNull;


import com.modesty.quickdevelop.Constants;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

public class HttpMethods {

    private HttpMethods() {

    }

    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create();
    private static RxJava2CallAdapterFactory rxJavaCallAdapterFactory = RxJava2CallAdapterFactory.create();

    private static HttpMethods httpMethods;

    public static HttpMethods getinstance() {
        if (httpMethods == null) {
            synchronized (HttpMethods.class) {
                if (httpMethods == null) {
                    httpMethods = new HttpMethods();
                }
            }
        }
        return httpMethods;
    }

    @NonNull
    private Retrofit getRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(gsonConverterFactory) //设置数据解析器
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)// 支持RxJava平台
                    .build();

    }
}
