package com.modesty.logger.file;

import android.text.TextUtils;

import com.modesty.logger.base.AbstractLogger;
import com.modesty.logger.base.Level;

import java.util.Date;
import java.util.Locale;

/**
 * Created by ${lixiang} on 2018/8/20.
 */

public class FileLogger extends AbstractLogger {
    public FileLogger(String name) {
        super(name);
    }

    public FileLogger(Class<?> clazz) {
        super(clazz);
    }

    public void trace(String msg, Throwable t) {
        if(this.isTraceEnabled()) {
            this.println(Level.TRACE, msg);
        }

    }

    public void trace(String msg, Object... args) {
        if(this.isTraceEnabled()) {
            this.printf(Level.TRACE, msg, args);
        }

    }

    public void debug(String msg, Throwable t) {
        if(this.isDebugEnabled()) {
            this.println(Level.DEBUG, msg);
        }

    }

    public void debug(String msg, Object... args) {
        if(this.isDebugEnabled()) {
            this.printf(Level.DEBUG, msg, args);
        }

    }

    public void info(String msg, Throwable t) {
        if(this.isInfoEnabled()) {
            this.println(Level.INFO, msg);
        }

    }

    public void info(String msg, Object... args) {
        if(this.isInfoEnabled()) {
            this.printf(Level.INFO, msg, args);
        }

    }

    public void warn(String msg, Throwable t) {
        if(this.isWarnEnabled()) {
            this.println(Level.WARN, msg);
        }

    }

    public void warn(String msg, Object... args) {
        if(this.isWarnEnabled()) {
            this.printf(Level.WARN, msg, args);
        }

    }

    public void error(String msg, Throwable t) {
        if(this.isErrorEnabled()) {
            this.println(Level.ERROR, msg);
        }

    }

    public void error(String msg, Object... args) {
        if(this.isErrorEnabled()) {
            this.printf(Level.ERROR, msg, args);
        }

    }

    private void printf(Level level, String msg, Object... args) {
        String fmtMsg = null != args && args.length > 0?String.format(Locale.getDefault(), msg, args):msg;
        if(!TextUtils.isEmpty(fmtMsg)) {
            this.println(level, fmtMsg);
        }

    }

    private void println(Level level, String s) {
        if(!TextUtils.isEmpty(s)) {
            StringBuilder builder = new StringBuilder();
            builder.append(DATE_FORMAT.format(new Date()));
            builder.append(" ");
            builder.append("[").append(Thread.currentThread().getName()).append("]");
            builder.append(" ");
            builder.append(level.name());
            builder.append(" ");
            builder.append(this.getName());
            builder.append(" - ");
            builder.append(s);
            FileLoggerExecutor.getInstance().enqueue(builder.toString());
        }
    }
}

