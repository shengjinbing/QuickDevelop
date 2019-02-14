package com.modesty.quickdevelop.mvp1;

/**
 * 工厂接口
 * @param <V>
 * @param <P>
 */
public interface PresenterMvpFactory<V extends BaseMvpView,P extends BaseMvpPresenter<V>>  {
    /**
     * 创建Presenter的接口方法
     * @return 需要创建的Presenter
     */
    P createMvpPresenter();
}
