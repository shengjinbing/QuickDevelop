package com.modesty.quickdevelop.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.modesty.quickdevelop.ui.activitys.DispatchActivity;

/**
 * Created by lixiang
 * on 2018/12/18
 */
@SuppressLint("AppCompatCustomView")
public class CustomTextView extends TextView {

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(DispatchActivity.TAG,"dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }

    /*@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    Log.d(DispatchActivity.TAG,"onInterceptTouchEvent");
        return super.onInterceptTouchEvent(ev);
    }*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(DispatchActivity.TAG,"onTouchEvent");
        return super.onTouchEvent(event);
    }
}
