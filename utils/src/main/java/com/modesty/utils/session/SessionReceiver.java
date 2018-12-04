package com.modesty.utils.session;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


/**
 * @author wangzhiyuan
 */
public abstract class SessionReceiver extends BroadcastReceiver {

    private static final String TAG = "SessionReceiver";

    public SessionReceiver() {}

    public static void registerLogoutReceiver(Context context, SessionReceiver receiver) {
        if(checkNull(context, receiver)) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SessionBroadcastSender.ACTION_LOGOUT);
            LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter);
            Log.d(TAG, "SessionReceiver registerLogoutReceiver succeeds");
        }
    }

    public static void registerLoginSuccessReceiver(Context context, SessionReceiver receiver) {
        if(checkNull(context, receiver)) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SessionBroadcastSender.ACTION_LOGIN_SUCCESS);
            LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter);
            Log.d(TAG, "SessionReceiver registerLoginSuccessReceiver succeeds");
        }
    }

    public static void unRegister(Context context, SessionReceiver receiver) {
        if(checkNull(context, receiver)) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
            Log.d(TAG, "SessionReceiver unregister receiver succeeds");
        }
    }

    private static boolean checkNull(Context context, SessionReceiver receiver) {
        return context != null && receiver != null;
    }

    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "SessionReceiver onReceive");
        String action = intent.getAction();
        if(SessionBroadcastSender.ACTION_LOGIN_SUCCESS.equals(action)) {
            this.onNotify(intent.getExtras());
        } else if(SessionBroadcastSender.ACTION_LOGOUT.equals(action)) {
            this.onNotify(null);
        }
    }

    public abstract void onNotify(Bundle bundle);
}
