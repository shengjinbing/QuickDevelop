package com.modesty.logger.base;

/**
 * Created by ${lixiang} on 2018/8/20.
 */

public final class LoggerFactory {
    private LoggerFactory() {
    }

    public static final Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public static final Logger getLogger(String name) {
        return LoggerBinder.getInstance().getLogger(name);
    }
}