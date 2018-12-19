package com.modesty.quickdevelop.ui.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.ui.view.CustomTextView;

public class DispatchActivity extends AppCompatActivity {

    public static final String TAG = "DISPATCHACTIVITY_LOG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);
        initView();
    }

    /**
     * 1.调用顺序为onTouch--->onTouchEvent------>onClick;onTouch的返回值为false才会触发onTouchEvent和onClick的回调
     * 2.ViewGroup默认不拦截任何事件，onInterceptTouchEvent默认返回false
     * 3.view没有onInterceptTouchEvent方法，会直接调用onTouchEvent方法
     * 4，View的onTouchEvent返回值为true的时候默认会消耗掉事件，除非clickable和longclickable同时为false，
     *    view的long事件默认是关闭的。
     * 5.事件传递是由外向内，事件总是县传递给父元素（Activity--->window（会将事件传给decor view）----->view）,requestDisallowInterceptTouchEvent
     *    方法可以再子元素中干预父元素的事件分发过程，但是ACTION_DOWN除外。
     */
    private void initView() {
        CustomTextView textView = (CustomTextView) findViewById(R.id.tv);
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(DispatchActivity.TAG,"onTouch");
                return true;
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DispatchActivity.TAG,"onClick");
            }
        });
    }
}
