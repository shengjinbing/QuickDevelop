package com.modesty.socket.location;

import android.support.v4.util.ArraySet;


import com.modesty.socket.SocketConfig;
import com.modesty.socket.client.ClientSocketManager;
import com.modesty.socket.model.CollectServerProtobuf;
import com.modesty.socket.utils.RequestUtil;
import com.modesty.socket.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangzhiyuan
 * @since 2018/6/25
 */

public final class LocationUploadManager {
    private LocationChangedListener locationChangedListener;

    private LocationUploadManager() {
    }

    public static LocationUploadManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final LocationUploadManager INSTANCE = new LocationUploadManager();
    }

    public synchronized void startUploadAtFixedRate() {
        cancel();
        if(locationChangedListener == null){
            locationChangedListener = new LocationChangedListenerImpl();
        }
        LocationServiceProvider locationServiceProvider = LocationDelegateManager.getInstance().getLocationServiceProvider();
        if(locationServiceProvider != null){
            locationServiceProvider.addLocationChangedListener(locationChangedListener);
            Utils.log("location callback is added");
        }
    }

    public synchronized void cancel() {
        LocationServiceProvider locationServiceProvider = LocationDelegateManager.getInstance().getLocationServiceProvider();
        if(locationServiceProvider != null){
            locationServiceProvider.removeLocationChangedListener(locationChangedListener);
            Utils.log("location callback is cancelled");
        }
    }

    private static final class LocationChangedListenerImpl implements LocationChangedListener {
        private final AtomicInteger atomicInteger = new AtomicInteger(0);
        private final Set<Location> locationSet = new ArraySet<>();
        LocationChangedListenerImpl(){
        }
        @Override
        public void onLocationChanged(Location newLocation) {
            if(newLocation == null){
                Utils.log("onLocationChanged--->new location is null");
                return;
            }
            locationSet.add(newLocation);
            int maxCacheSize = atomicInteger.incrementAndGet();
            if(maxCacheSize >= SocketConfig.instance().getMaxLocationCaches()){
                List<CollectServerProtobuf.CoordinateRequest> coordinateRequests = new ArrayList<>();
                for(Location location : locationSet){
                    CollectServerProtobuf.CoordinateRequest coordinateRequest = RequestUtil.RequestModelUtil.buildCoordinate(location);
                    coordinateRequests.add(coordinateRequest);
                }
                RequestUtil.sendCoordinates(ClientSocketManager.getInstance().getChannel(), coordinateRequests, newLocation);
                Utils.log("Coordinates are " + Arrays.toString(locationSet.toArray()));
                locationSet.clear();
                atomicInteger.set(0);
            }
        }
    }
}
