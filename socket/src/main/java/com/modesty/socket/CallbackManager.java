package com.modesty.socket;

import android.support.v4.util.ArraySet;


import com.modesty.socket.utils.Utils;

import java.util.Set;

/**
 * @author wangzhiyuan
 * @since 2018/6/25
 */

public final class CallbackManager {
    private final Set<Callback> observers = new ArraySet<>();

    private CallbackManager(){}

    public static CallbackManager getInstance(){
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder {
        private final static CallbackManager INSTANCE = new CallbackManager();
    }

    public synchronized void register(Callback callback){
        if (callback == null) {
            Utils.log("Can not register a callback that is null.");
            return;
        }
        observers.add(callback);
    }

    public synchronized void unregister(Callback callback){
        observers.remove(callback);
    }

    public synchronized void unregisterAll(){
        observers.clear();
    }

    public synchronized void notify(String content){
        for(Callback callback : observers){
            callback.update(this, content);
        }
    }

}
