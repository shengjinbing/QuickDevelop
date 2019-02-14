package com.modesty.quickdevelop.di.component;

import android.content.Context;


import com.modesty.quickdevelop.di.module.ApiModule;
import com.modesty.quickdevelop.di.module.AppModule;
import com.modesty.quickdevelop.network.helper.RetrofitHelper;

import javax.inject.Singleton;

import dagger.Component;

/**
 *1.如果 moudule所依赖的Comonent 中有被单利的对象，那么Conponnent也必须是单利的
 *2.单利对象只能在同一个Activity中有效。不同的Activity 持有的对象不同
 *3.子类component 依赖父类的component ，子类component的Scoped 要小于父类的
 *  Scoped，Singleton的级别是Application
 *4.inject(Activity act) 不能放父类
 *5.依赖component， component之间的Scoped 不能相同
 *6.多个Moudle 之间不能提供相同的对象实例
 *7.Moudle 中使用了自定义的Scoped 那么对应的Compnent 使用同样的Scoped
 */
@Singleton
@Component(modules = {AppModule.class, ApiModule.class})
public interface AppComponent {
    Context getContext();

    RetrofitHelper getRetrofitHelper();
}
