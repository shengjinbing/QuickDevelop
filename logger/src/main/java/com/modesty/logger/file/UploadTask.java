package com.modesty.logger.file;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Created by ${lixiang} on 2018/8/20.
 */

class UploadTask implements Runnable {
    private static final int TIMEOUT_CONNECTING = 5000;
    private static final int TIMEOUT_READ = 10000;
    private static final int STATUE_CODE_SUCCESS = 200;
    private static final int STATUE_CODE_DEFAULT = -800;
    private final File mFile;
    private final String mTargetUrl;
    private final Map<Object, Object> mParams;
    private final Callback mCallback;

    public UploadTask(String endpointUrl, File file, Callback callback) {
        this(endpointUrl, file, (Map)null, callback);
    }

    public UploadTask(String url, File file, Map<Object, Object> params, Callback callback) {
        this.mTargetUrl = url;
        this.mFile = file;
        this.mParams = params;
        this.mCallback = callback;
    }

    public void run() {
        if(this.mCallback != null) {
            this.mCallback.onStart();
        }

        String BOUNDARY = UUID.randomUUID().toString();
        String PREFIX = "--";
        String LINE_END = "\r\n";
        String CHARSET = "utf-8";
        HttpURLConnection conn = null;
        OutputStream os = null;
        InputStream is = null;

        try {
            if(!TextUtils.isEmpty(this.mTargetUrl)) {
                URL url = new URL(this.mTargetUrl);
                conn = (HttpURLConnection)url.openConnection();

                SSLSocketFactory foundSSLFactory;
                try {
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init((KeyManager[])null, (TrustManager[])null, (SecureRandom)null);
                    foundSSLFactory = sslContext.getSocketFactory();
                } catch (GeneralSecurityException var24) {
                    foundSSLFactory = null;
                }

                if(null != foundSSLFactory && conn instanceof HttpsURLConnection) {
                    ((HttpsURLConnection)conn).setSSLSocketFactory(foundSSLFactory);
                }

                conn.setConnectTimeout(5000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod(HttpMethod.POST.toString());
                conn.setRequestProperty("Accept", "*/*");
                conn.setRequestProperty("Charset", "utf-8");
                conn.setRequestProperty("Connection", "keep-alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                os = conn.getOutputStream();
                StringBuilder builder = new StringBuilder();
                if(this.mParams != null) {
                    Iterator var11 = this.mParams.entrySet().iterator();

                    while(var11.hasNext()) {
                        Map.Entry<Object, Object> entry = (Map.Entry)var11.next();
                        builder.append("--").append(BOUNDARY).append("\r\n");
                        builder.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + "\r\n");
                        builder.append("Content-Type: text/plain; charset=utf-8\r\n");
                        builder.append("\r\n");
                        builder.append(entry.getValue());
                        builder.append("\r\n");
                    }
                }

                builder.append("--");
                builder.append(BOUNDARY);
                builder.append("\r\n");
                builder.append("Content-Disposition: form-data; name=\"snapshot\"; filename=\"" + this.mFile.getName() + "\"" + "\r\n");
                builder.append("Content-Type: application/octet-stream; charset=utf-8\r\n");
                builder.append("\r\n");
                os.write(builder.toString().getBytes("utf-8"));
                FileInputStream fis = new FileInputStream(this.mFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                byte[] buffer = new byte[2048];

                int bytes;
                while((bytes = bis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytes);
                }

                bis.close();
                fis.close();
                os.write("\r\n".getBytes("utf-8"));
                byte[] end_data = ("--" + BOUNDARY + "--" + "\r\n").getBytes("utf-8");
                os.write(end_data);
                os.flush();
                if(this.isSuccessful(conn.getResponseCode())) {
                    is = conn.getInputStream();
                } else {
                    is = conn.getErrorStream();
                }

                byte[] response = this.slurp(is);
                String json = response == null?null:new String(response, Charset.defaultCharset());
                JSONObject jsonResponse = JSONUtils.stringToJsonObject(json);
                int code = jsonResponse.optInt("status", -800);
                if(code == 200) {
                    if(this.mCallback != null) {
                        this.mCallback.onSuccess(json);
                        return;
                    }
                } else if(this.mCallback != null) {
                    this.mCallback.onFailure();
                    return;
                }

                return;
            }
        } catch (Exception var25) {
            var25.printStackTrace();
            if(this.mCallback != null) {
                this.mCallback.onError(var25);
            }

            return;
        } finally {
            Utils.closeSilently(os);
            Utils.closeSilently(is);
            if(conn != null) {
                conn.disconnect();
                conn = null;
            }

        }

    }

    private boolean isSuccessful(int code) {
        return code >= 200 && code < 300;
    }

    private byte[] slurp(InputStream inputStream) {
        ByteArrayOutputStream buffer = null;

        try {
            byte[] data = new byte[8192];
            buffer = new ByteArrayOutputStream();

            int nRead;
            while((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            byte[] var5 = buffer.toByteArray();
            return var5;
        } catch (Exception var9) {
            var9.printStackTrace();
        } finally {
            Utils.closeSilently(buffer);
        }

        return null;
    }
}

