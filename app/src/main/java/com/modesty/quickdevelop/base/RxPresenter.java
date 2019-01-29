package com.modesty.quickdevelop.base;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;


import com.modesty.quickdevelop.network.rx.RxBus;
import com.modesty.quickdevelop.utils.RxLifecycleUtils;
import com.uber.autodispose.AutoDisposeConverter;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 优点:
 * 1.(1)降低耦合度(2)模块职责划分明显(3)利于测试驱动开发(4)代码复用(5)隐藏数据(6)代码灵活性
 * 缺点:
 * 1.由于对视图的渲染放在了Presenter中，所以视图和Presenter的交互会过于频繁。还有一点需要明白，如果Presenter过多地渲
 *   染了视图，往往会使得它与特定的视图的联系过于紧密。一旦视图需要变更，那么Presenter也需要变更了
 * 2.一个Presenter对应一个View，会创建大量的类。
 * 3.presenter的复用会产生接口冗余。
 * 4.presenter导致内存泄露(需要及时释放View的引用)，如果在子线程中未回调但是Activity已经销毁了，可以使用若引用包裹View
 * Created by zzq on 2016/12/20.
 * 基于Rx的Presenter封装,控制订阅的生命周期
 */
public class RxPresenter<T extends BaseContract.BaseView> implements BaseContract.BasePresenter<T> {

    protected T mView;
    private CompositeDisposable mCompositeDisposable;

    private LifecycleOwner lifecycleOwner;

    private void unSubscribe() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    /**
     * 删除
     *
     * @param disposable disposable
     */
    protected boolean remove(Disposable disposable) {
        return mCompositeDisposable != null && mCompositeDisposable.remove(disposable);
    }

    protected void addSubscribe(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    protected <K> void addRxBusSubscribe(Class<K> eventType, Consumer<K> act) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(RxBus.INSTANCE.toDefaultFlowable(eventType, act));
    }

    protected <T> AutoDisposeConverter<T> bindLifecycle() {
        if (null == lifecycleOwner)
            throw new NullPointerException("lifecycleOwner == null");
        return RxLifecycleUtils.bindLifecycle(lifecycleOwner);
    }

    /**
     * 暂时绑定不生效，需要在MyBaseActivity里面开启
     * @param owner
     */
    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
       this.lifecycleOwner = owner;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {

    }

    @Override
    public void onLifecycleChanged(@NonNull LifecycleOwner owner, @NonNull Lifecycle.Event event) {

    }

    @Override
    public void attachView(T view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        this.mView = null;
        unSubscribe();
    }
}
