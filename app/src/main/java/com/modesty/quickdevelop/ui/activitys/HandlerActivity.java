package com.modesty.quickdevelop.ui.activitys;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.modesty.logger.simplelog.Logger;
import com.modesty.quickdevelop.R;

public class HandlerActivity extends AppCompatActivity {

    private ThreadLocal<Boolean> mBooleanThreadLocal = new ThreadLocal<>();
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler);
        initView();
        initData();
        startThread3();
    }

        private void initData() {
           /* handler.post(new Runnable() {
                @Override
                public void run() {
                    Logger.d("handler的一次post"+Thread.currentThread());
                }
            });*/

        }

        private static Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Logger.d(msg.toString());
            return false;
        }
    });
    private void initView() {
        Button button = (Button) findViewById(R.id.btn_click);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBooleanThreadLocal.set(true);
                Logger.d("mainThread1#####mBooleanThreadLocal=="+mBooleanThreadLocal.get());
                startThread1();
                startThread2();
                if (mHandler != null){
                    //Message message = new Message();
                    Message message = Message.obtain();//从缓存池中取，避免创建对象的额外花销
                    message.what = 111;
                    message.obj = "你好啊";
                    mHandler.sendMessage(message);
                }

            }
        });

    }

    private void startThread1(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBooleanThreadLocal.set(false);
                Logger.d("startThread1#####mBooleanThreadLocal=="+mBooleanThreadLocal.get());
            }
        }).start();
    }
    private void startThread2(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.d("startThread1#####mBooleanThreadLocal=="+mBooleanThreadLocal.get());
            }
        }).start();
    }
    private void startThread3(){
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
                //会直接退出
                //looper.quit();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                    //安全退出，消息队列中的消息处理完毕以后再安全退出
//                    looper.quitSafely();
//                }

            }
        }).start();
    }
}
