package com.modesty.quickdevelop.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.modesty.quickdevelop.di.component.ActivityComponent;
import com.modesty.quickdevelop.di.component.DaggerActivityComponent;
import com.modesty.quickdevelop.di.module.ActivityModule;
import com.modesty.quickdevelop.utils.RxLifecycleUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.uber.autodispose.AutoDisposeConverter;


import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/11/17 0017.
 */

public abstract class BaseActivity<T extends BaseContract.BasePresenter> extends RxAppCompatActivity implements BaseContract.BaseView {
    @Inject
    protected T mPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initStatusBar();
        initInject();
        initPresenter();
        initView();
        initData();
        initListener();
        if (mPresenter != null){
            getLifecycle().addObserver(mPresenter);
        }
    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListener();

    public abstract int getLayoutId();


    /**
     * 初始化状态栏颜色
     */
    public void initStatusBar() {
    }

    /**
     * 注解
     */
    public void initInject() {
    }

    /**
     * 初始化Presenter()
     */
    private void initPresenter() {
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }

    }
    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

   protected ActivityComponent getActivityComponent() {
        return DaggerActivityComponent.builder()
                .appComponent(BaseApplication.getInstance().getAppComponent())
                .activityModule(getActivityModule())
                .build();
    }
    /**
     * 销毁
     */
    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroy();
    }

    protected <T> AutoDisposeConverter<T> bindLifecycle() {
        return RxLifecycleUtils.bindLifecycle(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}