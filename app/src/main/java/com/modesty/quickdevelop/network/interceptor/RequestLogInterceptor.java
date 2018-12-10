package com.modesty.quickdevelop.network.interceptor;


import com.modesty.logger.simplelog.Logger;
import com.modesty.quickdevelop.network.NetConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author lixiang
 * @since 2018/7/23
 */

public class RequestLogInterceptor implements Interceptor {
    private static final String TAG = "RequestLogInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        String protocol = Protocol.HTTP_1_1.toString();
        if(chain.connection() != null && chain.connection().protocol() != null){
            protocol = chain.connection().protocol().toString();
        }

        StringBuilder logMsg = new StringBuilder();
        logMsg.append("--> ");
        logMsg.append(protocol).append(", ");
        logMsg.append(request.method()).append(", ");
        logMsg.append("Request Headers: ").append(request.headers()).append("\r\n");
        logMsg.append(request.url()).append("\r\n");
        if(hasRequestBody){
            logMsg.append("Content-Type: ").append(requestBody.contentType()).append(", ");
            logMsg.append("Content-Length: ").append(requestBody.contentLength());
        }
        logMsg.append(" <-- end http request");

        if(NetConfig.instance().isLoggable()){
            Logger.t(TAG).d(logMsg.toString());
        }

        return chain.proceed(request);
    }
}
