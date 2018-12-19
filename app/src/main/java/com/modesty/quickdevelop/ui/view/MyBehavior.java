package com.modesty.quickdevelop.ui.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 自定义 Behavior 的总结
 * 1.确定 CoordinatorLayout 中 View 与 View 之间的依赖关系，通过 layoutDependsOn() 方法，返回值为 true 则依赖，否则不依赖。
 * 2.当一个被依赖项 dependency 尺寸或者位置发生变化时，依赖方会通过 Byhavior 获取到，然后在 onDependentViewChanged 中处理。如果在这个方法中 child 尺寸或者位置发生了变化，则需要 return true。
 * 3.当 Behavior 中的 View 准备响应嵌套滑动时，它不需要通过 layoutDependsOn() 来进行依赖绑定。只需要在 onStartNestedScroll() 方法中通过返回值告知 ViewParent，它是否对嵌套滑动感兴趣。返回值为 true 时，后续的滑动事件才能被响应。
 * 4.嵌套滑动包括滑动(scroll) 和 快速滑动(fling) 两种情况。开发者根据实际情况运用就好了。
 * 5.Behavior 通过 3 种方式绑定：1. xml 布局文件。2. 代码设置 layoutparam。3. 自定义 View 的注解。
 * Created by lixiang
 * on 2018/12/14
 */
public class MyBehavior extends CoordinatorLayout.Behavior<View> {
    public MyBehavior() {
    }

    public MyBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 确定一个 View 对另外一个 View 是否依赖的时候，是通过 layoutDependsOn() 这个方法
     *
     * @param parent
     * @param child      判断的主角
     * @param dependency 宾角
     * @return true代表依赖成立（为true的时候onDependentViewChanged() 和 onDependentViewRemoved() 才会被调用）
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        //return dependency instanceof DependencyView;
        return false;
    }

    /**
     * 当依赖的那个 View 发生变化时，才会被调用
     *
     * @param parent
     * @param child
     * @param dependency
     * @return
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        float x = child.getX();
        float y = child.getY();

        int dependTop = dependency.getTop();
        int dependBottom = dependency.getBottom();

        x = dependency.getX();

        if (child instanceof TextView) {
            y = dependTop - child.getHeight() - 20;
        } else {
            y = dependBottom + 50;
        }


        child.setX(x);
        child.setY(y);

        return true;
    }

    /**
     * 被调用时一般是指 dependency 被它的 parent 移除，或者是 child 设定了新的 anchor。
     *
     * @param parent
     * @param child
     * @param dependency
     */
    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, View child, View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
    }

    /**
     * 我们的目的是当 NestedScrollView 内容滑动时，MyBehavior 规定关联的 ImageView 对象进行相应的位移，这主要是在 Y 轴方向上。首先我们得实现这个方法。
     * 重点：：：：：：：：：：：：：：：：：：：：：
     * 当 Behavior 中的 View 准备响应嵌套滑动时，它不需要通过 layoutDependsOn() 来进行依赖绑定。
     * 只需要在 onStartNestedScroll() 方法中通过返回值告知 ViewParent，它是否对嵌套滑动感兴趣。返回值为 true 时，后续的滑动事件才能被响应。
     *
     * @param coordinatorLayout
     * @param child
     * @param directTargetChild
     * @param target
     * @param nestedScrollAxes
     * @return
     */
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child,
                                       View directTargetChild, View target, int nestedScrollAxes) {
        Log.d("MyBehavior", "onStartNestedScroll");
        return child instanceof ImageView && nestedScrollAxes == View.SCROLL_AXIS_VERTICAL;
    }

    /**
     * 我们要复写 onNestedPreScroll() 方法，dx 和 dy 是滑动的位移。另外还有一个方法 onNestedScroll()。
     * 两个方法的不同在于顺序的先后，onNestedPreScroll() 在 滑动事件准备作用的时候先行调用，注意是准备作用，
     * 然后把已经消耗过的距离传递给 consumed 这个数组当中。而 onNestedScroll() 是滑动事件作用时调用的。
     * 它的参数包括位移信息，以及已经在 onNestedPreScroll() 消耗过的位移数值。我们一般实现 onNestedPreScroll() 方法就好了。
     *
     * @param coordinatorLayout
     * @param child
     * @param target
     * @param dx
     * @param dy
     * @param consumed
     */
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        Log.d("MyBehavior", "onNestedPreScroll  dx:" + dx + " dy:" + dy);
        ViewCompat.offsetTopAndBottom(child, dy);
    }

    /**
     * 与前面的 NestedScroll 相似，我们可以在 fling 动作即将发生时，通过 onNestedPreFling 获知，
     * 如果在这个方法返回值为 true 的话会怎么样？它将会拦截这次 fling 动作，
     * 表明响应中的 child 自己处理了这次 fling 意图，那么 NestedScrollView 反而操作不了这个动作，因为系统会当作 child 消耗过这次事件
     *
     * @param coordinatorLayout
     * @param child
     * @param target
     * @param velocityX
     * @param velocityY
     * @return
     */
    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY) {
        Log.d("MyBehavior", "onNestedPreFling velocityY:" + velocityY);
        if (velocityY > 0) {
            child.animate().scaleX(2.0f).scaleY(2.0f).setDuration(2000).start();
        } else {
            child.animate().scaleX(1.0f).scaleY(1.0f).setDuration(2000).start();
        }

        return false;
//        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }
}
