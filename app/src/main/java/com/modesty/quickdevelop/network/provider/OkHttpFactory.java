package com.modesty.quickdevelop.network.provider;

import android.app.Application;

import com.modesty.quickdevelop.base.BaseApplication;
import com.modesty.quickdevelop.network.NetConfig;
import com.modesty.quickdevelop.network.ca.SslHelper;
import com.modesty.quickdevelop.network.dns.HttpDns;
import com.modesty.quickdevelop.network.interceptor.AppInterceptor;
import com.modesty.quickdevelop.network.interceptor.NetworkInterceptor;
import com.modesty.quickdevelop.network.interceptor.RequestLogInterceptor;
import com.modesty.quickdevelop.network.interceptor.ResponseLogInterceptor;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.BitSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * <p>
 * Before getting an global instance of {@link OkHttpClient}, you can use {@link NetConfig} to set some
 * parameters of network. Note that network configuration must be first set before you get an instance of {@link OkHttpClient},
 * for the first time, otherwise configuration is invalid<p/>
 *
 * @author lixiang
 */
public final class OkHttpFactory {

    private static volatile OkHttpClient sInstance;

    private OkHttpFactory() {
    }

    public static OkHttpClient getInstance() {
        if (sInstance == null) {
            synchronized (OkHttpClient.class) {
                if (sInstance == null) {
                    final OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.addInterceptor(new AppInterceptor());
                    builder.addNetworkInterceptor(new NetworkInterceptor());
                    builder.addInterceptor(new RequestLogInterceptor());
                    builder.addInterceptor(new ResponseLogInterceptor());
                    builder.hostnameVerifier(NetConfig.instance().getHostnameVerifier());
                    builder.connectTimeout(NetConfig.instance().getConnectTimeout(), TimeUnit.MILLISECONDS);
                    builder.readTimeout(NetConfig.instance().getReadTimeout(), TimeUnit.MILLISECONDS);
                    builder.writeTimeout(NetConfig.instance().getWriteTimeout(), TimeUnit.MILLISECONDS);
                    //builder.dns(new HttpDns());

                    final Set<Interceptor> interceptors = NetConfig.instance().getInterceptors();
                    for (Interceptor interceptor : interceptors) {
                        builder.addInterceptor(interceptor);
                    }

                    final Set<Interceptor> networkInterceptors = NetConfig.instance().getNetworkInterceptors();
                    for (Interceptor networkInterceptor : networkInterceptors) {
                        builder.addNetworkInterceptor(networkInterceptor);
                    }
                    builder.sslSocketFactory(SslHelper.getSSLSocketFactory(BaseApplication.context),
                            SslHelper.getSystemDefaultTrustManager());
                    //如果你生成证书时包含的域名或者ip地址和你服务器的域名或者ip不匹配时，默认是会报错的，需要使用
                    // hostnameVerifier来放弃验证域名。
                    builder.hostnameVerifier((hostname, session) -> true);

                    sInstance = builder.build();
                }
            }
        }

        return sInstance;
    }
}