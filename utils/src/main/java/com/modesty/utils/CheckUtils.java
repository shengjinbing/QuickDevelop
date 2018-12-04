package com.modesty.utils;

import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangzhiyuan
 * @since 2017/8/11
 */

public class CheckUtils {

    /**
     * 检查任意Object是否为空
     * <hr>
     * shallow check : 不会检查容器内部的元素是否为空
     */
    public static boolean isEmpty(Object obj) {

        if (obj == null) {
            return true;
        }

        if (obj instanceof Collection<?>) {
            // 检查各种Collection是否为空(List,Queue,Set)
            return ((Collection<?>) obj).isEmpty();
        } else if (obj instanceof Map<?, ?>) {
            // 检查各种Map
            return ((Map<?, ?>) obj).isEmpty();
        } else if (obj instanceof CharSequence) {
            // 检查各种CharSequence
            return ((CharSequence) obj).length() == 0;
        } else if (obj.getClass().isArray()) {
            // 检查各种base array
            // return Array.getLength(obj) == 0;
            if (obj instanceof Object[]) {
                return ((Object[]) obj).length == 0;
            } else if (obj instanceof int[]) {
                return ((int[]) obj).length == 0;
            } else if (obj instanceof long[]) {
                return ((long[]) obj).length == 0;
            } else if (obj instanceof short[]) {
                return ((short[]) obj).length == 0;
            } else if (obj instanceof double[]) {
                return ((double[]) obj).length == 0;
            } else if (obj instanceof float[]) {
                return ((float[]) obj).length == 0;
            } else if (obj instanceof boolean[]) {
                return ((boolean[]) obj).length == 0;
            } else if (obj instanceof char[]) {
                return ((char[]) obj).length == 0;
            } else if (obj instanceof byte[]) {
                return ((byte[]) obj).length == 0;
            }
        }

        return false;
    }

    public static boolean isExist(Object obj) {
        return !isEmpty(obj);
    }

    public static boolean isContainsEmpty(Object... objs) {
        if (isEmpty(objs)) {
            return true;
        }
        for (Object obj : objs) {
            if (isEmpty(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否为奇数
     */
    public static boolean isOdd(int i) {
        return i % 2 != 0;
    }

    /**
     * 是否为偶数
     */
    public static boolean isEven(int i) {
        return i % 2 == 0;
    }

    /**
     * 检查枚举组中是否包含指定枚举
     */
    public static boolean isContainsEnum(@Nullable Enum<?>[] group, Enum<?> child) {

        if (isEmpty(group)) {
            return false;
        }

        for (Enum<?> enums : group) {
            if (enums == child) {
                return true;
            }
        }
        return false;
    }

    /**
     * 快速点击事件
     */
    private static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        long delayTime = 500L;
        if (0L < timeD && timeD < delayTime) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }

    /**
     * 网络是否可用
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = null;
        if (cm != null) {
            network = cm.getActiveNetworkInfo();
        }
        return network != null && network.isAvailable() && network.isConnected();

    }

    /**
     * 检查gps开关是否已打开
     */
    public  static boolean isGpsOpenStatus(Context context) {
        String gps = Settings.System.getString(context.getContentResolver(), Settings.System.LOCATION_PROVIDERS_ALLOWED);
        return !(TextUtils.isEmpty(gps) || !gps.contains("gps"));
    }

    /**
     * 检查是否模拟位置
     */
    public static boolean isOPenMockLocation(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0;
    }

    /**当前点击点是否在视图中*/
    public static boolean isInsideView(MotionEvent event, View view) {
        if (view != null && event != null) {
            float eventX = event.getRawX();
            float eventY = event.getRawY();

            int[] contentArray = new int[2];

            Rect contentRect = new Rect();
            view.getLocationOnScreen(contentArray);
            view.getDrawingRect(contentRect);
            contentRect.offsetTo(contentArray[0], contentArray[1]);

            return contentRect.contains((int) eventX, (int) eventY);
        }

        return false;
    }

    /**
     * 判断扫描内容是否为url格式
     */
    public static boolean isUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        String regex = "http(s)?://.*";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = pattern.matcher(url);
        return m.matches();
    }
}
