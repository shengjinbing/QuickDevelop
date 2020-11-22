package com.modesty.quickdevelop.ui.activitys;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.base.BaseActivity;
import com.modesty.quickdevelop.mvp.contract.home.MVPActivityContract;
import com.modesty.quickdevelop.mvp.home.persenter.MVPActivityPersenterImpl;

/**
 * MVC  activity既充当Controller有充当显示器
 */
public class MvpDagger2Activity extends BaseActivity<MVPActivityPersenterImpl>
        implements MVPActivityContract.View {

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
      mPresenter.getCode("张立");
    }

    @Override
    protected void initListener() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_mvp_dagger2;
    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    public void updateCode() {

    }
}
