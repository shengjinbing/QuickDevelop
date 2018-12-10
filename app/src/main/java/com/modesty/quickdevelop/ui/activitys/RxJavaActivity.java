package com.modesty.quickdevelop.ui.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.modesty.logger.simplelog.Logger;
import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.bean.StarListBean;
import com.modesty.quickdevelop.network.ServiceFactory;
import com.modesty.quickdevelop.network.response.HttpResponse;
import com.modesty.quickdevelop.network.rx.BaseObjectSubscriber;
import com.modesty.quickdevelop.network.rx.RxUtils;
import com.modesty.quickdevelop.network.rx.TransFormUtils;

import java.util.List;

public class RxJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java);
    }

    /**
     * 正常方法调用
     *
     * @param view
     */
    public void normal(View view) {
        ServiceFactory.newApiService().getSeachList("张立")
                .compose(RxUtils.rxSchedulerHelper())
                .subscribeWith(new BaseObjectSubscriber<List<StarListBean>>() {
                    @Override
                    public void onSuccess(List<StarListBean> starListBeans) {
                        List<StarListBean> data = starListBeans;
                        Logger.d(data.toString());
                    }
                    @Override
                    public void onFailure(String code, String message) {
                        Logger.d("code=="+code+","+"messgae=="+message);
                    }
                });
    }
}
