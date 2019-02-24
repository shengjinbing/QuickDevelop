package com.modesty.quickdevelop.ui.activitys;

import android.arch.lifecycle.Lifecycle;
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
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class RxjavaChanageActivity extends RxAppCompatActivity {
    public static final String TAG = "RxjavaChanage_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava_chanage);

    }

    /**
     * 1.RxLifecycle加上生命周期绑定,需要继承RxAppCompatActivity和RxFragment等
     * 2.AutoDispose比RxLifecycle好的地方在于它不需要你的Activity或Fragment继承指定的类。只要你的Activity
     * 或Fragment的父类实现了LifecycleOwner这个接口即可。通过源码发现，support.v7包中的AppCompatActivity
     * 最终继承自SupportActivity，SupportActivity实现了LifecycleOwner接口。support.v4包中的Fragment也
     * 实现了LifecycleOwner接口。而我们目前的项目中为了保证兼容性，都是要依赖Android Support v7这个包的。这样
     * 一来我们就可以优雅的通过AutoDispose解决RxJava产生的内存泄漏问题了
     * <p>
     * <p>
     * RxLifecycle源码分析
     * 1.BehaviorSubject有何作用
     * public final class BehaviorSubject<T> extends Subject<T> {
     * ...
     * }
     * public abstract class Subject<T> extends Observable<T> implements Observer<T> {
     * ...
     * }
     * BehaviorSubject继承Subject，Subject集成Observable实现Observer，Subject的官方介绍：Subject可以看成是
     * 一个桥梁或者代理，在某些ReactiveX实现中（如RxJava），它同时充当了Observer和Observable的角色。因为它是一个
     * Observer，它可以订阅一个或多个Observable；又因为它是一个Observable，它可以转发它收到(Observe)的数据，也可
     * 以发射新的数据。也就是Subject既可以作为被观察者，也可以作为观察者。
     * 2.网络请求Observable对象使用TakeUntil操作符关联BehaviorSubject对象，BehaviorSubject对象发射了数据，
     * 然后网络请求Observable则丢失数据，即不在调用观察者Observer，BehaviorSubject过滤了ActivityEvent事件，
     * 只有事件为ActivityEvent.DESTROY时，BehaviorSubject才发射，所以当Activity关闭时，BehaviorSubject发射
     * ActivityEvent.DESTROY事件，网络请求Observable丢失数据，不在调用观察者Observer，整个网络请求和Activity生命周期绑定完成
     *
     * @param view
     */
    public void normalNet(View view) {
        BaseObjectSubscriber<List<StarListBean>> baseObjectSubscriber = ServiceFactory.newApiService().getSeachList("张立")
                .compose(RxUtils.rxSchedulerHelper())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .as(AutoDispose.autoDisposable(
                        AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
                .subscribeWith(new BaseObjectSubscriber<List<StarListBean>>() {
                    @Override
                    public void onSuccess(List<StarListBean> starListBeans) {
                        List<StarListBean> data = starListBeans;
                        Logger.d(data.toString());
                    }

                    @Override
                    public void onFailure(String code, String message) {
                        Logger.d("code==" + code + "," + "messgae==" + message);
                    }
                });
    }

    /**
     * 将被观察者发送的事件转换为任意的类型事件。
     * 数据类型转换
     *
     * @param view
     */
    public void map(View view) {
        ServiceFactory.newApiService().getSeachList("张立")
                .map(listHttpResponse -> {
                    String name = listHttpResponse.data.get(0).getName();
                    Log.d(TAG, name);
                    return name;
                })
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, s);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * 作用：将被观察者发送的事件序列进行 拆分 & 单独转换，再合并成一个新的事件序列，最后再进行发送
     * 1.为事件序列中每个事件都创建一个 Observable 对象；
     * 2.将对每个 原始事件 转换后的 新事件 都放入到对应 Observable对象；
     * 3.将新建的每个Observable 都合并到一个 新建的、总的Observable 对象；
     * 4.新建的、总的Observable 对象 将 新合并的事件序列 发送给观察者（Observer）
     * <p>
     * 无序的将被观察者发送的整个事件序列进行变换
     *
     * @param view
     */
    public void FlatMap(View view) {
        // 采用RxJava基于事件流的链式操作
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }

            // 采用flatMap（）变换操作符
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("我是事件 " + integer + "拆分后的子事件" + i);
                    // 通过flatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                    // 最终合并，再发送给被观察者
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });

        /*ServiceFactory.newApiService().getSeachList("张立")
                .flatMap(bean -> {
                            Log.d(TAG, bean.data.get(0).getName());
                            return ServiceFactory.newApiService().getSeachList("胡大平");
                        }
                )
                .compose(RxUtils.rxSchedulerHelper())
                .subscribeWith(new BaseObjectSubscriber<List<StarListBean>>() {
                    @Override
                    public void onSuccess(List<StarListBean> starListBeans) {
                        Log.d(TAG, starListBeans.get(0).getName());
                    }

                    @Override
                    public void onFailure(String code, String message) {

                    }
                });*/
    }

    /**
     * 1.新合并生成的事件序列顺序是有序的，即 严格按照旧序列发送事件的顺序
     * 2.concatMap：【有序】与 flatMap 的 区别在于，拆分 & 重新合并生成的事件序列 的顺序与被观察者旧序列生产的顺序一致
     *
     * @param view
     */
    public void ConcatMap(View view) {
// 采用RxJava基于事件流的链式操作
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }

            // 采用concatMap（）变换操作符
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("我是事件 " + integer + "拆分后的子事件" + i);
                    // 通过concatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                    // 最终合并，再发送给被观察者
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });

    }

    public void Buffer(View view) {
// 被观察者 需要发送5个数字
        Observable.just(1, 2, 3, 4, 5)
                .buffer(3, 2) // 设置缓存区大小 & 步长
                // 缓存区大小 = 每次从被观察者中获取的事件数量
                // 步长 = 每次获取新事件的数量
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Integer> stringList) {
                        //
                        Log.d(TAG, " 缓存区里的事件数量 = " + stringList.size());
                        for (Integer value : stringList) {
                            Log.d(TAG, " 事件 = " + value);
                        }
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

    public void flatMapIterable(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }

            // 采用concatMap（）变换操作符
        }).flatMapIterable(new Function<Integer, Iterable<String>>() {
            @Override
            public Iterable<String> apply(Integer integer) throws Exception {
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("我是事件 " + integer + "拆分后的子事件" + i);
                    // 通过concatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                    // 最终合并，再发送给被观察者
                }
                return list;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });

    }
}
