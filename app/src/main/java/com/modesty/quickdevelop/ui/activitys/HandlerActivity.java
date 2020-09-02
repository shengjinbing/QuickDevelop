package com.modesty.quickdevelop.ui.activitys;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LogPrinter;
import android.util.Printer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.modesty.logger.simplelog.Logger;
import com.modesty.quickdevelop.R;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * 1.MessageQueue虽然叫消息队列但是内部存储的数据结构是单链表;
 *   enqueueMessage()如果不是延迟消息直接加入链表头部，否者按时间比较（从小到大）将数据插入到合适的位子；
 *   如果加入的消息是异步的needWake = false;
 *
 * 2.通过ThreadLocal可以在轻松获取每个线程的Looper
 * 3.UI线程也就是ActivityThread，ActivityThread被创建的时候就会初始化Looper
 * 4.ThreadLocal里面保存的是Looper, 由下面源码可以看出来两次prepare方法调用会抛异常
 * private static void prepare(boolean quitAllowed) {
 * if (sThreadLocal.get() != null) {
 * throw new RuntimeException("Only one Looper may be created per thread");
 * }
 * sThreadLocal.set(new Looper(quitAllowed));
 * }
 * 5.只能在UI线程更新UI，ViewRootImpl对UI进验证，checkThread()方法。
 * 6.为什么不允许在子线程中更新UI？
 * （1）因为子线程更新UI并不是线程安全的。
 * （2）加锁机制会让UI访问变得复杂。
 * （3）加锁会降低UI的访问效率。
 * <p>
 * <p>
 * **************************Looper*******************************
 * looper的两个退出方法：quit直接退出Looper，而quitSafely会将消息队列中的消息安全的处理完毕后才安全的退出。
 * <p>
 * <p>
 * <p>
 * ***************重要的Looper消息循环和阻塞*********************
 * 1.postDelay()一个1秒钟的MyTask任务、消息进队，MessageQueue开始阻塞，Looper阻塞，mBlocked为true，在enqueueMessage的if中将needWake = mBlocked。
 * <p>
 * 2.然后post一个新的任务、消息进队，判断现在A时间还没到、正在阻塞，把新的任务插入消息队列的头部（MyTask任务的前面），然后此时needWake为true调用nativeWake()方法唤醒线程。
 * <p>
 * 3.MessageQueue.next()方法被唤醒后，重新开始读取消息链表，第一个消息B无延时，直接返回给Looper；
 * <p>
 * 4.Looper处理完这个消息再次调用next()方法，MessageQueue继续读取消息链表，第二个消息A还没到时间，计算一下剩余时间（假如还剩9秒）继续阻塞；
 * <p>
 * 5.直到阻塞时间到或者下一次有Message进队；
 *
 *
 *
 *  * ***************重要的4中引用消息循环和阻塞*********************
 *  1.软引用可用来实现内存敏感的高速缓存。软引用可以和一个引用队列（ReferenceQueue）联合使用，如果软引用所引用的对象被垃圾回收，
 *    Java虚拟机就会把这个软引用加入到与之关联的引用队列中。
 */
public class HandlerActivity extends AppCompatActivity {
    public static final String TAG = "HANDLER_LOG";



    //在Activity关闭的地方将线程停止以及把Handler的消息队列的所有消息对象移除
    //Handler改为静态类
    private static class MyHandler extends Handler{
        //在最常见的Handler持有一个Activity的引用,Handler作为一个耗时的异步线程处理,如果在处理过程中把Activity关闭了
        // 因为Handler还持有Activity的引用,而一个异步线程持有Handler引用,那么就将导致内存泄漏
        private WeakReference<HandlerActivity> reference;

        private ReferenceQueue mQueue = new ReferenceQueue();

