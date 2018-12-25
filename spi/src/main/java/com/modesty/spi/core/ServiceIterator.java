package com.modesty.spi.core;

import com.modesty.spi.creator.InstanceCreatorList;

import java.util.Iterator;
import java.util.ServiceConfigurationError;


/**
 * Created by lixiang
 * on 2018/12/20
 */
final class ServiceIterator<S> implements Iterator<S> {
    private final ServiceClassIterator<S> mServiceClassIterator;
    private final Class<S> service;
    private final InstanceCreatorList<S> instanceCreatorList;

    ServiceIterator(ServiceLoader<S> loader) {
        this.mServiceClassIterator = new ServiceClassIterator(loader);
        this.service = loader.service;
        this.instanceCreatorList = loader.instanceCreatorList;
    }

    @Override
    public boolean hasNext() {
        return this.mServiceClassIterator.hasNext();
    }

    @Override
    public S next() {
        Class aClass = this.mServiceClassIterator.next();

        try {
            return this.service.cast(this.instanceCreatorList.createInstance(aClass));
        } catch (Exception var3) {
            throw new ServiceConfigurationError("Couldn't instantiate class " + aClass.getName(), var3);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
