package com.modesty.spi.core;

import com.modesty.spi.SpiConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import java.util.Set;

/**
 * Created by lixiang
 * on 2018/12/20
 */
final class ServiceClassIterator<S> implements Iterator<Class<S>> {
    private final ClassLoader classLoader;
    private final Set<String> services;
    private final LoadingStrategy<S> loadingStrategy;
    private final LinkedList<Class<S>> queue = new LinkedList();
    private boolean hasRead = false;

    ServiceClassIterator(ServiceLoader<S> loader) {
        this.classLoader = loader.classLoader;
        this.services = loader.services;
        this.loadingStrategy = loader.loadingStrategy;
    }

    @Override
    public boolean hasNext() {
        if(!this.hasRead) {
            this.readClass();
        }

        return !this.queue.isEmpty();
    }

    @Override
    public Class<S> next() {
        if(!this.hasNext()) {
            throw new NoSuchElementException();
        } else {
            return (Class)this.queue.remove();
        }
    }

    private void readClass() {
        try {
            Set<Class<S>> set = new LinkedHashSet();
            Iterator var2 = this.services.iterator();

            while(var2.hasNext()) {
                String fileName = (String)var2.next();
                InputStream inputStream = null;

                try {
                    inputStream = SpiConfig.getInstance().getAppContext().getAssets().open(fileName);
                    Collection<Class<S>> classes = this.loadingStrategy.load(this.classLoader, inputStream);
                    if(classes != null && !classes.isEmpty()) {
                        set.addAll(classes);
                    }
                } catch (Exception var14) {
                    var14.printStackTrace();
                } finally {
                    this.closeQuietly(inputStream);
                }
            }

            this.queue.addAll(set);
        } finally {
            this.hasRead = true;
        }

    }

    private void closeQuietly(InputStream inputStream) {
        if(inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException var3) {
                ;
            }
        }

    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
