package com.modesty.quickdevelop.ui.activitys;

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
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class RxjavaChanageActivity extends AppCompatActivity {
    public static final String TAG = "RxjavaChanage_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava_chanage);

    }

    public void normalNet(View view) {
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
     * 新合并生成的事件序列顺序是有序的，即 严格按照旧序列发送事件的顺序
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
                        Log.d(TAG, " 缓存区里的事件数量 = " +  stringList.size());
                        for (Integer value : stringList) {
                            Log.d(TAG, " 事件 = " + value);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应" );
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }
                });

    }
}
