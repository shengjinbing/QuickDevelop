package com.modesty.quickdevelop.views.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class DragView extends View {


    private int lastX;
    private int lastY;
    private Scroller mScroller;


    public DragView(Context context) {
        this(context, null);
    }

    public DragView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
    }


    public boolean onTouchEvent(MotionEvent event) {

//        Log.d("付勇焜----->","TouchEvent");
//        Log.d("付勇焜----->",super.onTouchEvent(event)+"");


        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                lastX = x;
                lastY = y;

                break;

            case MotionEvent.ACTION_MOVE:

                //计算移动的距离
                int offX = x - lastX;
                int offY = y - lastY;
                //1.调用layout方法来重新放置它的位置
                /*layout(getLeft() + offX, getTop() + offY,
                        getRight() + offX, getBottom() + offY);*/

                //2.其实这两个方法分别是对左右移动和上下移动的封装，传入的就是偏移量。
               /* offsetLeftAndRight(offX);
                offsetTopAndBottom(offY);*/

                //3.更改布局参数
                /*ViewGroup.MarginLayoutParams mlp =
                        (ViewGroup.MarginLayoutParams) getLayoutParams();
                mlp.leftMargin = getLeft() + offX;
                mlp.topMargin = getTop() + offY;
                setLayoutParams(mlp);*/

                //4,移动的内容
                //((View) getParent()).scrollBy(-offX,- offY);

                //5开启模拟过程，在合适的地方（一般都在move中）startScroll方法。它有两个重载方法如下：
                //     startScroll(int startX,int startY, int dx,int dy,int duration)
                //     startScroll(int startX,int startY,int dx,int dy)
                //     方法中的参数无需多解释了，就是起始坐标与偏移量，还有完成偏移的时间而已。
                View viewGroup = (View) getParent();
                mScroller.startScroll(viewGroup.getScrollX(),
                        viewGroup.getScrollY(), -offX, -offY);
                break;
        }

        return true;
    }

    /**
     *
     * 重写computeScroll()方法，实现模拟滑动
     *
     * 因为computeScroll方法不会自动调用，是在draw方法中被调用的
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        //判断是否完成整个滑动
        if (mScroller.computeScrollOffset()) {
            //getCurrY以及getCurrX获得的是当前的滑动坐标
            ((View) getParent()).scrollTo(mScroller.getCurrX(),
                    mScroller.getCurrY());
        }
        //最后必须要用invalidate方法来刷新。因为computeScroll方法不会自动调用，是在draw方法中被调用的。所以
        //必须使用invalidate刷新，就会调用draw方法，自然就会调用computeScroll方法了。这样子就会实现循环调用
        invalidate();//必须要调用
    }
}