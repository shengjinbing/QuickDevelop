package com.modesty.spi.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.modesty.spi.creator.DefaultInstanceCreator;
import com.modesty.spi.creator.InstanceCreator;
import com.modesty.spi.creator.InstanceCreatorList;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by lixiang
 * on 2018/12/20
 */
final class ServiceLoader<S> implements Iterable<S> {
    private static final String META_INF_SERVICES = "META-INF/services/";
    final Class<S> service;
    final ClassLoader classLoader;
    final Set<String> services;
    private String configPath;
    LoadingStrategy<S> loadingStrategy;
    InstanceCreatorList<S> instanceCreatorList;
    private boolean loaded = false;

    private ServiceLoader(Class<S> service, ClassLoader classLoader) {
        if(service == null) {
            throw new NullPointerException("service == null");
        } else {
            this.service = service;
            this.classLoader = classLoader;
            this.services = new HashSet();
            this.configPath = "META-INF/services/";
            this.loadingStrategy = new DefaultLoadingStrategy();
            this.instanceCreatorList = new InstanceCreatorList(new InstanceCreator[]{new DefaultInstanceCreator()});
        }
    }

    public void setInstanceCreatorList(InstanceCreatorList<S> instanceCreatorList) {
        if(instanceCreatorList == null) {
            this.instanceCreatorList = new InstanceCreatorList(new InstanceCreator[]{new DefaultInstanceCreator()});
        } else {
            this.instanceCreatorList = instanceCreatorList;
        }
    }

    public void setLoadingStrategy(@Nullable LoadingStrategy<S> loadingStrategy) {
        if(loadingStrategy == null) {
            this.loadingStrategy = new DefaultLoadingStrategy();
        } else {
            this.loadingStrategy = loadingStrategy;
        }
    }

    public void setConfigPath(@Nullable String configPath) {
        if(configPath == null) {
            this.configPath = "META-INF/services/";
        } else {
            for(this.configPath = configPath.trim(); this.configPath.startsWith("/"); this.configPath = this.configPath.substring(1)) {
                ;
            }

            if(this.configPath.isEmpty()) {
                this.configPath = "META-INF/services/";
            }

        }
    }

    public void reload() {
        this.internalLoad();
        this.loaded = true;
    }

    private void internalLoad() {
        this.services.clear();
        String name;
        if(this.configPath.endsWith("/")) {
            name = this.configPath + this.service.getName();
        } else {
            name = this.configPath;
        }

        this.services.add(name);
    }

    @Override
    public Iterator<S> iterator() {
        if(!this.loaded) {
            this.reload();
        }

        return new ServiceIterator(this);
    }

    public Iterable<Class<S>> serviceClassIterable() {
        return new Iterable<Class<S>>() {
            @Override
            public Iterator<Class<S>> iterator() {
                return new ServiceClassIterator(ServiceLoader.this);
            }
        };
    }

    public static <S> ServiceLoader<S> load(@NonNull Class<S> service, ClassLoader classLoader) {
        if(classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }

        return new ServiceLoader(service, classLoader);
    }

    public static <S> ServiceLoader<S> load(@NonNull Class<S> service) {
        return load(service, Thread.currentThread().getContextClassLoader());
    }

    public static <S> ServiceLoader<S> loadInstalled(Class<S> service) {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        if(cl != null) {
            while(cl.getParent() != null) {
                cl = cl.getParent();
            }
        }

        return load(service, cl);
    }

    @Override
    public String toString() {
        return "ServiceLoader for " + this.service.getName();
    }
}
