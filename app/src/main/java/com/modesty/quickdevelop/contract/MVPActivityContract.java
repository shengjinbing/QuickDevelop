package com.modesty.quickdevelop.contract;

import com.modesty.quickdevelop.base.BaseContract;

public class MVPActivityContract {
    public interface View extends BaseContract.BaseView {
        void updateCode();
    }

    public interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getCode(String phone);
    }

    public interface Model {
    }


}