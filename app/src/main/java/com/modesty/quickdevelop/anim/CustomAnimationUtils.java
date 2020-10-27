package com.modesty.quickdevelop.anim;

import android.animation.ValueAnimator;
import android.widget.TextView;

import com.modesty.quickdevelop.anim.evaluator.ArgbEvaluator;

public class CustomAnimationUtils {

    public static void startArgbAnimator(int s, int e, TextView textView){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(s, e);
        valueAnimator.setEvaluator(new ArgbEvaluator());
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            textView.setBackgroundColor(animatedValue);
        });
        valueAnimator.start();
    }
}
