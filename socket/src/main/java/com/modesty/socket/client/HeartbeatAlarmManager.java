package com.modesty.socket.client;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.modesty.socket.SocketConfig;
import com.modesty.socket.utils.RequestUtil;
import com.modesty.socket.utils.SocketConstants;
import com.modesty.socket.utils.Utils;


/**
 * Send heartbeats according to network type to keep socket active as well as
 * to stop a device falling asleep when the screen is off.
 *
 * @author wangzhiyuan
 * @since 2018/6/25
 */

final class HeartbeatAlarmManager {
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private static int lastNetworkType;

    private HeartbeatAlarmManager() {
    }

    public static HeartbeatAlarmManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder {
        private static final HeartbeatAlarmManager INSTANCE = new HeartbeatAlarmManager();
    }

    synchronized void cancel() {
        if (alarmManager != null && pendingIntent != null) {
            pendingIntent.cancel();
            alarmManager.cancel(pendingIntent);
            alarmManager = null;
            pendingIntent = null;

            Utils.log("heartbeat alarm is cancelled");
        }
    }

    synchronized void send() {
        this.cancel();
        if ((lastNetworkType = Utils.getNetworkType(SocketConfig.instance().getAppContext())) == ConnectivityManager.TYPE_MOBILE) {
            sendHeartbeatRepeatedly(SocketConstants.ONE_MINUTE * 2);
        } else {
            sendHeartbeatRepeatedly(SocketConstants.ONE_MINUTE * 2);
        }
    }

    /**
     * Suggest that interval be 60 seconds or bigger to save power,
     * and note that the interval will be set to 60 seconds when the interval is
     * small than 60 seconds in or above 5.1{@link android.os.Build.VERSION_CODES#LOLLIPOP_MR1}.
     */
    private void sendHeartbeatRepeatedly(long interval) {
        final Context appContext = SocketConfig.instance().getAppContext();
        if(appContext == null){
            Utils.log("sendHeartbeatRepeatedly---> app context is null.");
            return;
        }
        final int requestCode = 1001;
        final Intent intent = new Intent(appContext, SendHeartbeatReceiver.class);
        alarmManager = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(
                appContext,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if(Utils.isSdkHigherThan18()){
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + interval,
                    pendingIntent);

            Utils.log("setExact(...)");
        }
        else{
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + interval,
                    interval,
                    pendingIntent);

            Utils.log("setRepeating(...)");
        }
    }

    public static final class SendHeartbeatReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(!ClientSocketManager.getInstance().isConnected()){
                Utils.log("onReceive--->channel is not connected");
                return;
            }

            Utils.log("onReceive--->send heartbeat");
            RequestUtil.sendHeartbeat(ClientSocketManager.getInstance().getChannel());

            if (Utils.isSdkHigherThan18() || lastNetworkType != Utils.getNetworkType(SocketConfig.instance().getAppContext())) {
                HeartbeatAlarmManager.getInstance().cancel();
                HeartbeatAlarmManager.getInstance().send();
            }
        }
    }
}
