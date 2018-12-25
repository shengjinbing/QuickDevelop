package com.modesty.quickdevelop.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.modesty.logger.simplelog.LogLevel;
import com.modesty.logger.simplelog.Logger;
import com.modesty.quickdevelop.network.NetConfig;
import com.squareup.leakcanary.LeakCanary;

import java.io.File;

/**
 * Created by 李想
 * on 2018/11/26
 */
public class BaseApplication extends Application {

    public static Application context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Logger.init("MODESTY_LOGG", LogLevel.FULL);
        NetConfig.instance().setLoggable(true);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...

    }

    public static Application getAppContext(){
        return context;
    }



}
