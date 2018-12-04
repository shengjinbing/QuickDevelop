package com.modesty.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

/**
 * @author wangzhiyuan
 */

public class ResourcesHelper {

    public static Resources getResources(Context context){
        return context.getResources();
    }

    public static int getColor(Context context, int rid) {
        return getResources(context).getColor(rid);
    }

    public static ColorStateList getColorStateList(Context context, int rid) {
        return getResources(context).getColorStateList(rid);
    }

    public static String getString(Context context, int rid) {
        return getResources(context).getString(rid);
    }

    public static String getString(Context context, int rid, int param1, int param2) {
        return getResources(context).getString(rid, param1, param2);
    }

    public static String getString(Context context, int rid, String str) {
        return getResources(context).getString(rid, str);
    }

    public static String[] getStringArray(Context context, int rid) {
        return getResources(context).getStringArray(rid);
    }

    public static Drawable getDrawable(Context context, int rid) {
        return getResources(context).getDrawable(rid);
    }

    public static float getDimension(Context context, int rid) {
        return getResources(context).getDimension(rid);
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return getResources(context).getDisplayMetrics();
    }

    public static int getDisplayMetrics(Context context, int x) {
        return getResources(context).getDimensionPixelSize(x);
    }

    public static int getDimensionPixelSize(Context context, int x) {
        return getResources(context).getDimensionPixelSize(x);
    }

    public static int getInteger(Context context,int rid) {
        return getResources(context).getInteger(rid);
    }

    public static XmlResourceParser getXml(Context context, int rid) {
        return getResources(context).getXml(rid);
    }

    public static Configuration getConfiguration(Context context) {
        return getResources(context).getConfiguration();
    }
}
