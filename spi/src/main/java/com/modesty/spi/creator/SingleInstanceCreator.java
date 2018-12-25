package com.modesty.spi.creator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by lixiang
 * on 2018/12/20
 */
public class SingleInstanceCreator<T> implements InstanceCreator<T> {
    public SingleInstanceCreator() {
    }

    @Override
    public T createInstance(Class<T> clazz) throws Exception {
        Method getInstanceMethod = clazz.getDeclaredMethod("getInstance", new Class[0]);
        if(Modifier.isStatic(getInstanceMethod.getModifiers())) {
            if(!getInstanceMethod.isAccessible()) {
                getInstanceMethod.setAccessible(true);
            }

            return (T) getInstanceMethod.invoke((Object)null, new Object[0]);
        } else {
            throw new NoSuchMethodException("static getInstance() not found!");
        }
    }
}