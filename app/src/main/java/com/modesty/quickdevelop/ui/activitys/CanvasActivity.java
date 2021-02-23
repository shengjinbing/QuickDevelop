package com.modesty.quickdevelop.ui.activitys;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.views.custom.CanvasView;


/**
 * Android Project Butter分析 https://blog.csdn.net/innost/article/details/8272867
 * 1.Project Butter对Android Display系统进行了重构，引入了三个核心元素，即VSYNC、Triple Buffer和Choreographer。
 * 其中，VSYNC是理解Project Buffer的核心。VSYNC是Vertical Synchronization（垂直同步）的缩写，是一种在PC上已经很早就广泛使用的技术。读者可简单的把它认为是一种定时中断。
 */
public class CanvasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        CanvasView canvasView = (CanvasView) findViewById(R.id.cv);
        Button button = (Button) findViewById(R.id.btn_invatitle);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.setColor(Color.GREEN);
                canvasView.invalidate();
            }
        });
    }
}