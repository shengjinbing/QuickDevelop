package com.modesty.quickdevelop.ui.activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.modesty.logger.simplelog.Logger;
import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.bean.StarListBean;
import com.modesty.quickdevelop.network.ServiceFactory;
import com.modesty.quickdevelop.network.response.HttpResponse;
import com.modesty.quickdevelop.network.rx.BaseObjectSubscriber;
import com.modesty.quickdevelop.network.rx.RxUtils;
import com.modesty.quickdevelop.network.rx.TransFormUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * // 下列方法一般用于测试使用
 * <p>
 * <-- empty()  -->
 * // 该方法创建的被观察者对象发送事件的特点：仅发送Complete事件，直接通知完成
 * Observable observable1=Observable.empty();
 * // 即观察者接收后会直接调用onCompleted（）
 * <p>
 * <-- error()  -->
 * // 该方法创建的被观察者对象发送事件的特点：仅发送Error事件，直接通知异常
 * // 可自定义异常
 * Observable observable2=Observable.error(new RuntimeException())
 * // 即观察者接收后会直接调用onError（）
 * <p>
 * <-- never()  -->
 * // 该方法创建的被观察者对象发送事件的特点：不发送任何事件
 * Observable observable3=Observable.never();
 * // 即观察者接收后什么都不调用
 */
public class RxJavaActivity extends AppCompatActivity {
    public static final String TAG = "RxJavaActivity_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java);
    }

    public void rxjava(View view) {
        startActivity(new Intent(this,RxjavaChanageActivity.class));
    }
    public void chanage(View view) {
        startActivity(new Intent(this,RxjavaChanageActivity.class));
    }
    public void concat(View view) {
        startActivity(new Intent(this,RxjavaConcatActivity.class));
    }
    public void function(View view) {
        startActivity(new Intent(this,FunctionActivity.class));
    }


    /**
     * 正常方法调用
     *
     * @param view
     */
    public void normal(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            // 1. 创建被观察者 & 生产事件
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        }).subscribe(new Observer<Integer>() {
            // 2. 通过通过订阅（subscribe）连接观察者和被观察者
            // 3. 创建观察者 & 定义响应事件的行为
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "开始采用subscribe连接");
            }
            // 默认最先调用复写的 onSubscribe（）

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "对Next事件" + value + "作出响应");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "对Error事件作出响应");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }

        });
    }


    /**
     * 1.快速创建1个被观察者对象（Observable）
     * 2.发送事件的特点：直接发送 传入的事件
     *
     * @param view
     */
    public void just(View view) {
        //最多传10个参数
        Observable.just(1, 2, 3, 4).subscribe(
                new Observer<Integer>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "开始采用subscribe连接");
                    }
                    // 默认最先调用复写的 onSubscribe（）

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "接收到了事件" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }

                });
    }

    /**
     * 1.快速创建1个被观察者对象（Observable）
     * 2.发送事件的特点：直接发送 传入的数组数据
     *
     * @param view
     */
    public void fromArray(View view) {
// 1. 设置需要传入的数组
        Integer[] items = {0, 1, 2, 3, 4};

        // 2. 创建被观察者对象（Observable）时传入数组
        // 在创建后就会将该数组转换成Observable & 发送该对象中的所有数据
        Observable.fromArray(items)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "数组遍历");
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "数组中的元素 = " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "遍历结束");
                    }

                });

    }

    /**
     * 1、快速创建1个被观察者对象（Observable）
     * 2、发送事件的特点：直接发送 传入的集合List数据
     *
     * @param view
     */
    public void fromIterable(View view) {
/*
 * 集合遍历
 **/
        // 1. 设置一个集合
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        // 2. 通过fromIterable()将集合中的对象 / 数据发送出去
        Observable.fromIterable(list)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "集合遍历");
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "集合中的数据元素 = " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "遍历结束");
                    }
                });
    }

    /**
     * 直到有观察者（Observer ）订阅时，才动态创建被观察者对象（Observable） & 发送事件
     * 1、通过 Observable工厂方法创建被观察者对象（Observable）
     * 2、每次订阅后，都会得到一个刚创建的最新的Observable对象，这可以确保Observable对象里的数据是最新的
     *
     * @param view
     */
    int i = 10;

    public void defer(View view) {
        //1. 第1次对i赋值

        // 2. 通过defer 定义被观察者对象
        // 注：此时被观察者对象还没创建
        Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> call() throws Exception {
                return Observable.just(i);
            }
        });

        //2. 第2次对i赋值
        i = 15;

        //3. 观察者开始订阅
        // 注：此时，才会调用defer（）创建被观察者对象（Observable）
        observable.subscribe(new Observer<Integer>() {

            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "开始采用subscribe连接");
            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "接收到的整数是" + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "对Error事件作出响应");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }
        });

    }

    /**
     * 快速创建1个被观察者对象（Observable）
     * 1.发送事件的特点：延迟指定时间后，发送1个数值0（Long类型）
     * 2.本质 = 延迟指定时间后，调用一次 onNext(0)
     *
     * @param view
     */
    public void timer(View view) {
        // 该例子 = 延迟2s后，发送一个long类型数值
        Observable.timer(2, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "开始采用subscribe连接");
                    }

                    @Override
                    public void onNext(Long value) {
                        Log.d(TAG, "接收到了事件" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }

                });

