package com.modesty.quickdevelop.network.exception;

import java.io.IOException;

import okhttp3.Interceptor;

/**
 * An exception occurs whenever network is not reachable, it's threw by {@link NetworkMonitorInterceptor}
 * during {@link NetworkMonitorInterceptor#intercept(Interceptor.Chain)} method.
 *
 * @author lixiang
 * @since 2018/8/14
 */

public class NetworkUnavailableException extends IOException {

    public NetworkUnavailableException(String message) {
        super(message);
    }

}
