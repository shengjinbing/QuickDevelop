package com.modesty.analytics.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * @author lixiang
 * @since 2017/7/5
 */

public class Utils {
    public static final String TAG = "CommonUtils";

    public static void closeSilently(@Nullable Closeable c) {
        if (c == null) return;
        try {
            c.close();
            c = null;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                return telephonyManager.getDeviceId() == null ? "" : telephonyManager.getDeviceId();
            }
        }
        return null;
    }

    public static String sha1(String decrypt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decrypt.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return decrypt;
    }

    public static int getVersionCode(Context context) {
        try {
            String pkgName = context.getPackageName();
            PackageInfo e = context.getPackageManager().getPackageInfo(pkgName, 0);
            if (e != null) {
                return e.versionCode;
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        return -1;
    }

    public static String getVersionName(Context context) {
        String appVersion = "";

        try {
            String e = context.getApplicationInfo().packageName;
            appVersion = context.getPackageManager().getPackageInfo(e, 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return appVersion;
    }

    public static String getAppName(Context context) {
        PackageManager pm = context.getPackageManager();
        return context.getApplicationInfo().loadLabel(pm).toString();

    }

    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    public static String getResolution(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if(windowManager != null){
            Display display = windowManager.getDefaultDisplay();
            int screenWidth = display.getWidth();
            int screenHeight = display.getHeight();
            return screenHeight + "*" + screenWidth;
        }
        return null;
    }

    public static String getOperator(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String operator = null;
        if (telManager != null) {
            operator = telManager.getSimOperator();
        }

        if (operator != null) {

            if (operator.equals("46000") || operator.equals("46002")
                    || operator.equals("46007")) {

                // 中国移动
                return "中国移动";
            } else if (operator.equals("46001")) {

                // 中国联通
                return "中国联通";
            } else if (operator.equals("46003")) {

                // 中国电信
                return "中国电信";
            }
        }

        return "unknown";
    }

    /**
     * 获取网络类型
     *
     * @return
     */
    public static String getNetworkType(Context context) {
        String name = "unknown";

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
                return "WIFI";
            }
        }

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return name;
        }

        int type = tm.getNetworkType();
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                name = "2G";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                name = "3G";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                name = "4G";
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                name = "unknown";
                break;
            default:
                name = "unknown";
                break;
        }
        return name;
    }

    public static String getMac() {
        String mac_s = "";
        try {
            byte[] mac;
            NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
            mac = ne.getHardwareAddress();
            mac_s = byte2hex(mac);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mac_s;
    }

    private static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
        }

        return "";
    }

    private static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        int len = b.length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs = hs.append("0").append(stmp);
            else {
                hs = hs.append(stmp);
            }
            if (n != len - 1) {
                hs.append(":");
            }
        }
        return String.valueOf(hs);
    }

    /**
     * 检查任意Object是否为空
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
        } else if(obj instanceof JSONObject){
            //检查json
            return ((JSONObject)obj).length() == 0;
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

    /**
     * 网络是否可用
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = null;
        if (cm != null) {
            network = cm.getActiveNetworkInfo();
        }
        if (network != null) {
            return network.isConnectedOrConnecting() || network.isAvailable();
        }
        return false;
    }

    public static JSONArray getInstalledApps(Context context) {
        if (context == null) {
            throw new NullPointerException("Context can not be null.");
        }

        final Context appContext = context.getApplicationContext();
        final PackageManager packageManager = appContext.getPackageManager();
        final JSONArray appInfoList = new JSONArray();

        try {
            final List<PackageInfo> packages = packageManager.getInstalledPackages(0);
            for (int i = 0; i < packages.size(); i++) {
                final PackageInfo packageInfo =  packages.get(i);

                if ((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags) != 0) {
                    continue;
                }

                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("packageName",packageInfo.packageName);
                jsonObject.put("appName",packageInfo.applicationInfo.loadLabel(packageManager).toString());
                jsonObject.put("firstInstallTime",packageInfo.firstInstallTime);
                jsonObject.put("lastUpdateTime",packageInfo.lastUpdateTime);

                appInfoList.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return appInfoList;
    }

}