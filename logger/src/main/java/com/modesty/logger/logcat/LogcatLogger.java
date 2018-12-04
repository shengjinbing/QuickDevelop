package com.modesty.logger.logcat;


import android.text.TextUtils;
import android.util.Log;

import com.modesty.logger.base.AbstractLogger;
import com.modesty.logger.base.Level;

import java.util.Locale;

/**
 * Created by ${lixiang} on 2018/8/20.
 */

public class LogcatLogger extends AbstractLogger {
    private static final int LOG_SEG_MAX_LENGTH = 200;
    private static final String LOG_START_FLAG = "==========Long log start==========";
    private static final String LOG_END_FLAG = "==========Long log end==========";

    public LogcatLogger(String name) {
        super(name);
    }

    public void trace(String msg, Throwable t) {
        if(this.isTraceEnabled()) {
            this.logWithoutTruncation(Level.TRACE, msg, t);
        }

    }

    public void trace(String msg, Object... args) {
        if(this.isTraceEnabled()) {
            this.logWithoutTruncation(Level.TRACE, this.formatMsg(msg, args));
        }

    }

    public void debug(String msg, Throwable t) {
        if(this.isDebugEnabled()) {
            this.logWithoutTruncation(Level.DEBUG, msg, t);
        }

    }

    public void debug(String msg, Object... args) {
        if(this.isDebugEnabled()) {
            this.logWithoutTruncation(Level.DEBUG, this.formatMsg(msg, args));
        }

    }

    public void info(String msg, Throwable t) {
        if(this.isInfoEnabled()) {
            this.logWithoutTruncation(Level.INFO, msg, t);
        }

    }

    public void info(String msg, Object... args) {
        if(this.isInfoEnabled()) {
            this.logWithoutTruncation(Level.INFO, this.formatMsg(msg, args));
        }

    }

    public void warn(String msg, Throwable t) {
        if(this.isWarnEnabled()) {
            this.logWithoutTruncation(Level.WARN, msg, t);
        }

    }

    public void warn(String msg, Object... args) {
        if(this.isWarnEnabled()) {
            this.logWithoutTruncation(Level.WARN, this.formatMsg(msg, args));
        }

    }

    public void error(String msg, Throwable t) {
        if(this.isErrorEnabled()) {
            this.logWithoutTruncation(Level.ERROR, msg, t);
        }

    }

    public void error(String msg, Object... args) {
        if(this.isErrorEnabled()) {
            this.logWithoutTruncation(Level.ERROR, this.formatMsg(msg, args));
        }

    }

    private String formatMsg(String msg, Object... args) {
        return null != args && args.length > 0?String.format(Locale.getDefault(), msg, args):msg;
    }

    private void logWithoutTruncation(Level level, String logContent, Throwable tr) {
        this.logWithoutTruncation(level, logContent + '\n' + Log.getStackTraceString(tr));
    }

    private void logWithoutTruncation(Level level, String logContent) {
        if(!TextUtils.isEmpty(logContent)) {
            long length = (long)logContent.length();
            if(length <= 200L) {
                this.logcatPrint(level, logContent);
            } else {
                this.logcatPrint(level, "==========Long log start==========");
                int offset = 0;
                int logLength = logContent.length();

                while(logLength - offset > 200) {
                    String logSeg = logContent.substring(offset, offset + 200);
                    offset += 200;
                    this.logcatPrint(level, logSeg);
                }

                this.logcatPrint(level, logContent.substring(offset));
                this.logcatPrint(level, "==========Long log end==========");
            }

        }
    }

    private void logcatPrint(Level level, String msg) {
        /*switch(null.$SwitchMap$com$elegant$log$base$Level[level.ordinal()]) {
            case 1:
                Log.v(this.getName(), msg);
                break;
            case 2:
                Log.d(this.getName(), msg);
                break;
            case 3:
                Log.i(this.getName(), msg);
                break;
            case 4:
                Log.w(this.getName(), msg);
                break;
            case 5:
                Log.e(this.getName(), msg);
                break;
            default:
                Log.d(this.getName(), msg);
        }*/

    }
}

