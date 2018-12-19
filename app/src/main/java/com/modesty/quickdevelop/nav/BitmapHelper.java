package com.modesty.quickdevelop.nav;


import android.content.Context;
import android.util.TypedValue;

/**
 * @author wangzhiyuan
 * @since 2017/8/30
 */

public class BitmapHelper {
    public static int dip2px(Context context, float dp) {
        return (int) (convertUnitToPixel(context, TypedValue.COMPLEX_UNIT_DIP, dp) + 0.5f);
    }
    private static float convertUnitToPixel(Context context, int unit, float in) {
        return TypedValue.applyDimension(unit, in, context.getResources().getDisplayMetrics());
    }
}
