package com.modesty.quickdevelop.ui.activitys;

import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.modesty.quickdevelop.R;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 这一部分分析源码不涉及hook，所有根据使用方式浏览源码比较好懂。
 */
public class RxjavaResourceActivity extends AppCompatActivity {
    public static final String TAG = "RxjavaResource_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava_resource);
        resource();
        mapResource();
    }

    /**
     * 版本2+
     * 我们的目的：
     * 1.知道源头(Observable)是如何将数据发送出去的。
     * 2.知道终点（Observer）是如何接收到数据的。
     * 3.何时将源头和终点关联起来的
     * 4.知道线程调度是怎么实现的
     * 5.知道操作符是怎么实现的
     */
    private void resource() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("1");
                e.onComplete();
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe() called with: d = [" + d + "]");
            }

            @Override
            public void onNext(String value) {
                Log.d(TAG, "onNext() called with: value = [" + value + "]");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError() called with: e = [" + e + "]");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete() called");
            }
        });
    }

    /**
     * 订阅的过程，是从下游到上游依次订阅的。
     * <p>
     * 1.即终点 Observer 订阅了 map 返回的ObservableMap。
     * 2.然后map的Observable(ObservableMap)在被订阅时，会订阅其内部保存上游Observable，用于订阅上游的Observer是一个装饰者(MapObserver)，内部保存了下游（本例是终点）Observer，以便上游发送数据过来时，能传递给下游。
     * 3.以此类推，直到源头Observable被订阅，根据上节课内容，它开始向Observer发送数据。
     * 数据传递的过程，当然是从上游push到下游的，
     * <p>
     * 1.源头Observable传递数据给下游Observer（本例就是MapObserver）
     * 2.然后MapObserver接收到数据，对其变换操作后(实际的function在这一步执行)，再调用内部保存的下游Observer的onNext()发送数据给下游
     * 3.以此类推，直到终点Observer。
     * <p>
     * subscribeOn只有第一个生效，因为订阅是从下游发送到上游
     * observeOn每一个都生效，因为数据从上游发送到下游
     */
    private void mapResource() {
        final Observable<String> testCreateObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("1");
                e.onComplete();
            }
        });
        Observable<Integer> map = testCreateObservable.map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                return Integer.parseInt(s);
            }
        });
        map
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe() called with: d = [" + d + "]");
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "onNext() called with: value = [" + value + "]");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError() called with: e = [" + e + "]");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete() called");
                    }
                });
    }
}
