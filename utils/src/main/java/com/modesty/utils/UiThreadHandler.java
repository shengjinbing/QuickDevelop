package com.modesty.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * @author wangzhiyuan
 */

public class UiThreadHandler {

    private static Handler sUiHandler = new Handler(Looper.getMainLooper());

    private static Object sToken = new Object();

    public UiThreadHandler() {
    }

    public static boolean post(Runnable r) {
        return sUiHandler != null && sUiHandler.post(r);
    }

    public static boolean postDelayed(Runnable r, long delayMillis) {
        return sUiHandler != null && sUiHandler.postDelayed(r, delayMillis);
    }

    public static Handler getsUiHandler() {
        return sUiHandler;
    }

    public static boolean postOnceDelayed(Runnable r, long delayMillis) {
        if(sUiHandler == null) {
            return false;
        } else {
            sUiHandler.removeCallbacks(r, sToken);
            return sUiHandler.postDelayed(r, delayMillis);
        }
    }

    public static void removeCallbacks(Runnable runnable) {
        if(sUiHandler != null) {
            sUiHandler.removeCallbacks(runnable);
        }
    }
}