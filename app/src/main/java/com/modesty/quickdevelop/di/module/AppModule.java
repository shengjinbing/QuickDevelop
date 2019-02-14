package com.modesty.quickdevelop.di.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/11/20 0020.
 */
@Module
public class AppModule {
    private Context mContext;

    public AppModule(Context context) {
        this.mContext = context;
    }

    @Provides
    public Context provideContext() {
        return mContext;
    }
}
