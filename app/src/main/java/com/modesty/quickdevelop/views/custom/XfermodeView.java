package com.modesty.quickdevelop.views.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class XfermodeView extends View {
    Paint paint;
    Path path;
    public XfermodeView(Context context) {
        this(context,null);
    }

    public XfermodeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public XfermodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿一直开着
        path = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        path.setFillType(Path.FillType.WINDING);
        path.addCircle(getWidth()/2,getHeight()/2,dp2px(100),Path.Direction.CCW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }



    public float dp2px(int dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }
}
