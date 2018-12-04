package com.modesty.quickdevelop.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by 李想
 * on 2018/11/26
 */
public class BaseApplication extends Application {

    public static Application context;
    @Override
    public void onCreate() {
        context = this;
        super.onCreate();
    }

    public static Application getAppContext(){
        return context;
    }



}
