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
 * 而且Choreographer刚好也提供了一个postFrameCallback
 * 方法供开发者使用。该方法的作用就是在下一帧的时候，会触发我们向Choreographer注册的callback回调。
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
