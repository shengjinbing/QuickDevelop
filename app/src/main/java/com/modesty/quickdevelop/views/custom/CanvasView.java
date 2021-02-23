package com.modesty.quickdevelop.views.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.modesty.quickdevelop.R;

public class CanvasView extends View {
    private Paint paint;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    private int color = Color.RED;
    public CanvasView(Context context) {
        this(context,null);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE,null);
        paint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("BBBBBB","onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(color);
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.clipRect(100,0,200,100);
        canvas.restore();

        canvas.drawColor(Color.GREEN);
    }
}
