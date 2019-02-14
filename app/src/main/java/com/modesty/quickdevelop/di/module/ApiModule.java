package com.modesty.quickdevelop.di.module;

import android.support.annotation.NonNull;


import com.modesty.quickdevelop.Constants;
import com.modesty.quickdevelop.di.qualifier.ActionUrl;
import com.modesty.quickdevelop.di.qualifier.HomeUrl;
import com.modesty.quickdevelop.network.api.ApiService;
import com.modesty.quickdevelop.network.helper.OkHttpHelper;
import com.modesty.quickdevelop.network.helper.RetrofitHelper;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Administrator on 2017/11/20 0020.
 */
@Module
public class ApiModule {
    //Constants.BASE_URL
    @NonNull
    public Retrofit createRetrofit(Retrofit.Builder builder, OkHttpClient client, String url) {
        return builder
                .client(client)
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

    }


    @Singleton
    @Provides
    public Retrofit.Builder provideRetrofitBuilder() {
        return new Retrofit.Builder();
    }

    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClient() {
        return OkHttpHelper.getInstance().getOkHttpClient();
    }

    @Singleton
    @Provides
    @ActionUrl
    public Retrofit provideActionRetrofit(Retrofit.Builder builder ,OkHttpClient okHttpClient){
        return createRetrofit(builder,okHttpClient, Constants.BASE_URL);
    }


    @Singleton
    @Provides
    public RetrofitHelper provideRetrofitHelper(ApiService ApiService ){
        return new RetrofitHelper(ApiService);
    }

    @Singleton
    @Provides
    public ApiService provideActionService(@ActionUrl Retrofit retrofit){
        return  retrofit.create(ApiService.class);
    }


    @Singleton
    @Provides
    @HomeUrl
    public Retrofit provideHomeRetrofit(Retrofit.Builder builder ,OkHttpClient okHttpClient){
        return createRetrofit(builder,okHttpClient, Constants.BASE_URL);
    }

}
