package com.modesty.logger.base;

/**
 * Created by ${lixiang} on 2018/8/20.
 */

public interface Logger {
    String getName();

    boolean isLoggable(Level var1);

    boolean isTraceEnabled();

    boolean isDebugEnabled();

    boolean isInfoEnabled();

    boolean isWarnEnabled();

    boolean isErrorEnabled();

    void trace(String var1, Throwable var2);

    void trace(String var1, Object... var2);

    void debug(String var1, Throwable var2);

    void debug(String var1, Object... var2);

    void info(String var1, Throwable var2);

    void info(String var1, Object... var2);

    void warn(String var1, Throwable var2);

    void warn(String var1, Object... var2);

    void error(String var1, Throwable var2);

    void error(String var1, Object... var2);
}
