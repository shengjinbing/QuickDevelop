package com.modesty.quickdevelop.ui.activitys;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LogPrinter;
import android.util.Printer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.modesty.logger.simplelog.Logger;
import com.modesty.quickdevelop.R;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * 一.Handler的源码和常见问题的解答
 * 1.一个线程中最多有多少个Handler，Looper，MessageQueue？
 *   1个
 * 2.Looper死循环为什么不会导致应用卡死，会耗费大量资源吗？
 *   阻塞
 * 3.子线程的如何更新UI，比如Dialog，Toast等？系统为什么不建议子线程中更新UI?
 *   子线程创建Looper；
 *  （1）因为子线程更新UI并不是线程安全的。
 *  （2）加锁机制会让UI访问变得复杂。
 *  （3）加锁会降低UI的访问效率。
 * 4.主线程如何访问网络？
 * 5.如何处理Handler使用不当造成的内存泄漏？
 *   1.有延时消息，在界面关闭后及时移除Message/Runnable，调用handler.removeCallbacksAndMessages(null)
 *   2.内部类导致的内存泄漏改为静态内部类，并对上下文或者Activity/Fragment使用弱引用
 * 6.Handler的消息优先级，有什么应用场景？
 *   1.可以看到Handler.Callback 有优先处理消息的权利 ，当一条消息被 Callback 处理并拦截（返回 true），那么 Handler
 *   的 handleMessage(msg) 方法就不会被调用了；如果 Callback 处理了消息，但是并没有拦截，那么就意味着一个消息可以同时被
 *   Callback 以及 Handler 处理。我们可以利用CallBack这个拦截来拦截Handler的消息。
 *   2.场景：Hook ActivityThread.mH ， 在 ActivityThread 中有个成员变量 mH ，它是个 Handler，又是个极其重要的类，几乎所有的插件化框架都使用了这个方法。
 *
 * 7.主线程的Looper何时退出？能否手动退出？
 *   在App退出时，ActivityThread中的mH（Handler）收到消息后，执行退出。手动退出会异常主线程不允许结束
 * 8.如何判断当前线程是安卓主线程？
 * 9.正确创建Message实例的方式？
 *   通过 Message 的静态方法 Message.obtain() 获取；
 *   通过 Handler 的公有方法 handler.obtainMessage()
 *   所有的消息会被回收，放入sPool中，使用享元设计模式
 * 10.Handler如何切换线程的？
 *    原理很简单，线程间是共享资源的，子线程通过handler.sendXXX，handler.postXXX等方法发送消息，然后通过Looper.loop()
 *    在消息队列中不断的循环检索消息，最后交给handle.dispatchMessage方法进行消息的分发处理
 *
 *
 *
 * 二.Handler深层次问题解答
 * 1.ThreadLocal?
 *   1.ThreadLocal.ThreadLocalMap threadLocals = null;是存在线程中的。所以ThreadLocal的get方法，其实就是拿到每个线程独有的ThreadLocalMap。
 * 2.epoll机制?
 *  其实不然，这里就涉及到 Linux  pipe/epoll机制，简单说就是在主线程的 MessageQueue 没有消息时，便阻塞在 loop 的
 *  queue.next() 中的 nativePollOnce() 方法里，此时主线程会释放 CPU 资源进入休眠状态，直到下个消息到达或者有事务发生，通
 *  过往 pipe 管道写端写入数据来唤醒主线程工作。这里采用的 epoll 机制，是一种IO多路复用机制，可以同时监控多个描述
 *  符，当某个描述符就绪(读或写就绪)，则立刻通知相应程序进行读或写操作，本质同步I/O，即读写是阻塞的。 所以说，主线程大多数时
 *  候都是处于休眠状态，并不会消耗大量CPU资源。
 *  https://juejin.cn/post/6893791473121280013#heading-17
 *  https://mp.weixin.qq.com/s/Qnser4SMoRtgEPd74oDJGQ
 *  https://www.zhihu.com/question/20122137/answer/14049112
 *  （重点）
 * 3.那么新的问题就来了，这里为什么选择Socket而不是选择Binder呢，关于这个问题的解释，笔者找到了一个很好的版本：
 * Socket可以实现异步的通知，且只需要两个线程参与（Pipe两端各一个），假设系统有N个应用程序，跟输入处理相关的线程数目是 N+1
 * (1是Input Dispatcher线程）。然而，如果用Binder实现的话，为了实现异步接收，每个应用程序需要两个线程，一个Binder线程，
 * 一个后台处理线程（不能在Binder线程里处理输入，因为这样太耗时，将会堵塞住发送端的调用线程）。在发送端，同样需要两个线程，
 * 一个发送线程，一个接收线程来接收应用的完成通知，所以，N个应用程序需要 2（N+1)个线程。相比之下，Socket还是高效多了。
 *
 *
 *
 * 3.Handle同步屏障机制
 *   1.Handler中加入了同步屏障这种机制，来实现[异步消息优先]执行的功能
 * 4.Handler的锁相关问题?
 * 5.Handler中的同步方法?
 * 6.如何在子线程通过 Handler 向主线程发送一个任务，并等主线程处理此任务后，再继续执行?
 *   Handler的runWithScissors()方法（@hide方法无法调用但是可以自定义一个相同功能的使用）要满足 2 个条件:
 *   1.Handler 的 Looper 不允许退出，例如 Android 主线程 Looper 就不允许退出;
 *   2.Looper 退出时，使用安全退出 quitSafely() 方式退出;
 *
 *
 *3.Handler在系统以及第三方框架的一些应用
 * HandlerThread
 * IntentService
 * 如何打造一个不崩溃的APP
 * Glide中的运用
 *
 *
 *
 *  * ***************重要的4中引用消息循环和阻塞*********************
 *  1.软引用可用来实现内存敏感的高速缓存。软引用可以和一个引用队列（ReferenceQueue）联合使用，如果软引用所引用的对象被垃圾回收，
 *    Java虚拟机就会把这个软引用加入到与之关联的引用队列中。
 *  2.LifecycleRegistry的mLifecycleOwner弱引用。
 *  3.ThreadLocal
 *  4.LeakCanary实现原理
 *  5.glide为什么使用WeakReference而不是SoftReference？
 *    1.这样可以保护当前使用的资源不会被 LruCache 算法回收，分担lrucache的压力
 *
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
            SystemClock.sleep(100000);
            Log.d(TAG,"休息结束");
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
        //initHandlerThread();
        initThreadLocal();
        //设置消息打印的日志
        getMainLooper().setMessageLogging(new Printer() {
            @Override
            public void println(String x) {

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initIdeaHandle();
        }
    }

    /**
     * 队列空，只有延时消息并且没到时间，同步阻塞时没有异步消息
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initIdeaHandle() {
        getMainLooper().getQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                //Log.d(TAG,"MessageQueue空闲了");
                return true;
            }
        });
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

    public void button4(View view) {
         /* handler.post(new Runnable() {
                @Override
                public void run() {
                    Logger.d("handler的一次post"+Thread.currentThread());
                }
            });*/
        Message obtain1 = Message.obtain(handler, new Runnable() {
            @Override
            public void run() {
                //Message的callback
                Log.d(TAG,"Message的callback");
            }
        });
        obtain1.what = 1;
        handler.sendMessage(obtain1);

        Message obtain2 = Message.obtain();
        obtain2.what = 2;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //这个Runnable最后被封装成Message的CallBack（享受最高优先级，原来如此）
                handler.sendMessage(obtain2);
            }
        }, 5000);

        Message obtain3 = Message.obtain();
        obtain3.what = 3;
        handler.sendMessage(obtain3);
    }


    private AlertDialog.Builder builder;
    AlertDialog alertDialog;

    /**
     * 子线程创建View
     * 1.Window是Android中的窗口，每个Activity和Dialog，Toast分别对应一个具体的Window，Window是一个抽象的概念，
     *  每一个Window都对应着一个View和一个ViewRootImpl，Window和View通过ViewRootImpl来建立联系，因此，它是以View的形式存在的。
     */
    public void button5(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //创建Looper，MessageQueue
                Looper.prepare();
                Toast.makeText(getApplicationContext(),"hahhahaha",Toast.LENGTH_SHORT).show();
                builder = new AlertDialog.Builder(HandlerActivity.this);
                builder.setTitle("jackie");
                alertDialog = builder.create();
                alertDialog.show();
               /* new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        builder = new AlertDialog.Builder(HandlerActivity.this);
                        builder.setTitle("jackie");
                        alertDialog = builder.create();
                        alertDialog.show();
                        //alertDialog.hide();
                    }
                });*/
                //开始处理消息
                Looper.loop();
            }
        }).start();
    }

    /**
     * @param view
     */
    public void button6(View view) {

    }

        @Override
    protected void onDestroy() {
        super.onDestroy();
        //
        handler.removeCallbacksAndMessages(null);
    }
}
/**
 * 5.只能在UI线程更新UI，ViewRootImpl对UI进验证，checkThread()方法。
 *  为什么不允许在子线程中更新UI？
 *  （1）因为子线程更新UI并不是线程安全的。
 *  （2）加锁机制会让UI访问变得复杂。
 *  （3）加锁会降低UI的访问效率。
 *
 */

/**
 * 重要的总结
 * 1.Message是有缓存池的，每次回收利用
 * 2.MessageQueue是一个由单链表构成的优先级队列（取的都是头部，所以说是队列）
 * 3.每个线程中只有一个Looper和MessageQueue。
 * 4.sendMessageAtFrontOfQueue() 方法，这个方法是将这个 Message 直接放到 MessageQueue 队列里的头部，可以理解成设置了这个 Message 为最高优先级
 */
