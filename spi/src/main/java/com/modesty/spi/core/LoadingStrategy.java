package com.modesty.spi.core;

import java.io.InputStream;
import java.util.Collection;

/**
 * Created by lixiang
 * on 2018/12/20
 */
public interface LoadingStrategy<S> {
    Collection<Class<S>> load(ClassLoader var1, InputStream var2) throws Exception;
}