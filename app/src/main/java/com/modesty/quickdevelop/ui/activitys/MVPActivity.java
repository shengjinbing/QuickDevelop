package com.modesty.quickdevelop.ui.activitys;

import android.os.Bundle;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.mvp1.AbstractMvpActivitiy;
import com.modesty.quickdevelop.mvp1.CreatePresenter;
import com.modesty.quickdevelop.mvp1.RequestPresenter;
import com.modesty.quickdevelop.mvp1.RequestView;

//声明需要创建的Presenter
@CreatePresenter(RequestPresenter.class)
public class MVPActivity extends AbstractMvpActivitiy<RequestView, RequestPresenter> implements RequestView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvp);
        //设置自己的Presenter工厂，如果你想自定义的话
        // setPresenterFactory(xxx);
        initData();
    }

    private void initData() {
        getMvpPresenter().clickRequest("hhh");
    }

    @Override
    public void updateUI() {

    }
}
