package com.modesty.logger.base;

import com.modesty.logger.file.FileLogger;
import com.modesty.logger.logcat.LogcatLogger;
import com.modesty.logger.nop.NopLogger;

import java.util.EnumMap;

/**
 * Created by ${lixiang} on 2018/8/20.
 */

public final class LoggerBinder {
    private static final EnumMap<LogType, Class<? extends Logger>> sLoggerMap = new EnumMap(LogType.class);

    private LoggerBinder() {
    }

    public static LoggerBinder getInstance() {
        return LoggerBinder.SingletonHolder.INSTANCE;
    }

    public Logger getLogger(String name) {
        try {
            return (Logger)((Class)sLoggerMap.get(LogSettings.instance().getLogType())).getConstructor(new Class[]{String.class}).newInstance(new Object[]{name});
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    static {
        sLoggerMap.put(LogType.LOGCAT, LogcatLogger.class);
        sLoggerMap.put(LogType.FILE, FileLogger.class);
        sLoggerMap.put(LogType.NOP, NopLogger.class);
    }

    private static final class SingletonHolder {
        private static final LoggerBinder INSTANCE = new LoggerBinder();

        private SingletonHolder() {
        }
    }
}
