package com.modesty.quickdevelop.utils.crash;

import android.app.Application;

/**
 * Created by lixiang on 2020/9/1
 * Describe:
 */
public class CrashCatcher implements Thread.UncaughtExceptionHandler {

    private Application app;

    public void init(Application app){
        this.app = app;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // 封装&保存到file

        exitApp();

    }

    private void exitApp(){
        System.exit(0);
    }
}
