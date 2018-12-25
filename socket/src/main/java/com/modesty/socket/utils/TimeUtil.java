package com.modesty.socket.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wangzhiyuan
 */
public final class TimeUtil {

    private static final SimpleDateFormat Year_Month_Day_Hour_Minute = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat Year_Month_Day = new SimpleDateFormat("yyyy-MM-dd");

    private TimeUtil() {}

    /**
     * long time to string, format is {@link #Year_Month_Day_Hour_Minute}
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, Year_Month_Day_Hour_Minute);
    }

    /**
     * long time to string
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * get current time in milliseconds
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #Year_Month_Day_Hour_Minute}
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    public static long convertDateToMs(String date) {
        return convertDateToMs(date, Year_Month_Day_Hour_Minute);
    }

    public static long convertDateToMs(String date, String dateFormat) {
        return convertDateToMs(date, new SimpleDateFormat(dateFormat));
    }

    public static long convertDateToMs(String date, SimpleDateFormat dateFormat) {
        long defaultTime = 0;

        if (TextUtils.isEmpty(date)) {
            return defaultTime;
        }

        try {
            return dateFormat.parse(date).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultTime;
    }

}
