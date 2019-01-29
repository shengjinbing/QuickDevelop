package com.modesty.quickdevelop.presenter;
import com.modesty.quickdevelop.base.RxPresenter;
import com.modesty.quickdevelop.contract.MVPActivityContract;

/**
* Created by Administrator on 2019/01/22
*/

public class MVPActivityPresenterImpl
        extends RxPresenter<MVPActivityContract.View>
        implements MVPActivityContract.Presenter<MVPActivityContract.View>{


    @Override
    public void getCode(String phone) {
       mView.updateCode();
    }
}