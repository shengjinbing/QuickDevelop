package com.modesty.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author wangzhiyuan
 * @since 2018/5/14
 */

public class ActivityLifecycleManager {
    private Application mApplication;
    /**
     * 当前出于 Start 状态的 Activity
     */
    private ArrayList<Activity> mStartedActivities = new ArrayList<>();

    private ArrayList<AppStateListener> mAppStateListeners = new ArrayList<>();

    /**
     * 当前存活的 Activity
     */
    private HashMap<Activity, ActivityTrace> mAliveActivities = new HashMap<>();

    /**
     * App 是否出于前台
     */
    private volatile boolean mIsAppActive = false;

    private Activity mCurrentResumedActivity;

    /**
     * Home 键事件广播的接受器
     */
    private HomeKeyEventReceiver mHomeKeyEventReceiver;
    /**
     * Home 键事件监听者列表
     */
    private ArrayList<HomeKeyEventListener> mHomeKeyEventListeners = new ArrayList<>();

    private DefActivityLifecycleCallbacks mInnerActivityListener = new DefActivityLifecycleCallbacks() {
        @Override
        public void onActivityStarted(Activity activity) {
            if (mStartedActivities.isEmpty()) {
                mIsAppActive = true;
                notifyAppStateChanged(AppStateListener.ACTIVE);
            }
            mStartedActivities.add(activity);

            ActivityTrace trace = mAliveActivities.get(activity);
            if (trace != null) {
                trace.startCnt++;
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            mStartedActivities.remove(activity);
            if (mStartedActivities.isEmpty()) {
                mIsAppActive = false;
                notifyAppStateChanged(AppStateListener.INACTIVE);
            }

            ActivityTrace trace = mAliveActivities.get(activity);
            if (trace != null) {
                trace.stopCnt++;
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            mCurrentResumedActivity = activity;
            ActivityTrace trace = mAliveActivities.get(activity);
            if (trace != null) {
                trace.resumeCnt++;
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            ActivityTrace trace = mAliveActivities.get(activity);
            if (trace != null) {
                trace.pauseCnt++;
            }
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            mAliveActivities.put(activity, new ActivityTrace(activity));
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            mAliveActivities.remove(activity);
        }
    };

    private ActivityLifecycleManager() {}

    public static ActivityLifecycleManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder{
        private static final ActivityLifecycleManager INSTANCE = new ActivityLifecycleManager();
    }

    public void start(Application application) {
        this.mApplication = application;
        registerInnerActivityListener();
        registerHomeKeyEventReceiver();
    }

    public void stop(){
        unregisterActivityLifecycleCallbacks(mInnerActivityListener);
        unregisterHomeKeyEventReceiver();
    }

    /**
     * 注册 Activity 生命周期的回调
     *
     * @param callbacks Activity 生命周期的回调
     */
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callbacks) {
        if (mApplication == null) {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return;
        }

        mApplication.registerActivityLifecycleCallbacks(callbacks);
    }

    /**
     * 取消注册 Activity 生命周期的回调
     *
     * @param callbacks Activity 生命周期的回调
     */
    public void unregisterActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callbacks) {
        if (mApplication == null) {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return;
        }

        mApplication.unregisterActivityLifecycleCallbacks(callbacks);
    }

    /**
     * 应用是否出于前台
     *
     * @return
     */
    public boolean isAppActive() {
        return mIsAppActive;
    }

    public Activity getCurrentActivity(){
        return mCurrentResumedActivity;
    }

    /**
     * 添加应用状态的监听
     *
     * @param listener
     */
    public void addAppStateListener(AppStateListener listener) {
        synchronized (mAppStateListeners) {
            mAppStateListeners.add(listener);
        }
    }

    /**
     * 移除应用状态的监听
     *
     * @param listener
     */
    public void removeAppStateListener(AppStateListener listener) {
        synchronized (mAppStateListeners) {
            mAppStateListeners.remove(listener);
        }
    }

    /**
     * 添加 home 键的事件监听
     *
     * @param listener
     */
    public void addHomeKeyEventListener(HomeKeyEventListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (mHomeKeyEventListeners) {
            mHomeKeyEventListeners.add(listener);
        }
    }

    /**
     * 移除 home 键的事件监听
     *
     * @param listener
     */
    public void removeHomeKeyEventListener(HomeKeyEventListener listener) {
        if (listener == null) {
            return;
        }

        synchronized (mHomeKeyEventListeners) {
            mHomeKeyEventListeners.remove(listener);
        }
    }

    /**
     * 注册Activity生命周期的监听
     */
    private void registerInnerActivityListener() {
        registerActivityLifecycleCallbacks(mInnerActivityListener);
    }

    private void notifyAppStateChanged(int state) {
        Object[] listeners = collectAppStateListeners();
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                ((AppStateListener) listeners[i]).onStateChanged(state);
            }
        }
    }

    private Object[] collectAppStateListeners() {
        Object[] listeners = null;
        synchronized (mAppStateListeners) {
            if (mAppStateListeners.size() > 0) {
                listeners = mAppStateListeners.toArray();
            }
        }
        return listeners;
    }

    private Object[] collectHomeKeyEventListeners() {
        Object[] listeners = null;
        synchronized (mHomeKeyEventListeners) {
            if (mHomeKeyEventListeners.size() > 0) {
                listeners = mHomeKeyEventListeners.toArray();
            }
        }
        return listeners;
    }

    private void registerHomeKeyEventReceiver() {
        android.content.IntentFilter filter = new android.content.IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        mHomeKeyEventReceiver = new HomeKeyEventReceiver();
        mApplication.registerReceiver(mHomeKeyEventReceiver, filter);
    }

    private void unregisterHomeKeyEventReceiver() {
        mApplication.unregisterReceiver(mHomeKeyEventReceiver);
    }

    private void onHomeKeyPressed() {
        Object[] listeners = collectHomeKeyEventListeners();
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                ((HomeKeyEventListener) listeners[i]).onHomeKeyPressed();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static abstract class DefActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
        @Override
        public void onActivityStarted(Activity activity) {}
        @Override
        public void onActivityResumed(Activity activity) {}
        @Override
        public void onActivityPaused(Activity activity) {}
        @Override
        public void onActivityStopped(Activity activity) {}
        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
        @Override
        public void onActivityDestroyed(Activity activity) {}
    }

    public interface AppStateListener {
        int INACTIVE = 0;
        int ACTIVE = 1;
        /**
         * App 状态的回调
         */
        void onStateChanged(int state);

    }

    /**
     * home 键的监听
     */
    public interface HomeKeyEventListener {
        void onHomeKeyPressed();
    }

    /**
     * Activity 的生命周期调用痕迹
     */
    static class ActivityTrace {
        Activity activity;
        int resumeCnt;
        int pauseCnt;
        int startCnt;
        int stopCnt;

        ActivityTrace(Activity activity) {
            this.activity = activity;
        }
    }

    private final class HomeKeyEventReceiver extends BroadcastReceiver {
        private final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null && reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    onHomeKeyPressed();
                }
            }
        }
    }
}
