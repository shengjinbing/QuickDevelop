package com.modesty.quickdevelop.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Trace;
import android.util.Log;

import com.github.moduth.blockcanary.BlockCanary;
import com.github.moduth.blockcanary.BlockCanaryContext;
import com.github.moduth.blockcanary.internal.BlockInfo;
import com.modesty.logger.simplelog.LogLevel;
import com.modesty.logger.simplelog.Logger;
import com.modesty.quickdevelop.di.component.AppComponent;
import com.modesty.quickdevelop.di.component.DaggerAppComponent;
import com.modesty.quickdevelop.di.module.ApiModule;
import com.modesty.quickdevelop.di.module.AppModule;
import com.modesty.quickdevelop.network.NetConfig;
import com.modesty.quickdevelop.utils.TraceUtil;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 李想
 * on 2018/11/26
 */
public class BaseApplication extends Application {

    public static Application context;
    private static BaseApplication instance;
    private AppComponent appComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        String rootdir = MMKV.initialize(this);
        Log.d("BBBBB",rootdir);

        TraceUtil.i("BaseApplication:onCreate()");
        instance = this;
        context = this;
        //配置日志
        Logger.init("MODESTY_LOGG", LogLevel.FULL);
        NetConfig.instance().setLoggable(true);

        //配置LeakCanary
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...

        //配置BlockCanary
        BlockCanary.install(this,new AppBlockCanaryContext()).start();

        //配置dagger2
        initComponent();
        TraceUtil.o();

    }

    /**
     * 添加依赖关系，连接Module之间的关系
     */
    private void initComponent() {
        appComponent = DaggerAppComponent.builder()
                .apiModule(new ApiModule())
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public static Application getAppContext(){
        return context;
    }

    public static BaseApplication getInstance(){
        return instance;
    }



}
