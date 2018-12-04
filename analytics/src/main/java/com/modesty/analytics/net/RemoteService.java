package com.modesty.analytics.net;

import android.content.Context;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

/**
 * @author lixiang
 * @since 2018/5/18
 */
public interface RemoteService {

    boolean isOnline(Context context);

    byte[] performRequest(
            HttpMethod method,
            String endpointUrl,
            Map<String, Object> params,
            SSLSocketFactory socketFactory);

}
