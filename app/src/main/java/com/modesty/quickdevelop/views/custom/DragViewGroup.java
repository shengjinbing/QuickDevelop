package com.modesty.quickdevelop.views.custom;

/**
 * Created by lixiang on 2020/10/23
 * Describe:
 */

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class DragViewGroup extends FrameLayout{

    private ViewDragHelper mViewDragHelper;

    private View mFirstView;
    private View mSecondView;

    private int mFirstViewWidth;



    public DragViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mViewDragHelper = ViewDragHelper.create(this, callback);
    }

    public DragViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mViewDragHelper = ViewDragHelper.create(this, callback);
    }

    public DragViewGroup(Context context) {
        super(context);
        mViewDragHelper = ViewDragHelper.create(this, callback);
    }

    /**
     * 关于mDragHelper的拖动等操作都在这个类里面操作
     */
    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        //什么时候开始检测触摸事件
        public boolean tryCaptureView(View child, int pointerId) {

            return mSecondView == child;
        }

        /**
         * 水平方向上的移动，默认的返回值为0，此时将它修改为检测到的left
         */
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            return left;
        }

        /**
         * 垂直方向上的移动，默认值也是0，在这里干脆直接让它返回0
         */
        public int clampViewPositionVertical(View child, int top, int dy) {
            return 0;
        }

        /**
         * 当手指抬起时或者说是拖动结束时会回调这个方法
         */
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            if(mSecondView.getLeft() <100)
            {//如果第二个view的left小于500像素，就不显示第一个view。下面的代码就相当于此

                mViewDragHelper.smoothSlideViewTo(mSecondView, 0, 0);
                ViewCompat.postInvalidateOnAnimation(DragViewGroup.this);
            }else
            {//显示第一个view

                mViewDragHelper.smoothSlideViewTo(mSecondView, 300, 0);
                ViewCompat.postInvalidateOnAnimation(DragViewGroup.this);
            }

        }

    };


    /**
     * 布局加载完成后调用
     * 在这个方法里获取子view
     */
    protected void onFinishInflate() {
        super.onFinishInflate();

        mFirstView = getChildAt(0);
        mSecondView = getChildAt(1);
    }

    /**
     * 子view的大小改变后回调该方法
     * 在这个方法里获取到mFirstView的宽度
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mFirstViewWidth = mFirstView.getMeasuredWidth();
    }


    public boolean onInterceptTouchEvent(android.view.MotionEvent ev)
    {
        //注意一定要将触摸事件拦截下来
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent event)
    {

        //必须将触摸事件传递给mDragHleper
        mViewDragHelper.processTouchEvent(event);

        return true;
    }

    public void computeScroll() {

        if(mViewDragHelper.continueSettling(true))
        {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

}
