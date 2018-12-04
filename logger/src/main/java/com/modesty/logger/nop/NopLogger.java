package com.modesty.logger.nop;

import com.modesty.logger.base.AbstractLogger;

/**
 * Created by ${lixiang} on 2018/8/20.
 */

public class NopLogger extends AbstractLogger {
    public NopLogger(String name) {
        super(name);
    }

    public NopLogger(Class<?> clazz) {
        super(clazz);
    }

    public void trace(String msg, Throwable t) {
    }

    public void trace(String msg, Object... args) {
    }

    public void debug(String msg, Throwable t) {
    }

    public void debug(String msg, Object... args) {
    }

    public void info(String msg, Throwable t) {
    }

    public void info(String msg, Object... args) {
    }

    public void warn(String msg, Throwable t) {
    }

    public void warn(String msg, Object... args) {
    }

    public void error(String msg, Throwable t) {
    }

    public void error(String msg, Object... args) {
    }
}

