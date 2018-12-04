package com.modesty.logger.simplelog;



/**
 * Created by ${lixiang} on 2018/8/20.
 */

public final class Logger {
    private static final String DEFAULT_TAG = "ELEGANT_LOG";
    private static final Printer printer = new LoggerPrinter();

    private Logger() {
    }

    public static Settings init() {
        return printer.init("ELEGANT_LOG", LogLevel.NONE);
    }

    public static Settings init(String tag, LogLevel logLevel) {
        return printer.init(tag, logLevel);
    }

    public static Printer t(String tag) {
        return printer.t(tag, printer.getSettings().getMethodCount());
    }

    public static Printer t(int methodCount) {
        return printer.t((String)null, methodCount);
    }

    public static Printer t(String tag, int methodCount) {
        return printer.t(tag, methodCount);
    }

    public static void d(String message, Object... args) {
        printer.d(message, args);
    }

    public static void e(String message, Object... args) {
        printer.e((Throwable)null, message, args);
    }

    public static void e(Throwable throwable, String message, Object... args) {
        printer.e(throwable, message, args);
    }

    public static void i(String message, Object... args) {
        printer.i(message, args);
    }

    public static void v(String message, Object... args) {
        printer.v(message, args);
    }

    public static void w(String message, Object... args) {
        printer.w(message, args);
    }

    public static void wtf(String message, Object... args) {
        printer.wtf(message, args);
    }

    public static void easyLog(String tag, String message) {
        t(tag).normalLog(message);
    }

    public static void json(String json) {
        printer.json(json);
    }

    public static void xml(String xml) {
        printer.xml(xml);
    }
}