        public MyHandler(HandlerActivity activity) {
            reference = new WeakReference<HandlerActivity>(activity,mQueue);
            Reference reference = mQueue.poll();
            if (reference != null){
                //利用这个方法，我们可以检查哪个WeakReference所软引用的对象已经被回收（SolfReference一样的用法）
            }
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    HandlerActivity weakReferenceActivity = (HandlerActivity) reference.get();
                    if (weakReferenceActivity != null) {
                        System.out.print("WeakReferenceActivity");
                    }
                    break;

                default:
                    break;
            }
        }
    }



    private static Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what;
            Log.d(TAG,what+msg.toString());
            return false;
        }
    });
    Handler mainHandler, workHandler;
    HandlerThread mHandlerThread;
    TextView text;
    private ThreadLocal<Boolean> mBooleanThreadLocal = new ThreadLocal<>();
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler);
        initView();
        initData();
        startThread3();
        initHandlerThread();
        initThreadLocal();
        getMainLooper().setMessageLogging(new Printer() {
            @Override
            public void println(String x) {

            }
        });
        startThread4();
    }

    private void initView() {
        // 显示文本
        text = (TextView) findViewById(R.id.text1);
        Button button = (Button) findViewById(R.id.btn_click);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBooleanThreadLocal.set(true);
                Logger.d("mainThread1#####mBooleanThreadLocal==" + mBooleanThreadLocal.get());
                startThread1();
                startThread2();
                if (mHandler != null) {
                    //Message message = new Message();
                    Message message = Message.obtain();//从缓存池中取，避免创建对象的额外花销
                    message.what = 111;
                    message.obj = "你好啊";
                    mHandler.sendMessage(message);
                }

            }
        });

    }

    private void initData() {
           /* handler.post(new Runnable() {
                @Override
                public void run() {
                    Logger.d("handler的一次post"+Thread.currentThread());
                }
            });*/
        Message obtain1 = Message.obtain();
        obtain1.what = 1;
        handler.sendMessage(obtain1);

        Message obtain2 = Message.obtain();
        obtain2.what = 2;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendMessage(obtain2);
            }
        }, 5000);

        Message obtain3 = Message.obtain();
        obtain3.what = 3;
        handler.sendMessage(obtain3);

    }

    private void startThread3() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        Logger.d(String.valueOf(msg.obj));
                        return false;
                    }
                });
                Looper.loop();
                Looper looper = mHandler.getLooper();
                // looper.setMessageLogging();
                //会直接退出
                //looper.quit();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                    //安全退出，消息队列中的消息处理完毕以后再安全退出
//                    looper.quitSafely();
//                }

            }
        }).start();
    }

    /**
     * 测试子线程创建Hanhder失败
     */
    private void startThread4(){
        new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                Handler handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {

                        return false;
                    }
                });
                super.run();
            }
        }.start();
    }

    /*********************HandlerThread相关*******************************/
    private void initHandlerThread() {
        // 创建与主线程关联的Handler
        mainHandler = new Handler();

        /**
         * 步骤1：创建HandlerThread实例对象
         * 传入参数 = 线程名字，作用 = 标记该线程
         */
        mHandlerThread = new HandlerThread("handlerThread");

        /**
         * 步骤2：启动线程
         */
        mHandlerThread.start();

        /**
         * 步骤3：创建工作线程Handler & 复写handleMessage（）
         * 作用：关联HandlerThread的Looper对象、实现消息处理操作 & 与其他线程进行通信
         * 注：消息处理操作（HandlerMessage（））的执行线程 = mHandlerThread所创建的工作线程中执行
         */

        workHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            // 消息处理的操作
            public void handleMessage(Message msg) {
                //设置了两种消息处理操作,通过msg来进行识别
                switch (msg.what) {
                    // 消息1
                    case 1:
                        try {
                            //延时操作
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // 通过主线程Handler.post方法进行在主线程的UI更新操作
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                text.setText("我爱学习");
                            }
                        });
                        break;

                    // 消息2
                    case 2:
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                text.setText("我不喜欢学习");
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        };

    }

    /**
     *
     */
    private void initThreadLocal() {
        ThreadLocal<Object> threadLocal = new ThreadLocal<>();
    }

    private void startThread1() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBooleanThreadLocal.set(false);
                Logger.d("startThread1#####mBooleanThreadLocal==" + mBooleanThreadLocal.get());
            }
        }).start();
    }

    private void startThread2() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.d("startThread1#####mBooleanThreadLocal==" + mBooleanThreadLocal.get());
            }
        }).start();
    }

    public void button1(View view) {
        // 通过sendMessage（）发送
        // MVPActivityModelImpl. 定义要发送的消息
        Message msg = Message.obtain();
        msg.what = 1; //消息的标识
        msg.obj = "A"; // 消息的存放
        // b. 通过Handler发送消息到其绑定的消息队列
        workHandler.sendMessage(msg);

    }

    public void button2(View view) {
        // 通过sendMessage（）发送
        // MVPActivityModelImpl. 定义要发送的消息
        Message msg = Message.obtain();
        msg.what = 2; //消息的标识
        msg.obj = "B"; // 消息的存放
        // b. 通过Handler发送消息到其绑定的消息队列
        workHandler.sendMessage(msg);

    }

    public void button3(View view) {
        mHandlerThread.quit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //
        handler.removeCallbacksAndMessages(null);
    }
}
