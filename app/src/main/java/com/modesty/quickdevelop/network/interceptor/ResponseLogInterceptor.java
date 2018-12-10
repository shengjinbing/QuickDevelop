package com.modesty.quickdevelop.network.interceptor;


import com.modesty.logger.simplelog.Logger;
import com.modesty.quickdevelop.network.NetConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 1.响应体只能被使用一次；
 2.响应体必须关闭：值得注意的是，在下载文件等场景下，当你以 response.body().byteStream() 形式获取输入流时，
 务必通过 Response.close() 来手动关闭响应体。
 3.获取响应体数据的方法：使用 bytes() 或 string() 将整个响应读入内存；或者使用 source(), byteStream(),
 charStream() 方法以流的形式传输数据。
 4.以下方法会触发关闭响应体：
 Response.close()
 Response.body().close()
 Response.body().source().close()
 Response.body().charStream().close()
 Response.body().byteString().close()
 Response.body().bytes()
 Response.body().string()
 ---------------------
 *
 * @author lixiang
 * @since 2018/7/23
 */

public class ResponseLogInterceptor implements Interceptor {
    private static final String TAG = "ResponseLogInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        long startTime = System.nanoTime();
        Response response = chain.proceed(chain.request());
        long endTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

        ResponseBody responseBody = response.body();
        String responseContent = null;
        String bodySize = null;
        MediaType contentType = null;
        boolean consumedResponse = false;

        StringBuilder logMsg = new StringBuilder();

        if (responseBody != null) {
            long contentLength = responseBody.contentLength();
            bodySize = contentLength != -1L ? contentLength + "-byte" : "unknown-length";
            contentType = responseBody.contentType();
            //responseBody.string()调用一次后就会关掉数据流
            responseContent = responseBody.string();
            consumedResponse = true;
        }

        logMsg.append("--> ");
        logMsg.append(response.code()).append(" ");
        logMsg.append(response.message()).append(" ");
        logMsg.append(response.protocol()).append(" ");
        logMsg.append(response.request().url()).append("\r\n");
        logMsg.append("Response Content: ").append(responseContent).append("\r\n");
        logMsg.append("Content-Type: ").append(contentType).append(", ");
        logMsg.append("Content-Length: ").append(bodySize).append(", ");
        logMsg.append(" (").append(endTime).append("ms)");
        logMsg.append(" <-- end http response");

        if(NetConfig.instance().isLoggable()){
            Logger.t(TAG).d(logMsg.toString());
        }

        return consumedResponse ?
                response.newBuilder().body(ResponseBody.create(contentType, responseContent)).build() :
                response;
    }
}