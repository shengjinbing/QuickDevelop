package com.modesty.quickdevelop.network.helper;

/**
 * 描述:okHttp 帮助类
 */

import android.content.Context;
import android.text.TextUtils;
import com.modesty.quickdevelop.SpUtils;
import com.modesty.quickdevelop.base.BaseApplication;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * 全局统一使用的OkHttpClient工具，okhttp版本：okhttp3
 */
public class OkHttpHelper {
    //读取时间
    private static final long DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000;
    //写入时间
    private static final long DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000;
    //超时时间
    private static final long DEFAULT_CONNECT_TIMEOUT_MILLIS = 20 * 1000;
    //最大缓存
    private static final long HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 20 * 1024 * 1024;//设置20M
    //长缓存有效期为7天
    private static final int CACHE_STALE_LONG = 60 * 60 * 24 * 7;

    private static volatile OkHttpHelper sInstance;

    private OkHttpClient mOkHttpClient;
    private Context mContext = BaseApplication.getAppContext();

    private OkHttpHelper() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        //包含header、body数据
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                //.cache(getCache(mContext))//设置缓存
                // 失败重发
                //.retryOnConnectionFailure(true)
                //设置缓存
                //.addNetworkInterceptor()
                //.addInterceptor()
                //FaceBook 网络调试器，可在Chrome调试网络请求，查看SharePreferences,数据库等
                //.addNetworkInterceptor(new StethoInterceptor())
                //http数据log，日志中打印出HTTP请求&响应数据
                .addInterceptor(loggingInterceptor)
                //便于查看json
                // .addInterceptor(new LoggerInterceptor())
                .addInterceptor(new TokenInterceptor())
                .build();
    }

    public static OkHttpHelper getInstance() {
        if (sInstance == null) {
            synchronized (OkHttpHelper.class) {
                if (sInstance == null) {
                    sInstance = new OkHttpHelper();
                }
            }
        }
        return sInstance;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 设置缓存路径
     *
     * @param context 上下文
     */
    public void setCache(Context context) {
        final File baseDir = context.getApplicationContext().getCacheDir();
        if (baseDir != null) {
            final File cacheDir = new File(baseDir, "CopyCache");
            mOkHttpClient.newBuilder().cache((new Cache(cacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE)));
        }
    }


    private class TokenInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            String token = SpUtils.getString(mContext, "login", "token");
            Request build;
            Request request = chain.request();
            if (TextUtils.isEmpty(token)) {
                build = request.newBuilder()
                        .build();
            } else {
                build = request.newBuilder()
                        .addHeader("Authorization", "APP:" + token)
                        .build();
            }

            return chain.proceed(build);
        }
    }
}
