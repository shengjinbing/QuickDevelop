package com.modesty.quickdevelop.ui.view.behaviors;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by lixiang on 2019/7/24
 */
public class SampleTitleBehavior extends CoordinatorLayout.Behavior<View> {
    public static final String TAG = "SampleTitleBehavior";

    // 列表顶部和title底部重合时，列表的滑动距离。
    private float deltaY;

    public SampleTitleBehavior() {
    }

    public SampleTitleBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 使用该Behavior的View要监听哪个类型的View的状态变化。
     * @param parent 代表CoordinatorLayout
     * @param child 代表使用该Behavior的View
     * @param dependency 代表要监听的View
     * @return
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof NestedScrollView;
    }

    /**
     * 当被监听的View状态变化时会调用该方法，参数和上一个方法一致。所以我们重写该方法，当RecyclerView的位置变化时，
     * 进而改变title的位置。
     * @param parent
     * @param child
     * @param dependency
     * @return
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        Log.d(TAG,child.getY()+"");
        if (deltaY == 0) {
            deltaY = dependency.getY() - child.getHeight();
        }
        float dy = dependency.getY() - child.getHeight();
        dy = dy < 0 ? 0 : dy;
        float y = -(dy / deltaY) * child.getHeight();
        float alpha = 1 -(dy / deltaY);
        //平移
        //child.setTranslationY(y);
        //透明度
        child.setAlpha(alpha);
        return true;
    }
}
