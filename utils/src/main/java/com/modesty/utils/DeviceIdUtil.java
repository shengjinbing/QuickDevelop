package com.modesty.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


import com.modesty.utils.storage.SharedPrefsMgr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class DeviceIdUtil {

    public static final String KEY_DEVICE_ID = "deviceId";

    private DeviceIdUtil() {}

    private static void saveDeviceId(Context context, String deviceId){
        SharedPrefsMgr.getInstance(context).putString(KEY_DEVICE_ID, deviceId);
    }

    public static String getDeviceId(Context context) {
        if(context == null){
            throw new NullPointerException("context must not be null.");
        }

        final Context appContext = context.getApplicationContext();
        String deviceId = SharedPrefsMgr.getInstance( context ).getString( KEY_DEVICE_ID );

        if (TextUtils.isEmpty( deviceId )) {
            deviceId = getDeviceIdInternal(appContext);
            if (!TextUtils.isEmpty(deviceId)) {
               saveDeviceId(appContext,deviceId);
            } else {
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                    if ( ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE ) == PackageManager.PERMISSION_GRANTED ) {
                        deviceId = ((TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE)).getSimSerialNumber();
                    }
                }else{
                    deviceId = ((TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE)).getSimSerialNumber();
                }
                if (!TextUtils.isEmpty(deviceId)) {
                    saveDeviceId(appContext,deviceId);
                } else {
                    deviceId = getDeviceSerial();
                    if (!TextUtils.isEmpty(deviceId) && !deviceId.equalsIgnoreCase("unknown")) {
                        saveDeviceId(appContext,deviceId);
                    } else {
                        deviceId = getAndroidId(appContext);
                        if (!TextUtils.isEmpty(deviceId)) {
                            saveDeviceId(appContext,deviceId);
                        } else {
                            deviceId = String.valueOf(System.currentTimeMillis());
                            saveDeviceId(appContext,deviceId);
                        }
                    }
                }
            }
        }

        return deviceId;
    }

    private static String getDeviceIdInternal(Context context) {
        String id = "";

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            if ( ContextCompat.checkSelfPermission( context,Manifest.permission.READ_PHONE_STATE ) != PackageManager.PERMISSION_GRANTED ) {
                return id;
            }
        }

        TelephonyManager telephonymanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonymanager != null) {
            id = telephonymanager.getDeviceId();
            if (TextUtils.isEmpty(id))
                id = "";
        }
        return id;
    }

    private static String getAndroidId(Context context) {
        String s = "";
        s = Settings.Secure.getString(context.getContentResolver(), "android_id");
        if (TextUtils.isEmpty(s))
            s = "";
        return s;
    }

    private static String getDeviceSerial() {
        String serial = "unknown";
        try {
            Class clazz = Class.forName("android.os.Build");
            Class paraTypes = Class.forName("java.lang.String");
            Method method = clazz.getDeclaredMethod("getString", paraTypes);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            serial = (String) method.invoke(new Build(), "ro.serialno");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return serial;
    }
}