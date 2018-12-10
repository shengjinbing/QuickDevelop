package com.modesty.quickdevelop;

import android.support.v4.util.ArrayMap;

import com.modesty.quickdevelop.network.provider.OkHttpProvider;

import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by 李想
 * on 2018/11/26
 */
public class RetrofitFactory {
    private static final Map<String, Retrofit> sRpcServiceMap = new ArrayMap<String, Retrofit>();

    private RetrofitFactory() {
    }

    public static synchronized Retrofit getInstance(String baseUrl) {
        Retrofit target = sRpcServiceMap.get(baseUrl);

        if (target == null) {
            target = new Retrofit.Builder()
                    .client(OkHttpProvider.getDefaultOkHttpClient())
                    .baseUrl(baseUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            sRpcServiceMap.put(baseUrl, target);
        }

        return target;
    }
}
