package com.modesty.quickdevelop.ui.activitys.frame;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.modesty.quickdevelop.R;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * 1.版本升级以后为什么性能提升一倍
 * 2.GC Root(class对象，静态变量)短生命周期被长生命周期的类引用
 */
public class LeakCanaryActivity extends AppCompatActivity {
    public static final String TAG = "LeakCanaryActivity_LOG";

    public static ReferenceQueue<WeakReference> referenceQueue = new ReferenceQueue<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Looper.prepare();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak_canary);
        //创建引用队列，关联弱引用
        WeakReference<Activity> weakReference = new WeakReference(this,referenceQueue);
        LeakCanaryActivity activity = (LeakCanaryActivity) weakReference.get();
        Log.d(TAG,activity.getClass().getName());
        Log.d(TAG,referenceQueue.poll()+"");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //查看当前activity销毁的时候引用队列中是否有值
    }
}