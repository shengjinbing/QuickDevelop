package com.modesty.quickdevelop.ui.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.modesty.quickdevelop.R;

/**
 * 1.layout_constrainedWidth约束宽度
 * 2.约束宽度之后layout_constraintHorizontal_bias靠左边
 * 3.用于TextView的layout_constraintBaseline_toBaselineOf基线
 * 4.layout_goneMarginStart左边gone的时候设置边距
 * 5.layout_constraintDimensionRatio宽高比 必须确定一个（宽高都没有确定值H，2：1 或者 W,2:1）
 * 6.layout_constraintWidth_percent相对父布局的百分比
 * 7.layout_constraintHorizontal_chainStyle设置在第一个组件上
 * 8.Guideline设置一条辅助线
 */
public class ConstraintLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constraint_layout);
    }
}