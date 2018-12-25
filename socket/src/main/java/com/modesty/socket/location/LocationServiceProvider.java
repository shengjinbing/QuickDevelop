package com.modesty.socket.location;

import android.support.annotation.Keep;
import android.support.v4.util.ArraySet;

import java.util.Set;

/**
 * @author wangzhiyuan
 * @since 2018/4/10
 */

@Keep
public abstract class LocationServiceProvider {
    private final Set<LocationChangedListener> mLocationChangedListeners = new ArraySet<>();

    public void addLocationChangedListener(LocationChangedListener locationChangedListener){
        mLocationChangedListeners.add(locationChangedListener);
    }

    public void removeLocationChangedListener(LocationChangedListener locationChangedListener){
        mLocationChangedListeners.remove(locationChangedListener);
    }

    protected void notify(Location location){
        for(LocationChangedListener listener : mLocationChangedListeners){
            listener.onLocationChanged(location);
        }
    }

    public abstract double getLat();
    public abstract double getLng();
    public abstract float getBearing();
    public abstract double getAltitude();
    public abstract float getAccuracy();
    public abstract String getAddress();
    public abstract String getProvider();
    public abstract String getAdCode();
    public abstract String getCityCode();
    public abstract int getLocType();
    public abstract float getSpeed();
    public abstract void update(Location location);
}
