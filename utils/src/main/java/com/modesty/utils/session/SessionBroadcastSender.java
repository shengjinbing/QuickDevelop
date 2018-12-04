package com.modesty.utils.session;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * @author wangzhiyuan
 */
public class SessionBroadcastSender {

    private final static String TAG = "SessionBroadcastSender";

    final static String ACTION_LOGIN_SUCCESS = "action_login_success";
    final static String ACTION_LOGOUT = "action_logout";

    private SessionBroadcastSender() {
    }

    public static void sendLoginSuccessBroadcast(Context context, Bundle data) {
        Log.d(TAG, "SessionBroadcastSender sendLoginSuccessBroadcast");
        if(context != null) {
            Intent intent = new Intent();
            intent.setAction(ACTION_LOGIN_SUCCESS);
            if(data != null){
                intent.putExtras(data);
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            Log.d(TAG, "SessionBroadcastSender sendLoginSuccessBroadcast done.");
        }
    }

    public static void sendLogoutBroadcast(Context context) {
        Log.d(TAG, "SessionBroadcastSender sendLogoutBroadcast");
        if(context != null) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_LOGOUT));
            Log.d(TAG, "SessionBroadcastSender sendLogoutBroadcast done.");
        }
    }
}
