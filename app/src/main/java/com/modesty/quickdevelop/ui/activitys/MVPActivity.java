package com.modesty.quickdevelop.ui.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.contract.MVPActivityContract;
import com.modesty.quickdevelop.mvp.AbstractMvpActivitiy;
import com.modesty.quickdevelop.mvp.BaseMvpView;
import com.modesty.quickdevelop.mvp.CreatePresenter;
import com.modesty.quickdevelop.mvp.RequestPresenter;
import com.modesty.quickdevelop.mvp.RequestView;

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
