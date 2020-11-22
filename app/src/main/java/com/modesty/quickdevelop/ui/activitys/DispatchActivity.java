package com.modesty.quickdevelop.ui.activitys;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.ui.view.CustomTextView;

import java.util.Random;

/**
 * 1.onClick会发生的前提是当前的View是可以点击的，并且收到down和up事件（百度面试被问）
 * 2.setOnClickListener会是clickable变为true（clickable和longClickable）
 * 3.requestDisallowInterceptTouchEvent方法可以在子元素中干预父元素的事件分发过程，但是只适应ACTION_DOWN以外的诗句
 * <p>
 * <p>
 * <p>
 * performMeasure(childWidthMeasureSpec,childHeightMeasureSpec)方法在ViewRootImpl中调用
 * 两次，第一次测量的是window的大小，第二次是decorView的大小。
 * <p>
 * 子线程在在viewRootImpl没有创建出来的时候可以更新UI，checkTread（）方法在viewRootImpl中调用。
 *
 * @Override public void requestLayout() {
 * if (!mHandlingLayoutInLayoutRequest) {
 * checkThread();
 * mLayoutRequested = true;
 * scheduleTraversals();
 * }
 * }
 */
public class DispatchActivity extends AppCompatActivity {

    public static final String TAG = "DISPATCHACTIVITY_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);
        initView();
        TextView tv = findViewById(R.id.tv_text);
        //1
        tv.post(() -> {
            //这里获取宽高要比treeObserver监听要晚
        });

        //2.子线程更新UI操作，需要先进行requestLayout（）
        tv.requestLayout();
        new Thread(() -> {
            //子线程更新UI
            tv.setText("adad");
        }).start();

        //3
        tv.setOnClickListener(v -> {
            new Thread(() -> {
                Looper.prepare();
                Button button = new Button(this);
                getWindowManager().addView(button, new WindowManager.LayoutParams());
                button.setOnClickListener(v1 -> {
                    button.setText("在子线程中更新UI");
                });
                Looper.loop();
            }).start();

        });

        //4
        new Thread(() -> {
            Looper.prepare();
            Toast.makeText(this,"子线程中弹Toast",Toast.LENGTH_LONG).show();
            Looper.loop();
        }).start();

        //5.开启硬件加速，并且固定View的宽高是可以进行子线程更新UI的，主要都是绕过
        // viewRootImpl的requestLayout()方法里面的checkThread();

        //6.
        SurfaceView surfaceView = findViewById(R.id.sv);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                //子线程更新UI，这里可以
                new Thread(() -> {
                    while (true){
                        Canvas canvas = holder.lockCanvas();
                        int r = new Random().nextInt(255);
                        int g = new Random().nextInt(255);
                        int b = new Random().nextInt(255);
                        canvas.drawColor(Color.rgb(r,g,b));
                        holder.unlockCanvasAndPost(canvas);
                        SystemClock.sleep(500);
                    }
                }).start();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        //onResume里面不能获取view的宽高，整个view的测量流程才刚开始，handleResumeActivity（）
    }

    /**
     * 1.调用顺序为onTouch--->onTouchEvent------>onClick;onTouch的返回值为false才会触发onTouchEvent和onClick的回调
     * 2.ViewGroup默认不拦截任何事件，onInterceptTouchEvent默认返回false
     * 3.view没有onInterceptTouchEvent方法，会直接调用onTouchEvent方法
     * 4，View的onTouchEvent返回值为true的时候默认会消耗掉事件，除非clickable和longclickable同时为false，
     * view的long事件默认是关闭的。
     * 5.事件传递是由外向内，事件总是县传递给父元素（Activity--->window（会将事件传给decor view）----->view）,requestDisallowInterceptTouchEvent
     * 方法可以再子元素中干预父元素的事件分发过程，但是ACTION_DOWN除外。
     */
    private void initView() {
        CustomTextView textView = findViewById(R.id.tv);
        textView.setOnTouchListener((v, event) -> {
            Log.d(DispatchActivity.TAG, "onTouch");
            return false;
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DispatchActivity.TAG, "onClick");
            }
        });
    }
}
