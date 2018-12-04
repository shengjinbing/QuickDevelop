package com.modesty.quickdevelop.network.rx;

import android.text.TextUtils;
import android.util.Log;


import com.modesty.quickdevelop.NetUtils;
import com.modesty.quickdevelop.SpUtils;
import com.modesty.quickdevelop.base.BaseApplication;
import com.modesty.quickdevelop.network.exception.ApiException;
import com.modesty.quickdevelop.network.response.HttpResponse;

import java.net.SocketTimeoutException;


import io.reactivex.subscribers.ResourceSubscriber;
import retrofit2.HttpException;
/*
 * 描述:统一处理订阅者
 */

public abstract class BaseObjectSubscriber<T> extends ResourceSubscriber<HttpResponse<T>> {
    private String mMsg;

    public BaseObjectSubscriber() {
    }


    public abstract void onSuccess(T t);

    public abstract void onFailure(String code, String message);

    public void onTokenFail(){

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!NetUtils.isNetworkConnected(BaseApplication.getAppContext())) {
            // Logger.d("没有网络");
        } else {

        }
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onNext(HttpResponse<T> response) {
        if (response.code.equals("0000")) {
            if (response.data == null) {
                onSuccess(null);
            }
            if (response.data != null) {
                onSuccess(response.data);
            }
            if (response.result != null) {
                onSuccess(response.result);
            }
        }else {
            //可以不处理任何东西
            onFailure(response.code, response.message);
        }
    }


    @Override
    public void onError(Throwable e) {
        if (mMsg != null && !TextUtils.isEmpty(mMsg)) {
            Log.d("BBBBB",mMsg);
        } else if (e instanceof ApiException) {
            Log.d("BBBBB",e.toString());
        } else if (e instanceof SocketTimeoutException) {
            Log.d("BBBBB","服务器响应超时ヽ(≧Д≦)ノ");
        } else if (e instanceof HttpException) {
            Log.d("BBBBB","数据加载失败ヽ(≧Д≦)ノ");
        } else {
            Log.d("BBBBB","未知错误ヽ(≧Д≦)ノ");
        }
    }
}
