package com.modesty.socket.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.modesty.socket.SocketConfig;


/**
 * @author wangzhiyuan
 */
public final class Utils {

    private Utils() {
    }

    public static String getNetworkTypeName(Context context){
        String typeName = "unknown";
        if(context != null){
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager == null){
                return typeName;
            }
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null){
                typeName = networkInfo.getTypeName();
            }
        }

        return typeName;
    }

    public static int getNetworkType(Context context){
        if(context != null){
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager != null){
                NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
                if(activeNetInfo != null){
                    return activeNetInfo.getType();
                }
            }
        }
        return -1;
    }

    public static boolean isSdkHigherThan18(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static void log(String log){
        if(SocketConfig.instance().isDebug()){
            Log.d(SocketConstants.TAG, log);
        }
    }


    public static String getVersionName(Context context) {
        String appVersion = "";

        try {
            if(context != null){
                String packageName = context.getApplicationInfo().packageName;
                appVersion = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return appVersion;
    }

}
