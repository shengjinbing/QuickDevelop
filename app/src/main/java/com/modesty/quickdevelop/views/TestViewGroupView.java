package com.modesty.quickdevelop.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class TestViewGroupView extends View {
    // 1.创建一个黑色画笔
    private Paint mPaintB = new Paint();

    // 1.创建一个红色画笔
    private Paint mPaintR = new Paint();

    public TestViewGroupView(Context context) {
        this(context, null);
    }

    public TestViewGroupView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestViewGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mPaintB.setColor(Color.BLACK);       //设置画笔颜色
        mPaintB.setStyle(Paint.Style.FILL);  //设置画笔模式为填充
        mPaintB.setStrokeWidth(10f);         //设置画笔宽度为10px

        mPaintR.setColor(Color.RED);       //设置画笔颜色
        mPaintR.setStyle(Paint.Style.FILL);  //设置画笔模式为填充
        mPaintR.setStrokeWidth(10f);         //设置画笔宽度为10px
    }

    /**
     *
     * Q: 在测量完View并使用setMeasuredDimension函数之后View的大小基本上已经确定了，那么为什么还要再次确定View的大小呢？
     * A: 这是因为View的大小不仅由View本身控制，而且受父控件的影响，所以我们在确定View大小的时候最好使用系统提供的onSizeChanged回调函数。
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    /**
     * 1.如果对View的宽高进行修改了，不要调用 super.onMeasure( widthMeasureSpec, heightMeasureSpec); 要调用
     *   setMeasuredDimension( widthsize, heightsize); 这个函数。
     * 2.在int类型的32位二进制位中，31-30这两位表示测量模式,29~0这三十位表示宽和高的实际值
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthsize = MeasureSpec.getSize(widthMeasureSpec);      //取出宽度的确切数值
        int widthmode = MeasureSpec.getMode(widthMeasureSpec);      //取出宽度的测量模式

        int heightsize = MeasureSpec.getSize(heightMeasureSpec);    //取出高度的确切数值
        int heightmode = MeasureSpec.getMode(heightMeasureSpec);    //取出高度的测量模式

        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }


    /**
     * 画布的一些操作：
     * 1.save：保存当前画布状态
     * 2.restore： 回滚到上一次保存的状态
     * 3.translate：相对于当前位置位移
     * 4.rotate：	旋转
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rectF = new RectF(100,100,800,400);
        canvas.drawRect(rectF,mPaintB);


        canvas.drawCircle(100,100,100,mPaintR);


    }

   /* 在UI主线程中，用invalidate()；本质是调用View的onDraw（）绘制。

    主线程之外，用postInvalidate()。*/

}
