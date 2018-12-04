package com.modesty.analytics;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

/**
 * @author lixiang
 * @since 2018/5/18
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
/* package */ class InternalActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private boolean mIsForeground = true;

    InternalActivityLifecycleCallbacks() {
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityPaused(Activity activity) {
        mIsForeground = false;
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        mIsForeground = true;
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    boolean isInForeground() {
        return mIsForeground;
    }

}
