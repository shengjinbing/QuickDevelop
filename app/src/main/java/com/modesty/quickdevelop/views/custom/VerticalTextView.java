package com.modesty.quickdevelop.views.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.modesty.quickdevelop.R;

/**
 * Created by lixiang on 2021/1/12
 * Describe:
 */
@SuppressLint("AppCompatCustomView")
public class VerticalTextView extends TextView {
    private TextView borderText = null;///用于描边的TextView

    public VerticalTextView(Context context) {
        this(context,null);
    }

    public VerticalTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VerticalTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        borderText = new TextView(context,attrs);
        init();
    }

    public void init(){
        TextPaint tp = borderText.getPaint();
        tp.setStrokeWidth(4);                                  //设置描边宽度
        tp.setStyle(Paint.Style.STROKE);                             //对文字只描边
        borderText.setTextColor(getResources().getColor(R.color.border_text));  //设置描边颜色
        borderText.setGravity(getGravity());
    }

    @Override
    public void setLayoutParams (ViewGroup.LayoutParams params){
        super.setLayoutParams(params);
        borderText.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CharSequence tt = borderText.getText();

        //两个TextView上的文字必须一致
        if(tt== null || !tt.equals(this.getText())){
            borderText.setText(getText());
            this.postInvalidate();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        borderText.measure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout (boolean changed, int left, int top, int right, int bottom){
        super.onLayout(changed, left, top, right, bottom);
        borderText.layout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        borderText.draw(canvas);
        super.onDraw(canvas);
    }
}
