package com.modesty.quickdevelop.di.component;

import android.app.Activity;
import com.modesty.quickdevelop.di.module.FragmentModule;
import com.modesty.quickdevelop.di.scope.FragmentScope;

import dagger.Component;

/**
 * @author zzq  作者 E-mail:   soleilyoyiyi@gmail.com
 * @date 创建时间：2017/4/27 19:30
 * 描述:FragmentComponent
 */
@FragmentScope
@Component(dependencies = AppComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {

    Activity getActivity();
}
