package com.modesty.quickdevelop.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.views.CircleView;
import com.modesty.quickdevelop.views.CustomViewGroup;
import com.modesty.quickdevelop.views.CustomViewGroup1;
import com.modesty.quickdevelop.views.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *①onMeasure、onLayout、onDraw
 * ②MeasureSpec为何如此设计？
 * ③子View的LayoutParams来源。ViewGroup#addView(view)这种添加view的方式，没有给子View设置LayoutParams，那么LayoutParams是谁设置的？
 * ④onMeasure和onLayout为何会执行两次或多次？
 * ⑤View#draw方法细节。
 * ⑥View绘制这一块遇到过什么问题么？如何解决的。
 */
public class CustomViewActivity extends AppCompatActivity {

    @BindView(R.id.me)
    MaterialEditText me;

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
     * <p>
     * Camera
     * 我们的手机屏幕是一个2D的平面，所以也没办法直接显示3D的信息，因此我们看到的所有3D效果都是3D在2D平面的投影而已，
     * 而本文中的Camera主要作用就是这个，将3D信息转换为2D平面上的投影，实际上这个类更像是一个操作Matrix的工具类，
     * 使用Camera和Matrix可以在不使用OpenGL的情况下制作出简单的3D效果。
     * 1.Android 上面观察View的摄像机默认位置在屏幕左上角，而且是距屏幕有一段距离的
     * 2.摄像机的位置默认是 (0, 0, -576)。其中 -576＝ -8 x 72，虽然官方文档说距离屏幕的距离是 -8,
     * 但经过测试实际距离是 -576 像素，当距离为 -10 的时候，实际距离为 -720 像素。
     */
    private void initView() {
        for (int i = 0; i < 1; i++) {
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

        me.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Log.d("BBBBB","start=="+start+"before=="+before+"count=="+count);
                if (charSequence == null || charSequence.length() == 0) {
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < charSequence.length(); i++) {
                    if (i != 3 && i != 8 && charSequence.charAt(i) == ' ') {
                        continue;
                    } else {
                        sb.append(charSequence.charAt(i));
                        if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                            sb.insert(sb.length() - 1, ' ');
                        }
                    }
                }
                if (!sb.toString().equals(charSequence.toString())) {
                    int index = start + 1;
                    if (sb.charAt(start) == ' ') {
                        if (before == 0) {
                            index++;
                        } else {
                            index--;
                        }
                    } else {
                        if (before == 1) {
                            index--;
                        }
                    }
                    me.setText(sb.toString());
                    me.setSelection(index);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void canvas(View view) {
        startActivity(new Intent(this,CanvasActivity.class));
    }
}
