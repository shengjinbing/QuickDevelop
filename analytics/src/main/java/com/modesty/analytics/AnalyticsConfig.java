package com.modesty.analytics;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;


import com.modesty.analytics.utils.Logger;
import com.modesty.analytics.utils.TrackerConstants;

import java.security.GeneralSecurityException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;


/**
 * Stores global configuration options for the library. You can enable and disable configuration
 * options using meta-data or by calling mutator methods for some often used settings.
 * Some settings are optional, and default to reasonable recommended values. Some settings are necessary
 * such as app key.
 *
 * @author lixiang
 * @since 2018/5/18
 */
public class AnalyticsConfig {
    private static final String LOGTAG = "AnalyticsConfig";

    public static final String VERSION = BuildConfig.VERSION_NAME;

    public static final int DEBUG_FLUSH_INTERVAL = 3 * 1000;
    public static final int DEBUG_UPLOAD_LIMIT = 1;

    public static final int MODE_DEBUG = 0;
    public static final int MODE_INSTANT_UPLOAD = 1;
    public static final int MODE_CACHE_UPLOAD = 2;

    private final int mBulkUploadLimit;
    private final int mFlushInterval;
    private final long mDataExpiration;
    private final int mMinimumDatabaseLimit;
    private final boolean mDisableFallback;
    private final boolean mDisableAppOpenEvent;
    private final String mEventsEndpoint;
    private final String mEventsFallbackEndpoint;
    private final String mResourcePackageName;

    // Mutable, with synchronized accessor and mutator
    private SSLSocketFactory mSSLSocketFactory;
    private int mMode;
    private String mChannel;
    private String mAppKey;
    private boolean mShouldLog;

    private static volatile AnalyticsConfig sInstance;
    private static final Object sInstanceLock = new Object();

    public static AnalyticsConfig getInstance(Context context) {
        if (sInstance == null) {
            synchronized (sInstanceLock) {
                if (null == sInstance) {
                    final Context appContext = context.getApplicationContext();
                    sInstance = readConfig(appContext);
                }
            }
        }
        return sInstance;
    }

    public synchronized void setChannel(String channel){
        this.mChannel = channel;
    }

    public synchronized String getChannel(){
        return mChannel;
    }

    public synchronized String getAppKey(){
        if(!TextUtils.isEmpty(mAppKey)){
            return mAppKey;
        }
        throw new IllegalStateException("app key must NOT be NULL, please set appkey through android manifest " +
                "or Analytics.getInstance(context).setAppKey(String appKey)");
    }

    public synchronized void setAppKey(String appKey){
        this.mAppKey = appKey;
    }

    public synchronized int getMode(){
        return mMode;
    }

    public synchronized void setMode(int mode){
        this.mMode = mode;
    }

    public synchronized void shouldLog(boolean shouldLog){
        this.mShouldLog = shouldLog;
        Logger.setLevel(shouldLog ? Logger.VERBOSE : Logger.WARN);
    }

    public synchronized void setSSLSocketFactory(SSLSocketFactory factory) {
        mSSLSocketFactory = factory;
    }

    public synchronized SSLSocketFactory getSSLSocketFactory() {
        return mSSLSocketFactory;
    }

