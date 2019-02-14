package com.modesty.quickdevelop.mvp.home.persenter;

import com.modesty.logger.simplelog.Logger;
import com.modesty.quickdevelop.base.RxPresenter;
import com.modesty.quickdevelop.bean.StarListBean;
import com.modesty.quickdevelop.mvp.contract.home.MVPActivityContract;
import com.modesty.quickdevelop.network.helper.RetrofitHelper;
import com.modesty.quickdevelop.network.rx.BaseObjectSubscriber;
import com.modesty.quickdevelop.network.rx.RxUtils;

import java.util.List;

import javax.inject.Inject;

public class MVPActivityPersenterImpl extends RxPresenter<MVPActivityContract.View> implements
        MVPActivityContract.Presenter<MVPActivityContract.View> {

    private RetrofitHelper mRetrofitHelper;

    @Inject
    public MVPActivityPersenterImpl(RetrofitHelper retrofitHelper) {
        mRetrofitHelper = retrofitHelper;
    }

    @Override
    public void getCode(String phone) {
        BaseObjectSubscriber<List<StarListBean>> subscriber = mRetrofitHelper.getSeachList(phone)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribeWith(new BaseObjectSubscriber<List<StarListBean>>(mView) {
                    @Override
                    public void onSuccess(List<StarListBean> bean) {
                        List<StarListBean> data = bean;
                        Logger.d(data.toString());
                    }

                    @Override
                    public void onFailure(String code, String message) {
                        Logger.d("code==" + code + "," + "messgae==" + message);
                    }
                });
        addSubscribe(subscriber);
    }
}
