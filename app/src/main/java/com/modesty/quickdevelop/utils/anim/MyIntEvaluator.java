package com.modesty.quickdevelop.utils.anim;

import android.animation.TypeEvaluator;

/**
 * TypeEvaluator:类型估值算法，估值器：根据当前属性值改变的百分比来计算改变后的属性值。
 * 系统预制的有：IntEvaluator（针对整型属性）、FloatEvaluator（针对浮点型属性）和ArgbEvaluator
 * （针对Color属性）
 *
 * Created by lixiang
 * on 2018/12/27
 */
public class MyIntEvaluator implements TypeEvaluator<Integer> {
    /**
     * @param fraction 估值小数,根据插值器返回的值来计算x的值
     * @param startValue 开始值
     * @param endValue 结束值
     * @return
     */
    @Override
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int)(startInt + fraction*(endValue - startInt));
    }
}