    private AnalyticsConfig(Bundle metaData, Context context) {
        SSLSocketFactory foundSSLFactory;
        try {
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            foundSSLFactory = sslContext.getSocketFactory();
        } catch (final GeneralSecurityException e) {
            Logger.i(LOGTAG, "System has no SSL support. Built-in events editor will not be available", e);
            foundSSLFactory = null;
        }
        mSSLSocketFactory = foundSSLFactory;

        mBulkUploadLimit = metaData.getInt("com.elegant.analytics.AnalyticsConfig.BulkUploadLimit", 30); // 30 records default
        mFlushInterval = metaData.getInt("com.elegant.analytics.AnalyticsConfig.FlushInterval", 60 * 1000); // 60s default
        mDataExpiration = metaData.getLong("com.elegant.analytics.AnalyticsConfig.DataExpiration", 1000 * 60 * 60 * 24 * 180L);
        mMinimumDatabaseLimit = metaData.getInt("com.elegant.analytics.AnalyticsConfig.MinimumDatabaseLimit", 10 * 1024 * 1024); // 10 Mb
        mDisableFallback = metaData.getBoolean("com.elegant.analytics.AnalyticsConfig.DisableFallback", true);
        mResourcePackageName = metaData.getString("com.elegant.analytics.AnalyticsConfig.ResourcePackageName"); // default is null
        mDisableAppOpenEvent = metaData.getBoolean("com.elegant.analytics.AnalyticsConfig.DisableAppOpenEvent", true);
        mMode = metaData.getInt("com.elegant.analytics.AnalyticsConfig.Mode", MODE_CACHE_UPLOAD);;
        mChannel = metaData.getString("com.elegant.analytics.AnalyticsConfig.Channel", TrackerConstants.CHANNEL_DEFAULT);
        mAppKey = metaData.getString("com.elegant.analytics.AnalyticsConfig.AppKey", "");

        mShouldLog = metaData.getBoolean("com.elegant.analytics.AnalyticsConfig.Log", false);
        shouldLog(mShouldLog);

        String eventsEndpoint = metaData.getString("com.elegant.analytics.AnalyticsConfig.EventsEndpoint");
        if (null == eventsEndpoint) {
            eventsEndpoint = "https://bdlog.zhidaohulian.com/app";
        }
        mEventsEndpoint = eventsEndpoint;

        String eventsFallbackEndpoint = metaData.getString("com.elegant.analytics.AnalyticsConfig.EventsFallbackEndpoint");
        if (null == eventsFallbackEndpoint) {
            eventsFallbackEndpoint = "https://bdlog.zhidaohulian.com/app";
        }
        mEventsFallbackEndpoint = eventsFallbackEndpoint;

        Logger.w(LOGTAG,toString());
    }

    public int getBulkUploadLimit() {
        return mMode == MODE_CACHE_UPLOAD ? mBulkUploadLimit : DEBUG_UPLOAD_LIMIT;
    }

    public int getFlushInterval() {
        return mMode == MODE_CACHE_UPLOAD ? mFlushInterval : DEBUG_FLUSH_INTERVAL;
    }

    public long getDataExpiration() {
        return mDataExpiration;
    }

    public int getMinimumDatabaseLimit() {
        return mMinimumDatabaseLimit;
    }

    public boolean getDisableFallback() {
        return mDisableFallback;
    }

    public boolean getDisableAppOpenEvent() {
        return mDisableAppOpenEvent;
    }

    public String getEventsEndpoint() {
        return mEventsEndpoint;
    }

    public String getEventsFallbackEndpoint() {
        return mEventsFallbackEndpoint;
    }

    public String getResourcePackageName() {
        return mResourcePackageName;
    }

    private static AnalyticsConfig readConfig(Context appContext) {
        final String packageName = appContext.getPackageName();
        try {
            final ApplicationInfo appInfo = appContext.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Bundle configBundle = appInfo.metaData;
            if (null == configBundle) {
                configBundle = new Bundle();
            }
            return new AnalyticsConfig(configBundle, appContext);
        } catch (final NameNotFoundException e) {
            throw new RuntimeException("Can't configure AT with package name " + packageName, e);
        }
    }

    @Override
    public String toString() {
        return "AnalyticsConfig{" +
                "mBulkUploadLimit=" + mBulkUploadLimit +
                ", mFlushInterval=" + mFlushInterval +
                ", mDataExpiration=" + mDataExpiration +
                ", mMinimumDatabaseLimit=" + mMinimumDatabaseLimit +
                ", mDisableFallback=" + mDisableFallback +
                ", mDisableAppOpenEvent=" + mDisableAppOpenEvent +
                ", mEventsEndpoint='" + mEventsEndpoint + '\'' +
                ", mEventsFallbackEndpoint='" + mEventsFallbackEndpoint + '\'' +
                ", mResourcePackageName='" + mResourcePackageName + '\'' +
                ", mSSLSocketFactory=" + mSSLSocketFactory +
                ", mMode=" + mMode +
                ", mChannel='" + mChannel + '\'' +
                ", mAppKey='" + mAppKey + '\'' +
                '}';
    }
}