// 注：timer操作符默认运行在一个新线程上
// 也可自定义线程调度器（第3个参数）：timer(long,TimeUnit,Scheduler)
    }

    /**
     * 快速创建1个被观察者对象（Observable）
     * 1. 发送事件的特点：每隔指定时间 就发送 事件
     * 2.发送的事件序列 = 从0开始、无限递增1的的整数序列
     *
     * @param view
     */
    public void interval(View view) {
// 参数说明：
        // 参数1 = 第1次延迟时间；
        // 参数2 = 间隔时间数字；
        // 参数3 = 时间单位；
        Observable.interval(3, 1, TimeUnit.SECONDS)
                // 该例子发送的事件序列特点：延迟3s后发送事件，每隔1秒产生1个数字（从0开始递增1，无限个）
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "开始采用subscribe连接");
                    }
                    // 默认最先调用复写的 onSubscribe（）

                    @Override
                    public void onNext(Long value) {
                        Log.d(TAG, "接收到了事件" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }

                });

// 注：interval默认在computation调度器上执行
// 也可自定义指定线程调度器（第3个参数）：interval(long,TimeUnit,Scheduler)

    }

    /**
     * @param view
     */
    public void intervalRange(View view) {
// 参数说明：
        // 参数1 = 事件序列起始点；
        // 参数2 = 事件数量；
        // 参数3 = 第1次事件延迟发送时间；
        // 参数4 = 间隔时间数字；
        // 参数5 = 时间单位
        Observable.intervalRange(3, 10, 2, 1, TimeUnit.SECONDS)
                // 该例子发送的事件序列特点：
                // 1. 从3开始，一共发送10个事件；
                // 2. 第1次延迟2s发送，之后每隔2秒产生1个数字（从0开始递增1，无限个）
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "开始采用subscribe连接");
                    }
                    // 默认最先调用复写的 onSubscribe（）

                    @Override
                    public void onNext(Long value) {
                        Log.d(TAG, "接收到了事件" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }

                });
    }

    /**
     * a. 发送的事件序列 = 从0开始、无限递增1的的整数序列
     * b. 作用类似于intervalRange（），但区别在于：无延迟发送事件
     *
     * @param view
     */
    public void range(View view) {
// 参数说明：
        // 参数1 = 事件序列起始点；
        // 参数2 = 事件数量；
        // 注：若设置为负数，则会抛出异常
        Observable.range(3, 10)
                // 该例子发送的事件序列特点：从3开始发送，每次发送事件递增1，一共发送10个事件
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "开始采用subscribe连接");
                    }
                    // 默认最先调用复写的 onSubscribe（）

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "接收到了事件" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }

                });

    }

    public void rangeLong(View view) {
        // 参数说明：
        // 参数1 = 事件序列起始点；
        // 参数2 = 事件数量；
        // 注：若设置为负数，则会抛出异常
        Observable.rangeLong((long) 3.8, (long) 10.4444)
                // 该例子发送的事件序列特点：从3开始发送，每次发送事件递增1，一共发送10个事件
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "开始采用subscribe连接");
                    }
                    // 默认最先调用复写的 onSubscribe（）

                    @Override
                    public void onNext(Long value) {
                        Log.d(TAG, "接收到了事件" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }

                });

    }

    /**
     * 实现一个网络轮训
     *
     * @param view
     */
    public void intervalLoop(View view) {
        Observable.interval(2, 1, TimeUnit.SECONDS)
                 /*
                  * 步骤2：每次发送数字前发送1次网络请求（doOnNext（）在执行Next事件前调用）
                  * 即每隔1秒产生1个数字前，就发送1次网络请求，从而实现轮询需求
                  *
                  */
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                        Log.d(TAG, "第 " + aLong + " 次轮询");
                        ServiceFactory.newApiService().getSeachList("张立")
                                .compose(RxUtils.rxSchedulerHelper())
                                .subscribeWith(new BaseObjectSubscriber<List<StarListBean>>() {
                                    @Override
                                    public void onSuccess(List<StarListBean> starListBeans) {
                                        Log.d(TAG, starListBeans.get(0).getName());
                                    }

                                    @Override
                                    public void onFailure(String code, String message) {

                                    }
                                });
                    }
                })
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.d(TAG, "aLong==" + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }
                });
    }
}
