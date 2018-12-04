package com.modesty.logger.file;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ${lixiang} on 2018/8/20.
 */

class SharedPrefsMgr {
    private static final String File_Name = "logger_shared_pref";
    private static SharedPrefsMgr sInstance;
    private static SharedPreferences sSharedPrefs;

    public static synchronized SharedPrefsMgr getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SharedPrefsMgr(context.getApplicationContext());
        }

        return sInstance;
    }

    private SharedPrefsMgr(Context context) {
        sSharedPrefs = context.getSharedPreferences("logger_shared_pref", 0);
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
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public long getLong(String key, long defaultValue) {
        try {
            return sSharedPrefs.getLong(key, defaultValue);
        } catch (Exception var5) {
            return defaultValue;
        }
    }

    public int getInt(String key, int value) {
        try {
            return sSharedPrefs.getInt(key, value);
        } catch (Exception var4) {
            return value;
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

    public void remove(String key) {
        SharedPreferences.Editor editor = sSharedPrefs.edit();
        editor.remove(key);
        editor.apply();
    }
}
