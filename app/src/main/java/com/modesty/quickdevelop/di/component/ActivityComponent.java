package com.modesty.quickdevelop.di.component;

import com.modesty.quickdevelop.di.module.ActivityModule;
import com.modesty.quickdevelop.di.scope.ActivityScope;
import com.modesty.quickdevelop.ui.activitys.MvpDagger2Activity;

import javax.inject.Inject;

import dagger.Component;

/**
 * Created by Administrator on 2017/11/20 0020.
 * <p>
 * <p>
 * 需要用到这个连接器的对象，就是这个对象里面有需要注入的属性
 * （被标记为@Inject的属性）
 * 这里inject表示注入的意思，这个方法名可以随意更改，但建议就
 * 用inject即可。
 */

@ActivityScope
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MvpDagger2Activity mvpDagger2Activity);
}
