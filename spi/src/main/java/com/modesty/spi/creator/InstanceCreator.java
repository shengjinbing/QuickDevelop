package com.modesty.spi.creator;

/**
 * Created by lixiang
 * on 2018/12/20
 */
public interface InstanceCreator<T> {
    T createInstance(Class<T> var1) throws Exception;
}
