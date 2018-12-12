package com.modesty.quickdevelop.network.rx;

import android.content.Context;
import android.widget.Toast;

import com.modesty.logger.simplelog.Logger;
import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.base.BaseApplication;
import com.modesty.quickdevelop.bean.BaseData;
import com.modesty.quickdevelop.network.exception.NetworkUnavailableException;
import com.modesty.quickdevelop.network.response.HttpResponse;
import com.modesty.quickdevelop.utils.NetConstants;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * @author wangzhiyuan
 * @since 2018/6/7
 */

public abstract class SubscriberImpl<T extends BaseData> extends SubscriberEx<T> {

    public SubscriberImpl() {
        super();
    }

    public SubscriberImpl(Context attachedContext) {
        super(attachedContext);
    }

    public SubscriberImpl(Context attachedContext, String loadingMessage) {
        super(attachedContext, loadingMessage);
    }

    public SubscriberImpl(Context attachedContext, String loadingMessage, boolean cancellable) {
        super(attachedContext, loadingMessage, cancellable);
    }

    public SubscriberImpl(Context attachedContext, String loadingMessage, boolean cancellable, boolean cancellableOnTouchOutSide) {
        super(attachedContext, loadingMessage, cancellable, cancellableOnTouchOutSide);
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        Logger.e(e, "SubscriberImpl---onError occurs");
        if (e instanceof NetworkUnavailableException) {
            onNetworkLost();
        } else {
            //onFailure(BizNetConstants.CODE_REQUEST_ERROR, ResourcesHelper.getString(BaseApp.getAppContext(), R.string.network_error));
        }
    }

    @Override
    public void onNext(T o) {
        super.onNext(o);
        if (o == null) {
            Logger.i("SubscriberImpl---onNext, result is null");
            //onFailure(BizNetConstants.CODE_REQUEST_ERROR, ResourcesHelper.getString(BaseApp.getAppContext(), R.string.network_error));
            return;
        }
        switch (o.code) {
            case 0:
                onSuccess(o);
                break;
            default:
                onFailure(o.code, o.message);
                break;
        }
    }

    protected abstract void onFailure(int code, String message);

    protected abstract void onSuccess(T o);

    protected void onNetworkLost() {
        Toast.makeText(BaseApplication.getAppContext(), "请求异常，请重试！", Toast.LENGTH_SHORT).show();
    }

}
