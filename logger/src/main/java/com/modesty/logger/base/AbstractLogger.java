package com.modesty.logger.base;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;



/**
 * Created by ${lixiang} on 2018/8/20.
 */

public abstract class AbstractLogger implements Logger {
    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private final String mName;
    protected Level mLevel = LogSettings.instance().getLevel();

    public AbstractLogger(String name) {
        this.mName = name;
    }

    public AbstractLogger(Class<?> clazz) {
        this.mName = clazz.getName();
    }

    public String getName() {
        return this.mName;
    }

    public boolean isLoggable(Level level) {
        return level.level >= this.mLevel.level;
    }

    public boolean isTraceEnabled() {
        return Level.TRACE.level >= this.mLevel.level;
    }

    public boolean isDebugEnabled() {
        return Level.DEBUG.level >= this.mLevel.level;
    }

    public boolean isInfoEnabled() {
        return Level.INFO.level >= this.mLevel.level;
    }

    public boolean isWarnEnabled() {
        return Level.WARN.level >= this.mLevel.level;
    }

    public boolean isErrorEnabled() {
        return Level.ERROR.level >= this.mLevel.level;
    }
}