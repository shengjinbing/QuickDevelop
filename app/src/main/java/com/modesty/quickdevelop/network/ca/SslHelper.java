package com.modesty.quickdevelop.network.ca;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by lixiang on 2021/3/11
 * Describe:7.0验证方式 https://mp.weixin.qq.com/s/_g5Mmy0KmgCnpsQfw34-oQ
 */
public class SslHelper {
    /**
     * 获取SSLSocketFactory
     * @param context
     * @return
     */
    public static SSLSocketFactory getSSLSocketFactory(Context context) {
        InputStream is = null;
        SSLSocketFactory sslSocketFactory = null;
        try {
            // is = context.getResources().openRawResource(R.raw.sng_certificate);
            sslSocketFactory = getSslContext(is).getSocketFactory();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sslSocketFactory;
    }

    /**
     * 获取X509TrustManager
     * @return
     */
    public static X509TrustManager getSystemDefaultTrustManager() {
        TrustManagerFactory trustManagerFactory = null;
        try {
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }

    private static SSLContext getSslContext(InputStream certificateIs) throws GeneralSecurityException {
        //从证书文件中读入证书
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(certificateIs);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("certificate can not be empty");
        }

        //将读取的证书放入KeyStore, 密码可以为任意值
        char[] password = "ss007".toCharArray();
        KeyStore keyStore = getEmptyKeyStore(password);
        int index = 0;
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }

        // 使用 keyStore去构建一个X509信任管理器
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        //构建信任的证书管理器构建SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, null);
        return sslContext;
    }

    //获取一个空keystore
    private static KeyStore getEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());//此处使用.crt文件，所以使用getDefaultType即可
            InputStream in = null; //'null' creates an empty key store.
            keyStore.load(in, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
