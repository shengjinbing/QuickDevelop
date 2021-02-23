package com.modesty.quickdevelop.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.views.CircleView;
import com.modesty.quickdevelop.views.CustomViewGroup;
import com.modesty.quickdevelop.views.CustomViewGroup1;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *①onMeasure、onLayout、onDraw
 * ②MeasureSpec为何如此设计？
 * ③子View的LayoutParams来源。ViewGroup#addView(view)这种添加view的方式，没有给子View设置LayoutParams，那么LayoutParams是谁设置的？
 * ④onMeasure和onLayout为何会执行两次或多次？
 * ⑤View#draw方法细节。
 * ⑥View绘制这一块遇到过什么问题么？如何解决的。
 *
 * 自定义View有哪几种方式？注意事项。你对自定义属性如何理解？
 * 事件分发。滑动冲突如何解决，具体在哪个方法里面解决？如何判断滑动方向？
 *
 * 1.竖向的TextView如何实现。TextView文字描边效果如何实现?
 * 原生：android:ems=”1” 设置每行只显示1个字符，数字和字母支持不友好（一行显示多个）
 * 描边就是再创建一个TextView
 * 2.View绘制流程。onMeasure、onLayout、onDraw。
 * 3.事件分发。冲突解决。
 * 4.MeasureSpec讲一下
 *
 */
public class CustomViewActivity extends AppCompatActivity {
    @BindView(R.id.fl)
    ViewFlipper fl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_view);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();

    }

    /**
     * Matrix
     * 1.Matrix是一个矩阵，主要功能是坐标映射，数值转换。
     * 2.作用范围更广，Matrix在View，图片，动画效果等各个方面均有运用，相比与之前讲解等画布操作应用范围更广。
     * 3.更加灵活，画布操作是对Matrix的封装，Matrix作为更接近底层的东西，必然要比画布操作更加灵活。
     * 4.封装很好，Matrix本身对各个方法就做了很好的封装，让开发者可以很方便的操作Matrix
     * 5.难以深入理解，很难理解中各个数值的意义，以及操作规律，如果不了解矩阵，也很难理解前乘，后乘
     * Camera
     * 我们的手机屏幕是一个2D的平面，所以也没办法直接显示3D的信息，因此我们看到的所有3D效果都是3D在2D平面的投影而已，
     * 而本文中的Camera主要作用就是这个，将3D信息转换为2D平面上的投影，实际上这个类更像是一个操作Matrix的工具类，
     * 使用Camera和Matrix可以在不使用OpenGL的情况下制作出简单的3D效果。
     * 1.Android 上面观察View的摄像机默认位置在屏幕左上角，而且是距屏幕有一段距离的
     * 2.摄像机的位置默认是 (0, 0, -576)。其中 -576＝ -8 x 72，虽然官方文档说距离屏幕的距离是 -8,
     * 但经过测试实际距离是 -576 像素，当距离为 -10 的时候，实际距离为 -720 像素。
     */
    private void initView() {
        for (int i = 0; i < 10; i++) {
            TextView textView = new TextView(this);
            textView.setText("hahah"+i);
            fl.addView(textView);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView view = (TextView) fl.getChildAt(0);
                view.setTextColor(getResources().getColor(R.color.colorAccent));
                fl.showNext();
            }
        },1000);
    }

    private void initData() {

    }

    private void initListener() {
        CustomViewGroup cvg = (CustomViewGroup) findViewById(R.id.cvg);
        CustomViewGroup1 cvg1 = (CustomViewGroup1) findViewById(R.id.cvg1);
        CircleView circleView = (CircleView) findViewById(R.id.circleView);
        cvg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvg.invalidate();
                // cvg.requestLayout();
                //circleView.invalidate();
                //circleView.requestLayout();
            }
        });
        cvg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvg1.invalidate();
                // cvg.requestLayout();
                //circleView.invalidate();
                //circleView.requestLayout();
            }
        });
        circleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleView.invalidate();
               // cvg.requestLayout();
                //circleView.invalidate();
                //circleView.requestLayout();
            }
        });

    }

    public void canvas(View view) {
        startActivity(new Intent(this,CanvasActivity.class));
    }
}
