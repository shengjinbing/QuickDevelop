package com.modesty.quickdevelop.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * 1.invalidate()和requestLayout()方法调用有什么区别？
 * 第一次进入onMeasure调用两次
 * view：
 * invalidate()之后：onDraw
 * requestLayout()之后：onMeasure->onLayout->onDraw
 * viewGroup：
 * 第一次进入onLayout(changed = true)这是这个视图的新尺寸或位置
 * invalidate()之后：没有任何方法调用(如果LayoutParams和一些条件发生改变这里影响回调的方法会增多)
 * requestLayout()之后：onMeasure->onLayout(changed = false);
 *
 *
 * 2.无论是 requestLayout 还是 invalidate 方法最后都会调用到 ViewRootImpl.performTraversals 方法开始 View 的更新
 * 3.调用 View.invalidate() 方法后会逐级往上调用父 View 的相关方法，最终在 Choreographer 的控制下调用
 *   ViewRootImpl.performTraversals() 方法。由于 mLayoutRequested == false，因此只有满足 mFirst ||
 *   windowShouldResize || insetsChanged || viewVisibilityChanged || params != null ... 等条件才会执
 *   行 measure 和 layout 流程，否则只执行 draw 流程，draw 流程的执行过程与是否开启硬件加速有关：
 *
 *    关闭硬件加速则从 DecorView 开始往下的所有子 View 都会被重新绘制。
 *    开启硬件加速则只有调用 invalidate 方法的 View 才会重新绘制。
 *
 *    View 在绘制后会设置 PFLAG_DRAWN 标志位。
 *
 * 作者：苍耳叔叔
 * 链接：https://juejin.cn/post/6904518722564653070
 * 来源：掘金
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */
public class CustomViewGroup1 extends ViewGroup {
    public static final String TAG = "ViewGroup_log";

    public CustomViewGroup1(Context context) {
        this(context,null);
    }

    public CustomViewGroup1(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomViewGroup1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG,"onMeasure1");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG,"onLayout1");
        Log.d(TAG,"changed1=="+changed+"l=="+l+"t=="+t+"r=="+r+"b=="+b);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            childAt.layout(l,t,r,b);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG,"onDraw1");
        super.onDraw(canvas);
    }
}
