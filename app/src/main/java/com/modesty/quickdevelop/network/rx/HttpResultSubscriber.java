package com.modesty.quickdevelop.network.rx;


import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public abstract class HttpResultSubscriber<T> implements Subscriber<T> {
    @Override
    public void onComplete() {

    }

    @Override
    public void onSubscribe(Subscription s){
        onSubsc(s);
    }

    @Override
    public void onError(Throwable e) {
        if (e != null) {
            e.printStackTrace();
            if (e.getMessage() == null) {
                _onError(new Throwable(e.toString()));
            } else {
                _onError(new Throwable(e.getMessage()));
            }
        } else {
            _onError(new Exception("null message"));
        }
    }

    @Override
    public void onNext(T t) {
        onSuccess(t);

    }

    public abstract void onSuccess(T t);

    public abstract void _onError(Throwable e);

    public abstract void onSubsc(Subscription t);
}
