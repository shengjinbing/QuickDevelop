package com.modesty.quickdevelop.ui.activitys;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.bean.StarListBean;
import com.modesty.quickdevelop.bean.StarListData;
import com.modesty.quickdevelop.network.ServiceFactory;
import com.modesty.quickdevelop.network.response.HttpResponse;
import com.modesty.quickdevelop.network.rx.BaseObjectSubscriber;
import com.modesty.quickdevelop.network.rx.RxUtils;
import com.modesty.quickdevelop.network.rx.SubscriberEx;
import com.modesty.quickdevelop.network.rx.SubscriberImpl;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

public class RxjavaConcatActivity extends AppCompatActivity {
    public static final String TAG = "RxjavaConcat_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava_concat);
    }

    /**
     * 对于从磁盘 / 内存缓存中 获取缓存数据
     *
     * @param view
     */
    public void concat(View view) {
        // concat（）：组合多个被观察者（≤4个）一起发送数据
        //concatArray() 则可＞4个
        // 注：串行执行
        Flowable<HttpResponse<List<StarListBean>>> flowable1 = ServiceFactory.newApiService().getSeachList("张立");
        Flowable<HttpResponse<List<StarListBean>>> flowable2 = ServiceFactory.newApiService().getSeachList("胡大平");
        Flowable.concat(flowable1, flowable2).compose(RxUtils.rxSchedulerHelper())
                .subscribeWith(new BaseObjectSubscriber<List<StarListBean>>() {
                    @Override
                    public void onSuccess(List<StarListBean> starListBeans) {
                        Log.d(TAG, starListBeans.get(0).getName());
                    }

                    @Override
                    public void onFailure(String code, String message) {

                    }
                });
       /* Observable.concatArray(Observable.just(1, 2, 3),
                Observable.just(4, 5, 6),
                Observable.just(7, 8, 9),
                Observable.just(10, 11, 12))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

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
                });*/

    }

    /**
     * 作用:组合多个被观察者一起发送数据，合并后 按时间线并行执行
     * 1.二者区别：组合被观察者的数量，即merge（）组合被观察者数量≤4个，而mergeArray（）则可＞4个
     * 2.区别上述concat（）操作符：同样是组合多个被观察者一起发送数据，但concat（）操作符合并后是按发送顺序串行执行
     *
     * @param view
     */
    public void merge(View view) {
// merge（）：组合多个被观察者（＜4个）一起发送数据
        // 注：合并后按照时间线并行执行
        Observable.merge(
                Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS), // 从0开始发送、共发送3个数据、第1次事件延迟发送时间 = 1s、间隔时间 = 1s
                Observable.intervalRange(2, 3, 1, 1, TimeUnit.SECONDS)) // 从2开始发送、共发送3个数据、第1次事件延迟发送时间 = 1s、间隔时间 = 1s
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

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

    }

    public void concatDelayError(View view) {
/*
        <-- 使用了concatDelayError（）的情况 -->
*/
        Observable.concatArrayDelayError(
                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                        emitter.onNext(1);
                        emitter.onNext(2);
                        emitter.onNext(3);
                        emitter.onError(new NullPointerException()); // 发送Error事件，因为使用了concatDelayError，所以第2个Observable将会发送事件，等发送完毕后，再发送错误事件
                        emitter.onComplete();
                    }
                }),
                Observable.just(4, 5, 6))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

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
     * 该类型的操作符主要是对多个被观察者中的事件进行合并处理。
     * 合并 多个被观察者（Observable）发送的事件，生成一个新的事件序列（即组合过后的事件序列），并最终发送
     *
     * @param view
     */
    public void zip(View view) {
        Flowable.zip(ServiceFactory.newApiService().getSeachList1("张立"), ServiceFactory.newApiService().getSeachList1("胡大平"),
                (listHttpResponse, listHttpResponse2) -> {
                    listHttpResponse.getData().get(0).setName(listHttpResponse.getData().get(0).getName() + listHttpResponse2.getData().get(0).getName());
                    return listHttpResponse;
                })
                .compose(RxUtils.rxSchedulerHelper())
                .subscribeWith(new SubscriberImpl<StarListData>(this) {
                    @Override
                    protected void onSuccess(StarListData o) {
                        Log.d(TAG, o.getData().get(0).getName());
                    }

                    @Override
                    protected void onFailure(int code, String message) {

                    }
                });
       /* Flowable.zip(ServiceFactory.newApiService().getSeachList("张立"), ServiceFactory.newApiService().getSeachList("胡大平"), new BiFunction<HttpResponse<List<StarListBean>>, HttpResponse<List<StarListBean>>, HttpResponse<List<StarListBean>>>() {
            @Override
            public HttpResponse<List<StarListBean>> apply(HttpResponse<List<StarListBean>> listHttpResponse, HttpResponse<List<StarListBean>> listHttpResponse2) throws Exception {
                listHttpResponse.data.get(0).setName(listHttpResponse.data.get(0).getName()+","+listHttpResponse2.data.get(0).getName());
                return listHttpResponse;
            }
        })
                .compose(RxUtils.rxSchedulerHelper())
                .subscribeWith(new BaseObjectSubscriber<List<StarListBean>>() {
                    @Override
                    public void onSuccess(List<StarListBean> starListBean) {
                        Log.d(TAG,starListBean.get(0).getName());
                    }

                    @Override
                    public void onFailure(String code, String message) {

                    }
                });*/
    }

    /**
     * 当两个Observables中的任何一个发送了数据后，将先发送了数据的Observables 的最新（最后）一个数据 与
     * 另外一个Observable发送的每个数据结合，最终基于该函数的结果发送数据
     * <p>
     * 联合判断，
     *
     * @param view
     */
    public void combineLatest(View view) {
//与Zip（）的区别：Zip（） = 按个数合并，即1对1合并；CombineLatest（） = 按时间合并，即在同一个时间点上合并
        Observable.combineLatest(
                Observable.just(1L, 2L, 3L), // 第1个发送数据事件的Observable
                Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS), // 第2个发送数据事件的Observable：从0开始发送、共发送3个数据、第1次事件延迟发送时间 = 1s、间隔时间 = 1s
                new BiFunction<Long, Long, Long>() {
                    @Override
                    public Long apply(Long o1, Long o2) throws Exception {
                        // o1 = 第1个Observable发送的最新（最后）1个数据
                        // o2 = 第2个Observable发送的每1个数据
                        Log.e(TAG, "合并的数据是： " + o1 + " " + o2);
                        return o1 + o2;
                        // 合并的逻辑 = 相加
                        // 即第1个Observable发送的最后1个数据 与 第2个Observable发送的每1个数据进行相加
                    }
                }).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long s) throws Exception {
                Log.e(TAG, "合并的结果是： " + s);
            }
        });

    }

    /**
     * 把被观察者需要发送的事件聚合成1个事件 & 发送
     * 聚合的逻辑根据需求撰写，但本质都是前2个数据聚合，然后与后1个数据继续进行聚合，依次类推
     *
     * @param view
     */
    public void reduce(View view) {
        Observable.just(1, 2, 3, 4)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    // 在该复写方法中复写聚合的逻辑
                    @Override
                    public Integer apply(@NonNull Integer s1, @NonNull Integer s2) throws Exception {
                        Log.e(TAG, "本次计算的数据是： " + s1 + " 乘 " + s2);
                        return s1 * s2;
                        // 本次聚合的逻辑是：全部数据相乘起来
                        // 原理：第1次取前2个数据相乘，之后每次获取到的数据 = 返回的数据x原始下1个数据每
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer s) throws Exception {
                Log.e(TAG, "最终计算的结果是： " + s);

            }
        });


    }

    /**
     * 将被观察者Observable发送的数据事件收集到一个数据结构里
     *
     * @param view
     */
    public void collect(View view) {
        Observable.just(1, 2, 3, 4, 5, 6)
                .collect(
                        // 1. 创建数据结构（容器），用于收集被观察者发送的数据
                        new Callable<ArrayList<Integer>>() {
                            @Override
                            public ArrayList<Integer> call() throws Exception {
                                return new ArrayList<>();
                            }
                            // 2. 对发送的数据进行收集
                        }, new BiConsumer<ArrayList<Integer>, Integer>() {
                            @Override
                            public void accept(ArrayList<Integer> list, Integer integer)
                                    throws Exception {
                                // 参数说明：list = 容器，integer = 后者数据
                                list.add(integer);
                                // 对发送的数据进行收集
                            }
                        }).subscribe(new Consumer<ArrayList<Integer>>() {
            @Override
            public void accept(@NonNull ArrayList<Integer> s) throws Exception {
                Log.e(TAG, "本次发送的数据是： " + s);

            }
        });

    }

    /**
     * 在一个被观察者发送事件前，追加发送一些数据 / 一个新的被观察者
     *
     * @param view
     */
    public void startWith(View view) {
//        <-- 在一个被观察者发送事件前，追加发送一些数据 -->
        // 注：追加数据顺序 = 后调用先追加
        Observable.just(4, 5, 6)
                .startWith(0)  // 追加单个数据 = startWith()
                .startWithArray(1, 2, 3) // 追加多个数据 = startWithArray()
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

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


//<-- 在一个被观察者发送事件前，追加发送被观察者 & 发送数据 -->
        // 注：追加数据顺序 = 后调用先追加
               /* Observable.just(4, 5, 6)
                        .startWith(Observable.just(1, 2, 3))
                        .subscribe(new Observer<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Integer value) {
                                Log.d(TAG, "接收到了事件"+ value  );
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "对Error事件作出响应");
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "对Complete事件作出响应");
                            }
                        });*/

    }

    /**
     * 统计被观察者发送事件的数量
     *
     * @param view
     */
    public void count(View view) {
// 注：返回结果 = Long类型
        Observable.just(1, 2, 3, 4)
                .count()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "发送的事件数量 =  " + aLong);

                    }
                });

    }
}
