package com.modesty.quickdevelop.utils.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;



import java.util.ArrayList;
import java.util.List;

/**
 * Created by congtaowang on 2018/3/20.
 */

public class AnimUtils {

    public static final int ANIM_DURATION_100 = 100;
    public static final int ANIM_DURATION = 500;

    public static final int ANIM_DELAY = 1000;
    public static final int ANIM_DELAY_300 = 300;
    public static final int ANIM_DELAY_100 = 100;
    public static final int ANIM_DURATION_ZERO = 0;

    public static final float ALPHA_TRANSPARENT = 0f;
    public static final float ALPHA_DISPLAY = 1f;

    public static final float[] ALPHA_TRANSPARENT_2_DISPLAY = {
            ALPHA_TRANSPARENT, ALPHA_DISPLAY
    };

    public static final float[] ALPHA_DISPLAY_2_TRANSPARENT = {
            ALPHA_DISPLAY, ALPHA_TRANSPARENT
    };


    public static class DefaultAnimatorListener implements Animator.AnimatorListener {

        private final Animator.AnimatorListener delegate;

        public DefaultAnimatorListener(Animator.AnimatorListener delegate) {
            this.delegate = delegate;
        }

        public Animator.AnimatorListener getDelegate() {
            return delegate;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            if (getDelegate() != null) {
                getDelegate().onAnimationStart(animation);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (getDelegate() != null) {
                getDelegate().onAnimationEnd(animation);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            if (getDelegate() != null) {
                getDelegate().onAnimationCancel(animation);
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            if (getDelegate() != null) {
                getDelegate().onAnimationRepeat(animation);
            }
        }
    }

    public static class AnimAction {

        private Animator animator;
        private AnimAction nextAnim;
        private Animator.AnimatorListener delegate;
        private boolean isFinished = true;

        public AnimAction(Animator animator, AnimAction nextAnim, Animator.AnimatorListener delegate) {
            this.animator = animator;
            this.nextAnim = nextAnim;
            this.delegate = delegate;
        }

        public AnimAction(Animator animator, AnimAction nextAnim) {
            this.animator = animator;
            this.nextAnim = nextAnim;
        }

        public Animator getAnimator() {
            return animator;
        }

        public AnimAction getNextAnim() {
            return nextAnim;
        }

        public AnimAction setAnimator(Animator animator) {
            this.animator = animator;
            return this;
        }

        public AnimAction setNextAnim(AnimAction nextAnim) {
            this.nextAnim = nextAnim;
            return this;
        }

        public Animator.AnimatorListener getDelegate() {
            return delegate;
        }

        public AnimAction setDelegate(Animator.AnimatorListener delegate) {
            this.delegate = delegate;
            return this;
        }

        public void start() {
            if (animator == null) {
                return;
            }
            isFinished = false;
            animator.addListener(new DefaultAnimatorListener(getDelegate()) {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (getNextAnim() != null) {
                        getNextAnim().start();
                    } else {
                        isFinished = true;
                    }
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                }
            });
            animator.start();
        }

        public boolean isFinished() {
            if (getNextAnim() != null && getNextAnim() != this) {
                return getNextAnim().isFinished();
            }
            return isFinished;
        }
    }

    public static class AnimActionTeam {
        private final List<AnimAction> actions = new ArrayList<>();

    }

    public static void start(AnimAction animAction) {
        if (animAction != null) {
            animAction.start();
        }
    }

    public static Animator alphaAnim(View target, float... alpha) {
        if (alpha == null || alpha.length == 0) {
            throw new IllegalArgumentException("alpha can't be null or empty");
        }
        Animator animator = ObjectAnimator.ofFloat(target, "alpha", alpha);
        animator.setDuration(ANIM_DURATION);
        return animator;
    }

    public static Animator scaleXAnim(View target, float... scaleX) {
        Animator animator = ObjectAnimator.ofFloat(target, "scaleX", scaleX);
        animator.setDuration(ANIM_DURATION);
        return animator;
    }

    public static Animator scaleYAnim(View target) {
        Animator animator = ObjectAnimator.ofFloat(target, "scaleY", 0.5f, 1f);
        animator.setDuration(ANIM_DURATION);
        return animator;
    }

    public static Animator scrollXAnim(View target, float... scrollX) {
        Animator animator = ObjectAnimator.ofFloat(target, "scrollX", scrollX);
        animator.setDuration(ANIM_DURATION);
        return animator;
    }

    public static Animator rotationXAnim(View target, float... rotationX) {
        Animator animator = ObjectAnimator.ofFloat(target, "rotationX", rotationX);
        animator.setDuration(ANIM_DURATION);
        return animator;
    }

    public static Animator rotationYAnim(View target, float... rotationX) {
        Animator animator = ObjectAnimator.ofFloat(target, "rotationY", rotationX);
        animator.setDuration(ANIM_DURATION);
        return animator;
    }

    public static Animator rotationAnim(View target, float... rotationX) {
        Animator animator = ObjectAnimator.ofFloat(target, "rotation", rotationX);
        animator.setDuration(ANIM_DURATION);
        return animator;
    }

    public static Animator combination(Animator... animators) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        return animatorSet;
    }

    public static Animator translateYAnim(View target, float... translateY) {
        Animator animator = ObjectAnimator.ofFloat(target, "translationY", translateY);
        animator.setDuration(ANIM_DURATION);
        return animator;
    }

    public static Animator translateXAnim(View target, float... translateX) {
        Animator animator = ObjectAnimator.ofFloat(target, "translationX", translateX);
        animator.setDuration(ANIM_DURATION);
        return animator;
    }

    public static void slideToUp(View view, Animation.AnimationListener animationListener) {

        try {
            Animation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            slide.setDuration(300);
            slide.setAnimationListener(animationListener);
            view.startAnimation(slide);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void slideToDown(View view, Animation.AnimationListener animationListener) {

        try {
            Animation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
            slide.setDuration(300);
            slide.setAnimationListener(animationListener);
            view.startAnimation(slide);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void slideTopToDown(View view, Animation.AnimationListener animationListener) {

        try {
            Animation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -1.0f, Animation.RELATIVE_TO_SELF, 0f);
            slide.setDuration(300);
            slide.setAnimationListener(animationListener);
            view.startAnimation(slide);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void slideDownToTop(View view, Animation.AnimationListener animationListener) {

        try {
            Animation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    0f, Animation.RELATIVE_TO_SELF, -1f);
            slide.setDuration(300);
            slide.setAnimationListener(animationListener);
            view.startAnimation(slide);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
