package com.modesty.spi.utils;

import java.lang.reflect.Constructor;

/**
 * Created by lixiang
 * on 2018/12/20
 */
public class SpiUtil {
    public SpiUtil() {
    }

    public static <T> T makeInstance(Class<T> clazz, Class[] parameterTypes, Object... args) throws Exception {
        Constructor constructor = clazz.getDeclaredConstructor(parameterTypes);
        constructor.setAccessible(true);
        return (T) constructor.newInstance(args);
    }

    public static <T> T makeInstance(Class<T> clazz) throws Exception {
        Constructor constructor = clazz.getDeclaredConstructor(new Class[0]);
        constructor.setAccessible(true);
        return (T) constructor.newInstance(new Object[0]);
    }
}
