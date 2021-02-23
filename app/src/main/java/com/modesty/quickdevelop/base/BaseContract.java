package com.modesty.quickdevelop.base;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;

/**
 * 基础契约类 用来管理 presenter 与 view
 * Created by zzq on 2016/12/20.
 */

public interface BaseContract {

    interface BaseView {
        int a= 2;

        /**
         * 请求出错
         */
        void showError(String msg);

        /**
         * 请求完成
         */
        void complete();
    }

    interface BasePresenter<T> extends LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        void onCreate(LifecycleOwner owner);

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        void onDestroy(LifecycleOwner owner);

        @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
        void onLifecycleChanged(LifecycleOwner owner,
                                Lifecycle.Event event);

        /**
         * 绑定
         *
         * @param view view
         */
        void attachView(T view);

        /**
         * 解绑
         */
        void detachView();
    }

}
