package com.modesty.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    private static String mMacSerial = null;
    private static String mCPUSerial = null;
    private static boolean isMacSerialNoObtained = false;
    private static boolean isCPUSerialNoObtained = false;
    private static final Pattern VERSION_NAME_PATTERN = Pattern.compile("(\\d+\\.\\d+\\.\\d+)\\-*.*");


    public static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "android_id");
    }

    public static String getCPUSerialno() {
        if (!TextUtils.isEmpty(mCPUSerial)) {
            return mCPUSerial;
        } else if (isCPUSerialNoObtained) {
            mCPUSerial = "";
            return mCPUSerial;
        } else {
            String str = "";

            InputStreamReader ir = null;
            LineNumberReader input = null;
            try {
                isCPUSerialNoObtained = true;
                Process ex = Runtime.getRuntime().exec("cat /proc/cpuinfo");
                if (ex == null) {
                    return null;
                }

                ir = new InputStreamReader(ex.getInputStream());
                input = new LineNumberReader(ir);

                while (null != str) {
                    str = input.readLine();
                    if (str != null) {
                        mCPUSerial = str.trim();
                        break;
                    }
                }
            } catch (IOException var4) {
                var4.printStackTrace();
            } finally {
                if (ir != null) {
                    try {
                        ir.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return mCPUSerial;
        }
    }

    public static int getVersionCode(Context context) {
        String pkgName = context.getPackageName();

        try {
            PackageInfo e = context.getPackageManager().getPackageInfo(pkgName, 0);
            if (e != null) {
                return e.versionCode;
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        return 1;
    }


    public static String getMacSerialno() {
        if (!TextUtils.isEmpty(mMacSerial)) {
            return mMacSerial;
        } else if (isMacSerialNoObtained) {
            mMacSerial = "";
            return mMacSerial;
        } else {
            String str = "";

            InputStreamReader ir = null;
            LineNumberReader input = null;
            try {
                isMacSerialNoObtained = true;
                Process ex = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
                if (ex == null) {
                    return null;
                }

                ir = new InputStreamReader(ex.getInputStream());
                input = new LineNumberReader(ir);

                while (null != str) {
                    str = input.readLine();
                    if (str != null) {
                        mMacSerial = str.trim();
                        break;
                    }
                }
            } catch (IOException var4) {
                var4.printStackTrace();
            } finally {
                if (ir != null) {
                    try {
                        ir.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return mMacSerial;
        }
    }

    /**
     * 获取网络类型
     *
     * @return
     */
    public static String getNetworkType(Context context) {
        String name = "UNKNOWN";

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
                name = "UNKNOWN";
                break;
            default:
                name = "UNKNOWN";
                break;
        }
        return name;
    }

    /**
     * 得到手机的IMEI号
     *
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            return telephonyManager.getDeviceId();
        }
        return null;
    }

    /**
     * 得到手机的IMSI号
     *
     * @return
     */
    public static String getIMSI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            return telephonyManager.getSubscriberId();
        }
        return null;
    }

    public static String checkSimState(Context context) {
        String mString = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = 0;
        if (telephonyManager != null) {
            simState = telephonyManager.getSimState();
        }

        switch (simState) {

            case TelephonyManager.SIM_STATE_ABSENT:
                mString = "无卡";
                // do something
                break;

            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                mString = "需要NetworkPIN解锁";
                // do something

                break;

            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                mString = "需要PIN解锁";
                // do something
                break;

            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                mString = "需要PUN解锁";
                // do something
                break;

            case TelephonyManager.SIM_STATE_READY:
                mString = "良好";
                // do something
                break;

            case TelephonyManager.SIM_STATE_UNKNOWN:
                mString = "未知状态";
                // do something
                break;
        }
        return mString;
    }


    /**
     * 获取路由器Mac
     */
    public static String getRouterMac(Context context) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null && wifi.getConnectionInfo() != null) {
            return wifi.getConnectionInfo().getBSSID();
        }
        return "";
    }

    /**
     * 获取wifi名字
     */
    public static String getWifiName(Context context) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null && wifi.getConnectionInfo() != null) {
            return wifi.getConnectionInfo().getSSID();
        }
        return "";
    }


    public static String getMobileIP(Context ctx) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);// 获取系统的连接服务
        // 实例化mActiveNetInfo对象
        NetworkInfo mActiveNetInfo = null;// 获取网络连接的信息
        if (mConnectivityManager != null) {
            mActiveNetInfo = mConnectivityManager.getActiveNetworkInfo();
        }
        if (mActiveNetInfo == null) {
            return "";
        } else {
            return getIp(mActiveNetInfo);
        }
    }

    // 显示IP信息
    private static String getIp(NetworkInfo mActiveNetInfo) {
        // 如果是WIFI网络
        if (mActiveNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return getLocalIPAddress();
        }
        // 如果是手机网络
        else if (mActiveNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return getLocalIPAddress();
        } else {
            return "";
        }

    }

    // 获取本地IP函数
    private static String getLocalIPAddress() {
        try {
            Enumeration<NetworkInterface> mEnumeration = NetworkInterface.getNetworkInterfaces();
            if (mEnumeration != null) {
                while (mEnumeration.hasMoreElements()) {
                    NetworkInterface intf = mEnumeration.nextElement();
                    if (intf != null && intf.getInetAddresses() != null) {
                        Enumeration<InetAddress> enumIPAddr = intf.getInetAddresses();
                        while (enumIPAddr.hasMoreElements()) {
                            InetAddress inetAddress = enumIPAddr.nextElement();
                            // 如果不是回环地址
                            if (inetAddress != null && !inetAddress.isLoopbackAddress()) {
                                // 直接返回本地IP地址
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static String getVersionName(Context context) {
        return getVersionName(context,true);
    }

    public static String getVersionName(Context context,boolean fullVersionName) {
        String appVersion = "";

        try {
            String packageName = context.getApplicationInfo().packageName;
            appVersion = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
            if (!fullVersionName && appVersion != null && appVersion.length() > 0) {
                Matcher matcher = VERSION_NAME_PATTERN.matcher(appVersion);
                if (matcher.matches()) {
                    appVersion = matcher.group(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return appVersion;
    }

    public static String getAppName(Context context) {
        PackageManager pm = context.getPackageManager();
        return context.getApplicationInfo().loadLabel(pm).toString();

    }

    public static String getModel() {
        String temp = Build.MODEL;
        return TextUtils.isEmpty(temp)?"":temp;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static long getLeftMemory(Context context) {
        if (Build.VERSION.SDK_INT >= 16) {
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            mActivityManager.getMemoryInfo(mi);
            return (mi.totalMem - mi.availMem) / 1000;
        }
        return -1;
    }


    public static String encode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String decode(String string) {
        try {
            return URLDecoder.decode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

}
