package com.modesty.quickdevelop.ui.activitys;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.modesty.logger.simplelog.Logger;
import com.modesty.quickdevelop.R;

/**
 * 1.MessageQueue虽然叫消息队列但是内部存储的数据结构是单链表
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
 */
public class HandlerActivity extends AppCompatActivity {
    public static final String TAG = "HANDLER_LOG";

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
        // a. 定义要发送的消息
        Message msg = Message.obtain();
        msg.what = 1; //消息的标识
        msg.obj = "A"; // 消息的存放
        // b. 通过Handler发送消息到其绑定的消息队列
        workHandler.sendMessage(msg);

    }

    public void button2(View view) {
        // 通过sendMessage（）发送
        // a. 定义要发送的消息
        Message msg = Message.obtain();
        msg.what = 2; //消息的标识
        msg.obj = "B"; // 消息的存放
        // b. 通过Handler发送消息到其绑定的消息队列
        workHandler.sendMessage(msg);

    }

    public void button3(View view) {
        mHandlerThread.quit();
    }
}
