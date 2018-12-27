package com.modesty.quickdevelop.utils.anim;


import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BaseInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by lixiang
 * on 2018/12/26
 */
public class MyLinearInterpolator implements Interpolator {


    /**
     *TimeInterpolator为时间估计器：根据时间流逝的百分比来计算出当前属性值改变的百分比，
     * 系统预置的有LinearInterpolator(线性插值器：匀速动画)、AccelerateDecelerateInterpolator(加速减速插值器：
     * 动画两头慢中间快)和DecelerateInterpolator(减速插值器：动画越来越慢)。
     *
     * 动画默认刷新率为10ms/帧
     *
     *
     * @param input  时间改变的百分比
     * @return
     */
    @Override
    public float getInterpolation(float input) {
        //线性插值器返回输入值
        return input;
    }
}
