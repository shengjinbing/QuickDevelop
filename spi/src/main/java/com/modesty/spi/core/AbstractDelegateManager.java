package com.modesty.spi.core;

import com.modesty.spi.SpiConfig;
import com.modesty.spi.utils.SpiUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by lixiang
 * on 2018/12/20
 */
public abstract class AbstractDelegateManager<S> {
    private static final String META_INF = "META-INF";
    private static boolean resourcesInitialized;

    private static synchronized void initResources() {
        if (!resourcesInitialized) {
            resourcesInitialized = true;
        }

    }

    protected AbstractDelegateManager() {
        initResources();
    }

    protected void loadDelegates(Class<S> clazz, final Collection<S> collection) {
        loadDelegates(clazz, new AbstractDelegateManager.DelegateListener<S>() {
            @Override
            public void onDelegate(String id, S service) {
                collection.add(service);
            }
        });
    }

    protected void loadDelegates(final Class<S> clazz, final AbstractDelegateManager.DelegateListener<S> listener) {
        loadDelegateClasses(clazz, new AbstractDelegateManager.DelegateListener<Class<? extends S>>() {
            @Override
            public void onDelegate(String id, Class<? extends S> cls) {
                try {
                    S s = SpiUtil.makeInstance(clazz);
                    if (s != null) {
                        listener.onDelegate(id, s);
                    }
                } catch (Exception var4) {
                    var4.printStackTrace();
                }

            }
        });
    }

    protected void loadDelegateClasses(Class<S> clazz, final Collection<Class<? extends S>> collection) {
         loadDelegateClasses(clazz, new AbstractDelegateManager.DelegateListener<Class<? extends S>>() {
            @Override
            public void onDelegate(String id, Class<? extends S> cls) {
                collection.add(cls);
            }
        });
    }

    protected void loadDelegateClasses(Class<S> clazz, AbstractDelegateManager.DelegateListener<Class<? extends S>> listener) {
        ServiceLoader<S> serviceLoader = ServiceLoader.load(clazz, this.getClass().getClassLoader());
        Set<String> bizIds = SpiConfig.getInstance().getBizIds();
        Iterator var5 = bizIds.iterator();

        while (var5.hasNext()) {
            String id = (String) var5.next();
            serviceLoader.setConfigPath(String.format("%s/%s/", new Object[]{"META-INF", id}));
            serviceLoader.reload();
            Iterator var7 = serviceLoader.serviceClassIterable().iterator();

            while (var7.hasNext()) {
                Class<S> cls = (Class) var7.next();
                listener.onDelegate(id, cls);
            }
        }

    }

    public interface DelegateListener<S> {
        void onDelegate(String var1, S var2);
    }
}

