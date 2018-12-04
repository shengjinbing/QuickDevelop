package com.modesty.analytics.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author lixiang
 * @since 2017/5/17
 */
public class SharedPrefsMgr {

    private static final String File_Name = "at_shared_file";
    private static SharedPrefsMgr sInstance;
    private static SharedPreferences sSharedPrefs;

    public synchronized static SharedPrefsMgr getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SharedPrefsMgr(context.getApplicationContext());
        }

        return sInstance;
    }

    private SharedPrefsMgr(Context context) {
        sSharedPrefs = context.getSharedPreferences(File_Name, Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sSharedPrefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String tag) {
        return sSharedPrefs.getString(tag, "");
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            return sSharedPrefs.getBoolean(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public long getLong(String key, long defaultValue) {
        try {
            return sSharedPrefs.getLong(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sSharedPrefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void putLong(String key, long value) {
        SharedPreferences.Editor editor = sSharedPrefs.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = sSharedPrefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int defaultValue) {
        try {
            return sSharedPrefs.getInt(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = sSharedPrefs.edit();
        editor.remove(key);
        editor.apply();
    }

}