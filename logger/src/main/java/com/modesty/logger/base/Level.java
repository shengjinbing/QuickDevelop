package com.modesty.logger.base;

/**
 * Created by ${lixiang} on 2018/8/20.
 */


public enum Level {
    OFF(2147483647),
    TRACE(1),
    DEBUG(2),
    INFO(3),
    WARN(4),
    ERROR(5);

    public final int level;

    private Level(int level) {
        this.level = level;
    }
}

