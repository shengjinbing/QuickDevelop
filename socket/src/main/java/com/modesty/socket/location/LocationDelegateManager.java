package com.modesty.socket.location;

import android.util.Log;

import com.modesty.spi.core.AbstractDelegateManager;
import com.modesty.spi.utils.SpiUtil;


/**
 * @author wangzhiyuan
 * @since 2018/6/27
 */

public class LocationDelegateManager extends AbstractDelegateManager<LocationServiceProvider>
        implements AbstractDelegateManager.DelegateListener<Class<? extends LocationServiceProvider>> {
    private static final String TAG = "LocationDelegateManager";

    private LocationServiceProvider locationServiceProvider;
    private volatile boolean loaded = false;

    public static LocationDelegateManager getInstance(){
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder{
        private static final LocationDelegateManager INSTANCE = new LocationDelegateManager();
    }

    private LocationDelegateManager(){
        loadDelegates();
    }

    public void loadDelegates() {
        if (!loaded) {
            loadDelegateClasses(LocationServiceProvider.class, this);
            loaded = true;
        }
    }

    public void unloadDelegates() {
        if (loaded) {
            if(locationServiceProvider != null){
                Log.d(TAG, String.format("注销 LocationServiceProvider -> %s", locationServiceProvider.getClass().getName()));
                locationServiceProvider = null;
            }
            loaded = false;
        }
    }

    @Override
    public void onDelegate(String id, Class<? extends LocationServiceProvider> service) {
        try {
            this.locationServiceProvider = SpiUtil.makeInstance(service);
            Log.d(TAG, String.format("Biz id is [%s], 注册 LocationServiceProvider -> %s ", id, service.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized LocationServiceProvider getLocationServiceProvider(){
        return locationServiceProvider;
    }

    public void update(Location location){
        if(locationServiceProvider != null){
            locationServiceProvider.update(location);
        }
    }
}
