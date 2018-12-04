package com.modesty.analytics;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Keep;
import android.support.v4.util.ArrayMap;


import com.modesty.analytics.utils.DeviceIdUtil;
import com.modesty.analytics.utils.Logger;
import com.modesty.analytics.utils.TrackerConstants;
import com.modesty.analytics.utils.Utils;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author lixiang
 * @since 2018/5/18
 */

public class Analytics {
    private static final String LOGTAG = "Analytics";

    private final JSONObject mEmptyJson = new JSONObject();
    private final Handler mUiThreadHandler = new Handler(Looper.getMainLooper());

    private Context mContext;
    private AnalyticsMessages mMessages;
    private Map<String, Object> mCommonParams;
    private InternalActivityLifecycleCallbacks mInternalActivityLifecycleCallbacks;
    private volatile boolean mHasStarted = false;

    @SuppressLint("StaticFieldLeak")
    private static volatile Analytics sInstance;

    private Analytics() {
    }

    @Keep
    public static Analytics getInstance() {
        if(sInstance == null){
            synchronized (Analytics.class) {
                if(sInstance == null){
                    sInstance = new Analytics();
                }
            }
        }
        return sInstance;
    }

    @Keep
    public void start(Context context){
        if(mHasStarted){
            return;
        }
        if (null == context) {
            throw new NullPointerException("context CANNOT be NULL");
        }
        mContext = context;
        mMessages = AnalyticsMessages.getInstance(context);

        this.mHasStarted = true;
    }

    @Keep
    public void stop(){
        if(!hasInitialized()) {
            return;
        }
        this.flush();
        this.mHasStarted = false;
    }

    private void uploadUserAppList(){
        mUiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMessages.uploadUserAppList();
            }
        }, 2000L);
    }

    private Map<String, Object> initCommonParams(){
        final Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("app_key", AnalyticsConfig.getInstance(mContext).getAppKey());
        map.put("model", Build.MODEL);
        map.put("build_code", Utils.getVersionCode(mContext));
        map.put("app_version", Utils.getVersionName(mContext));
        map.put("system_version", Build.VERSION.RELEASE);
        map.put("os", TrackerConstants.OS);
        map.put("package_name", Utils.getPackageName(mContext));
        map.put("sdk_version", AnalyticsConfig.VERSION);
        map.put("jail_break", "0");
        map.put("platform", TrackerConstants.PLATFORM);
        map.put("scale", Utils.getResolution(mContext));
        map.put("imei", DeviceIdUtil.getDeviceId(mContext));
        map.put("data_sources", TrackerConstants.ANALYTICS_TYPE_NATIVE);
        map.put("brand", Build.BRAND);
        map.put("manufacturer", Build.MANUFACTURER);
        return map;
    }

    private Map<String,Object> getVariableParams(){
        Map<String, Object> map = new ArrayMap<>();
        map.put("upload_time", System.currentTimeMillis());
        map.put("channel_id", AnalyticsConfig.getInstance(mContext).getChannel());
        map.put("net_type", Utils.getNetworkType(mContext));
        map.put("carrier", Utils.getOperator(mContext));
        return map;
    }

    private boolean hasInitialized(){
        if(!mHasStarted){
            Logger.e("Analytics","you must call Analytics.getInstance().start(context) in your application first.");
            return false;
        }
        return true;
    }

    @Keep
    public void setCustomParams(Map<String, Object> map){
        if(!hasInitialized()) {
            return;
        }
        if(Utils.isEmpty(map)){
            return;
        }
        if(mCommonParams == null){
            mCommonParams = initCommonParams();
        }
        mCommonParams.putAll(map);
    }

    @Keep
    public void setChannel(String channel){
        if(!hasInitialized()) {
            return;
        }
        AnalyticsConfig.getInstance(mContext).setChannel(channel);
    }

    @Keep
    public void setAppKey(String appKey){
        if(!hasInitialized()) {
            return;
        }
        AnalyticsConfig.getInstance(mContext).setAppKey(appKey);
    }

    @Keep
    public void setMode(int mode){
        if(!hasInitialized()) {
            return;
        }
        AnalyticsConfig.getInstance(mContext).setMode(mode);
    }

    @Keep
    public void shouldLog(boolean shouldLog){
        if(!hasInitialized()) {
            return;
        }
        AnalyticsConfig.getInstance(mContext).shouldLog(shouldLog);
    }

    @Keep
    public void track(String eventName, Map<String, Object> properties) {
        if(!hasInitialized()) {
            return;
        }
        if (Utils.isEmpty(properties)) {
            track(eventName, mEmptyJson);
        } else {
            try {
                track(eventName, new JSONObject(properties));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Keep
    public void track(String eventName, JSONObject properties) {
        if(!hasInitialized()) {
            return;
        }
        try {
            final JSONObject element = new JSONObject();
            element.put("id", eventName);
            element.put("time", System.currentTimeMillis());
            if(!Utils.isEmpty(properties)){
                element.put("params", properties.toString());
            }
            mMessages.eventsMessage(element);
        } catch (final Exception e) {
            Logger.e(LOGTAG, "Exception tracking event " + eventName, e);
        }
    }

    @Keep
    public void track(String eventName) {
        if(!hasInitialized()) {
            return;
        }
        track(eventName, mEmptyJson);
    }

    /**
     * Push all queued events to servers.
     * We strongly recommend placing a call to flush() in the onDestroy() method of
     * your main application activity or when your app is shut down.
     */
    @Keep
    public void flush() {
        if(!hasInitialized()) {
            return;
        }
        mMessages.postToServer();
    }

    @Keep
    public Map<String, Object> getCommonParams() {
        if(!hasInitialized()) {
            return Collections.emptyMap();
        }

        if(mCommonParams == null){
            mCommonParams = initCommonParams();
        }

        final Map<String, Object> variableParams = getVariableParams();
        if(!Utils.isEmpty(variableParams)){
            mCommonParams.putAll(variableParams);
        }

        Logger.d("Analytics","[common params] " + mCommonParams.toString());
        return Collections.unmodifiableMap(mCommonParams);
    }

    private void registerActivityLifecycleCallbacks() {
        if (mContext.getApplicationContext() instanceof Application) {
            final Application app = (Application) mContext.getApplicationContext();
            mInternalActivityLifecycleCallbacks = new InternalActivityLifecycleCallbacks();
            app.registerActivityLifecycleCallbacks(mInternalActivityLifecycleCallbacks);
        } else {
            Logger.i(LOGTAG, "Context is not an Application.");
        }
    }

    private void unregisterActivityLifecycleCallbacks(){
        if (mContext.getApplicationContext() instanceof Application &&
                mInternalActivityLifecycleCallbacks != null) {
            final Application app = (Application) mContext.getApplicationContext();
            app.unregisterActivityLifecycleCallbacks(mInternalActivityLifecycleCallbacks);
        } else {
            Logger.i(LOGTAG, "Context is not an Application.");
        }
    }

}
