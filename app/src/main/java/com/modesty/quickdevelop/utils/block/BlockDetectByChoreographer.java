package com.modesty.quickdevelop.utils.block;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Choreographer;

import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;

/**
 * Created by lixiang on 2020/9/1
 * Describe:
 * 1。而且Choreographer刚好也提供了一个postFrameCallback
 * 方法供开发者使用。该方法的作用就是在下一帧的时候，会触发我们向Choreographer注册的callback回调。
 * 2.Choreographer就是负责获取Vsync同步信号并控制App线程(主线程)完成图像绘制的类。
 * 3.每个线程中保存一个Choreographer实例对象
 * private static final ThreadLocal<Choreographer> sThreadInstance =
 *             new ThreadLocal<Choreographer>() {
 *     @Override
 *     protected Choreographer initialValue() {
 *         Looper looper = Looper.myLooper();
 *         if (looper == null) {
 *             //抛出异常。
 *         }
 *         return new Choreographer(looper);
 *     }
 * };
 * 4.同一个App的每个窗体旗下ViewRootImpl使用的同一个Choregrapher对象，他控制者整个App中大部分视图的绘制节奏。
 *
 * CALLBACK_INPUT：输入
 * CALLBACK_ANIMATION:动画
 * CALLBACK_TRAVERSAL:遍历，执行measure、layout、draw
 * CALLBACK_COMMIT：遍历完成的提交操作，用来修正动画启动时间
 *
 */
public class BlockDetectByChoreographer {

    private static String TAG ="BlockDetectByChoreographer";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void start(){
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback(){
            long lastFrameTimeNos = 0;
            @Override
            public void doFrame(long frameTimeNanos) {
                if (lastFrameTimeNos == 0){
                    //第一次初始化不做检测
                    lastFrameTimeNos = frameTimeNanos;
                }else {
                    long diffMs = TimeUnit.MILLISECONDS.convert(frameTimeNanos - lastFrameTimeNos,
                            TimeUnit.MILLISECONDS);
                    int frames = (int) (diffMs / 16.7);
                    if (diffMs > 16.7){
                        Log.w(TAG, "UI线程超时(超过16ms):" + diffMs + "ms" + " , 丢帧:" + frames);
                    }
                    lastFrameTimeNos = frameTimeNanos;
                }

                Choreographer.getInstance().postFrameCallback(this);
            }
        });
    }
}
