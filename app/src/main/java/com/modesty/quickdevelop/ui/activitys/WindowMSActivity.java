package com.modesty.quickdevelop.ui.activitys;

import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;

import com.modesty.quickdevelop.R;

import retrofit2.http.PATCH;

public class WindowMSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_ms);
        initView();
    }

    private void initView() {
        createWindow();
    }

    /**
     * FLAG_NOT_TOUCH_MODAL 自己接受点击事件
     * FLAG_NOT_FOCUSABLE 不需要获取焦点
     * FLAG_SHOW_WHEN_LOCKED 开启此模式可以让window显示在锁屏的界面上
     *
     * Type参数表示Window的类型，有三种类型分别是应用Window，子Window和系统Window。
     * 1.应用window对应着一个Activity.
     * 2.子window不能单独存在，它需要附属在特定的父Window之中，比如常见的Dialog就是一个子Window。
     * 3.系统window是需要声明权限在能创建的window，比如Toast和系统的状态栏这些都是系统的window。
     */
    private void createWindow(){
        WindowManager windowManager = getWindowManager();
        Button button = new Button(this);
        button.setText("button");
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT
                , 0, 0, PixelFormat.TRANSLUCENT);
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                |WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                |WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        params.gravity = Gravity.LEFT|Gravity.TOP;
        params.x = 100;
        params.y = 300;
        windowManager.addView(button,params);
    }
}
