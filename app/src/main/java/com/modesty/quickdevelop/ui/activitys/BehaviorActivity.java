package com.modesty.quickdevelop.ui.activitys;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.modesty.quickdevelop.R;

/**
 * 三种设置behavior的方式
 * 1.xml
 * 2.CoordinatorLayout.LayoutParams layoutParams =
 * (CoordinatorLayout.LayoutParams) mIvTest.getLayoutParams();
 * layoutParams.setBehavior(new MyBehavior());
 * 3.注解@CoordinatorLayout.DefaultBehavior(AppBarLayout.Behavior.class)
 *
 * onStartNestedScroll的onStartNestedScroll（）方法会调用孩子的Behavior的Behavior.onStartNestedScroll（）方法
 *
 * 分析：
 * public boolean isNestedScrollingEnabled() {
 * return (mPrivateFlags3 & PFLAG3_NESTED_SCROLLING_ENABLED) ==
 * PFLAG3_NESTED_SCROLLING_ENABLED;
 * }
 * 当 isNestedScrollingEnabled() 返回 true 时，它的 ViewParent 的 onStartNestedScroll() 才能被触发。
 * 这个方法的逻辑就是判断一个 View 中 mPrivateFlags3 这个变量中的 PFLAG3_NESTED_SCROLLING_ENABLED 这一 bit 是否被置为 1 。
 *
 * Nested scroll 的流程
 * 到这里的时候，一个嵌套滑动的事件的起始我们才彻底明白。它是由一个 NestedScrollingChild(5.0 版本
 * setNestedScrollEnable(true) 就好了) 发起，通过向上遍历 parent,借助于 parent 对象的相关方法来完成交互。
 * 值得注意的是 5.0 版本以下，parent 要保证是一个 NestedScrollingParent 对象。
 *
 * Behavior 在之前也说过，它是一种插件。正因为这种机制，它将干涉 CoordinatorLayout 与 childView
 * 之间的关系，Behavior 通过拦截 CoordinatorLayout 发给子 View 的信号，根据自身的规则进而来达到控制 childView 的目的。
 * 如果没有这些 Behavior 存在的话，CoordinatorLayout 跟普通的 ViewGroup 无疑。
 *
 *
 *
 * 通常自定义Behavior分为两种情况：
 * 1.某个View依赖另一个View，监听其位置、尺寸等状态的变化。
 * 2.某个View监听CoordinatorLayout内实现了NestedScrollingChild接口的子View的滑动状态变化(也是一种依赖关系)。
 */
public class BehaviorActivity extends AppCompatActivity {


    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavior);
        mButton = (Button) findViewById(R.id.btn);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mButton.setNestedScrollingEnabled(true);
        }

    }

    /**
     * NavigationMenuView、NestedScrollView、RecyclerView、SwipleRefreshLayout。四个实现NestedScrollingChild的类可以滑动
     *
     * @param view
     */
    public void nestscroll(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mButton.startNestedScroll(View.SCROLL_AXIS_HORIZONTAL);
        }
    }
}
