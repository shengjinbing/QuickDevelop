package com.modesty.analytics.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * @author lixiang
 * @since 2017/8/11
 */
public class ScreenReceiverManager {
    private ScreenReceiver mScreenReceiver;

    private ScreenReceiverManager(){
    }

    public static ScreenReceiverManager instance(){
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder{
        private static final ScreenReceiverManager INSTANCE = new ScreenReceiverManager();
    }

    public boolean isScreenOn() {
        return mScreenReceiver == null || mScreenReceiver.isScreenOn;
    }

    public void registerScreenReceiver(Context context) {
        if(mScreenReceiver == null){
            mScreenReceiver = new ScreenReceiver();
        }
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);

        context.registerReceiver(mScreenReceiver, intentFilter);
    }

    public void unregisterScreenReceiver(Context appContext) {
        if (mScreenReceiver != null) {
            appContext.unregisterReceiver(mScreenReceiver);
            mScreenReceiver = null;
        }
    }

    private class ScreenReceiver extends BroadcastReceiver{
        private static final String TAG = "ScreenReceiver";
        private volatile boolean isScreenOn = true;

        @Override
        public void onReceive(final Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                isScreenOn = false;
                Logger.i(TAG, "Screen is off.");
            } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                isScreenOn = true;
                Logger.i(TAG, "Screen is on.");
            }
        }
    }
}