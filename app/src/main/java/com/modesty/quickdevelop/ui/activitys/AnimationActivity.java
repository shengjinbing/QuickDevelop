package com.modesty.quickdevelop.ui.activitys;

import android.animation.Animator;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.modesty.quickdevelop.R;

import java.security.PrivateKey;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 动画可以分3中：
 * 1.View动画（平移，缩放，旋转，透明度）
 * 2.帧动画（属于View动画的一种）
 * 3.属性动画
 */
public class AnimationActivity extends AppCompatActivity {
    private final static  String TAG = "ANIMATION_LOG";

    @BindView(R.id.iv_icon)
    ImageView mIvIcon;
    private Animation mAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        ButterKnife.bind(this);
        //initListener();
        //showAnim();
        showFrameAnimation();
    }

    private void showAnim() {
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_test);
        mIvIcon.setAnimation(mAnimation);
    }

    /**
     * 帧动画容易引起OOM，避免使用过多尺寸较大的图片
     */
    private void showFrameAnimation() {
        mIvIcon.setBackgroundResource(R.drawable.frame_animation);
        AnimationDrawable drawable = (AnimationDrawable) mIvIcon.getBackground();
        drawable.start();

    }

    private void initListener() {
        ObjectAnimator valueAnimator = (ObjectAnimator) ObjectAnimator.ofInt(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            /**
             * 监听动画每一帧
             * @param animation
             */
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void ObjectAnimator(View view) {
        Button button = (Button) findViewById(R.id.btn_wra);
        //ObjectAnimator.ofFloat(new ViewWrapper(button),"width",500).setDuration(5000).start();
        performAnimate(button,0,500);
    }

    public void performAnimate(final  View target,final int start,int end){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private IntEvaluator mIntEvaluator = new IntEvaluator();
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //获得当前动画的进度值，整型，1-100
                int curValue = (int) animation.getAnimatedValue();
                Log.d(TAG,"curValue="+curValue);
                float animatedFraction = animation.getAnimatedFraction();
                target.getLayoutParams().width = mIntEvaluator.evaluate(animatedFraction,start,end);
                target.requestLayout();
            }
        });
        valueAnimator.setDuration(5000).start();
    }

    /**
     *我们对Object的属性abc做动画需要满足两个条件
     * 1.object必须要提供setAbc的方法，如果动画的时候没有传递初始值，那么还要提供getAbc的方法，因为
     * 系统要去取abc的值（不满足这条直接cracsh）
     * 2.object的setAbc对属性abc所做的改变必须能通过某种方法反应出来，比如会带来UI的改变之类的（不满足这条动画无效但不会crash）
     *
     *
     * 满足1条件不满足2条件的3种解决办法
     * 1.给对象加上set和get方法如果有权限的话
     * 2.用一个包装类包装原始对象，间接提供get和set
     * 3.才有ValueAnimator，监听动画过程，自己实现属性变化。
     */
    static class ViewWrapper{
        private View target;
        public ViewWrapper(View target) {
            this.target = target;
        }
        public int getWidth(){
            return target.getLayoutParams().width;
        }

        public void setWidth(float width){
            target.getLayoutParams().width = (int) width;
            target.requestLayout();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
