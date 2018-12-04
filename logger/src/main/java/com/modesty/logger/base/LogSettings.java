package com.modesty.logger.base;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * Created by ${lixiang} on 2018/8/20.
 */

public final class LogSettings {
    private Level mLevel;
    private LogType mLogType;
    private Context mAppContext;
    private String mLogUploadUrl;

    private LogSettings() {
        this.mLevel = Level.INFO;
        this.mLogType = LogType.LOGCAT;
    }

    public static LogSettings instance() {
        return LogSettings.SingletonHolder.INSTANCE;
    }

    public Level getLevel() {
        return this.mLevel;
    }

    public LogSettings level(Level level) {
        this.mLevel = level;
        return this;
    }

    public LogType getLogType() {
        return this.mLogType;
    }

    public LogSettings logType(LogType logType) {
        this.mLogType = logType;
        return this;
    }

    public Context getContext() {
        return this.mAppContext;
    }

    public LogSettings context(Context context) {
        this.mAppContext = context.getApplicationContext();
        return this;
    }

    public String getLogUploadUrl() {
        return this.mLogUploadUrl;
    }

    public LogSettings logUploadUrl(String url) {
        this.mLogUploadUrl = url;
        return this;
    }

    private static final class SingletonHolder {
        @SuppressLint({"StaticFieldLeak"})
        private static final LogSettings INSTANCE = new LogSettings();

        private SingletonHolder() {
        }
    }
}

