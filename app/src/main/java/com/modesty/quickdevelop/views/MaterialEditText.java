package com.modesty.quickdevelop.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.modesty.quickdevelop.R;

/**
 * Created by lixiang on 2019-08-06
 */
public class MaterialEditText extends AppCompatEditText {
    private boolean hideUnderline;
    private int underlineColor;
    //获取焦点后的下划线颜色
    private int focusUnderlineColor;
    private boolean showClearButton;
    private int baseColor;
    private int iconSize;
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap clearButtonBitmaps;
    private boolean clearButtonTouched;
    private boolean clearButtonClicking;
    private boolean focusHideUnderline;
    private float underlineHeight;
    private float underlineFocusHeight;
    private int clearButtonPadding;

    public MaterialEditText(Context context) {
        super(context);
        init(context, null);
    }

    public MaterialEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaterialEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }
        iconSize = getPixel(32);
        // default baseColor is black
        int defaultBaseColor = Color.BLACK;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialEditText);
        baseColor = typedArray.getColor(R.styleable.MaterialEditText_met_baseColor, defaultBaseColor);
        hideUnderline = typedArray.getBoolean(R.styleable.MaterialEditText_met_hideUnderline, false);
        focusHideUnderline = typedArray.getBoolean(R.styleable.MaterialEditText_met_focu_hideUnderline, true);
        underlineColor = typedArray.getColor(R.styleable.MaterialEditText_met_underlineColor, -1);
        focusUnderlineColor = typedArray.getColor(R.styleable.MaterialEditText_met_focus_underlineColor, -1);
        showClearButton = typedArray.getBoolean(R.styleable.MaterialEditText_met_clearButton, false);
        clearButtonBitmaps = generateIconBitmaps(typedArray.getResourceId(R.styleable.MaterialEditText_met_iconClearButton, -1));
        underlineHeight = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_underline_height, 1);
        underlineFocusHeight = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_focus_underline_height, 2);
        clearButtonPadding = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_clearButton_padding, 0);

        //清空背景
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(null);
        } else {
            setBackgroundDrawable(null);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int startX = getScrollX();
        int endX = getScrollX() + getWidth();
        int lineStartY = getScrollY() + getHeight();
        // draw the icon(s)
        paint.setAlpha(255);
        // draw the clear button
        if (hasFocus() && showClearButton && !TextUtils.isEmpty(getText()) && isEnabled()) {
            paint.setAlpha(255);
            int buttonLeft = endX;
            buttonLeft += -clearButtonBitmaps.getWidth();
            int iconTop = (lineStartY - clearButtonBitmaps.getHeight()) / 2;
            canvas.drawBitmap(clearButtonBitmaps, buttonLeft, iconTop, paint);
        }
        // draw the underline
        if (!hideUnderline) {
            if (!isEnabled()) { // disabled 虚线下划线
                lineStartY += -underlineHeight;
                paint.setColor(underlineColor != -1 ? underlineColor : baseColor & 0x00ffffff | 0x44000000);
                float interval = getPixel(1);
                for (float xOffset = 0; xOffset < getWidth(); xOffset += interval * 3) {
                    canvas.drawRect(startX + xOffset, lineStartY, startX + xOffset + interval, lineStartY + getPixel(1), paint);
                }
            } else if (hasFocus() && !focusHideUnderline) { // focused
                lineStartY += -underlineFocusHeight;
                paint.setColor(focusUnderlineColor);
                canvas.drawRect(startX, lineStartY, endX, lineStartY + getPixel(2), paint);
            } else { // normal
                lineStartY += -underlineHeight;
                paint.setColor(underlineColor != -1 ? underlineColor : baseColor & 0x00ffffff | 0x1E000000);
                canvas.drawRect(startX, lineStartY, endX, lineStartY + getPixel(1), paint);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (hasFocus() && showClearButton && isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (insideClearButton(event)) {
                        clearButtonTouched = true;
                        clearButtonClicking = true;
                        return true;
                    }
                case MotionEvent.ACTION_MOVE:
                    if (clearButtonClicking && !insideClearButton(event)) {
                        clearButtonClicking = false;
                    }
                    if (clearButtonTouched) {
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (clearButtonClicking) {
                        if (!TextUtils.isEmpty(getText())) {
                            setText(null);
                        }
                        clearButtonClicking = false;
                    }
                    if (clearButtonTouched) {
                        clearButtonTouched = false;
                        return true;
                    }
                    clearButtonTouched = false;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    clearButtonTouched = false;
                    clearButtonClicking = false;
                    break;
                default:
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 判断点击事件是否在删除按钮里面
     *
     * @param event
     * @return
     */
    private boolean insideClearButton(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int endX = getScrollX() + getWidth();
        int buttonLeft = endX - clearButtonBitmaps.getWidth() -clearButtonPadding;
        int buttonTop = (getScrollY() + getHeight() - clearButtonBitmaps.getHeight()) / 2 - clearButtonPadding;
        return (x >= buttonLeft && x < buttonLeft + clearButtonBitmaps.getWidth()+clearButtonPadding
                && y >= buttonTop && y < buttonTop + clearButtonBitmaps.getHeight()+clearButtonPadding);
    }

    private int getPixel(int dp) {
        return dp2px(getContext(), dp);
    }

    private Bitmap generateIconBitmaps(@DrawableRes int origin) {
        if (origin == -1) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), origin, options);
        int size = Math.max(options.outWidth, options.outHeight);
        options.inSampleSize = size > iconSize ? size / iconSize : 1;
        options.inJustDecodeBounds = false;
        return generateIconBitmaps(BitmapFactory.decodeResource(getResources(), origin, options));
    }

    private Bitmap generateIconBitmaps(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return generateIconBitmaps(Bitmap.createScaledBitmap(bitmap, iconSize, iconSize, false));
    }

    private Bitmap generateIconBitmaps(Bitmap origin) {
        if (origin == null) {
            return null;
        }
        origin = scaleIcon(origin);
        return origin;
    }

    private Bitmap scaleIcon(Bitmap origin) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        int size = Math.max(width, height);
        if (size == iconSize) {
            return origin;
        } else if (size > iconSize) {
            int scaledWidth;
            int scaledHeight;
            if (width > iconSize) {
                scaledWidth = iconSize;
                scaledHeight = (int) (iconSize * ((float) height / width));
            } else {
                scaledHeight = iconSize;
                scaledWidth = (int) (iconSize * ((float) width / height));
            }
            return Bitmap.createScaledBitmap(origin, scaledWidth, scaledHeight, false);
        } else {
            return origin;
        }
    }

    public static int dp2px(Context context, float dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return Math.round(px);
    }

}
