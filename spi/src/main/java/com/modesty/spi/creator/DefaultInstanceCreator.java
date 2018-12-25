package com.modesty.spi.creator;

import java.lang.reflect.Constructor;

/**
 * Created by lixiang
 * on 2018/12/20
 */
public class DefaultInstanceCreator<T> implements InstanceCreator<T> {
    public DefaultInstanceCreator() {
    }

    @Override
    public T createInstance(Class<T> clazz) throws Exception {
        Constructor<T> defaultConstructor = clazz.getDeclaredConstructor(new Class[0]);
        if(!defaultConstructor.isAccessible()) {
            defaultConstructor.setAccessible(true);
        }

        return defaultConstructor.newInstance(new Object[0]);
    }
}
