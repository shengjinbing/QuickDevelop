package com.modesty.quickdevelop.ui.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.modesty.quickdevelop.R;

public class CustomViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_view);
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
     *
     * Camera
     * 我们的手机屏幕是一个2D的平面，所以也没办法直接显示3D的信息，因此我们看到的所有3D效果都是3D在2D平面的投影而已，
     * 而本文中的Camera主要作用就是这个，将3D信息转换为2D平面上的投影，实际上这个类更像是一个操作Matrix的工具类，
     * 使用Camera和Matrix可以在不使用OpenGL的情况下制作出简单的3D效果。
     * 1.Android 上面观察View的摄像机默认位置在屏幕左上角，而且是距屏幕有一段距离的
     * 2.摄像机的位置默认是 (0, 0, -576)。其中 -576＝ -8 x 72，虽然官方文档说距离屏幕的距离是 -8,
     *   但经过测试实际距离是 -576 像素，当距离为 -10 的时候，实际距离为 -720 像素。
     */
    private void initView() {

    }

    private void initData() {

    }

    private void initListener() {

    }
}
