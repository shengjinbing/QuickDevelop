package com.modesty.quickdevelop.ui.activitys;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.modesty.quickdevelop.R;
import com.squareup.leakcanary.LeakCanary;

/**
 * 性能优化相关：
 * --------------------布局优化----------------------------
 * 1、删除布局中无用控件和层级；有选择的使用性能较低ViewGroup，比如RelativeLayoutd布局过程需要花费更多的CUP时间，FrameLayout和LinearLayout
 *    一样都是一种简单高效的布局，ConstraintLayout约束布局最高效推存使用。
 * 2、<include>标签主要用于布局复用；<merge>一般和<include>配合使用减少层级；<ViewStud>则提供按需求加载功能，需要的时候才会将ViewStub
 *    中的布局加载到内存，这提高了初始化效率,不支持<merge>标签。
 *    <include>标签只支持android:layout_开头的属性，android:id除外，如果根布局也有id则以标签id为准
 *    <ViewStud>常用于加载网络请求失败，有两种方式显示出来，setVisibility和inflate
 * ----------------------绘制优化----------------------------
 * 绘制优化是指View的onDraw方法要避免执行大量的操作。
 * 1.onDraw中不要创建新的局部对象，因为onDrwa方法可能会频繁调用，这样在一瞬间会产生大量的临时对象，这样会占用更多的内存导致gc
 * 2.onDraw中不要做耗时操作，大量的循环十分抢占cpu的时间片，这样会造成View的绘制不流畅，View的绘制帧率保持在60fps是最佳，
 * 这就要求每帧的绘制时间不超过16ms（16ms=1000/60）。
 */
public class OptimizationActivity extends AppCompatActivity {
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optimization);
        LeakCanaryStatic();
        LeakCanarySingleton();
    }

    /**
     * 静态变量引起的内存泄露
     */
    private void LeakCanaryStatic() {
        //context = this;
    }

    /**
     * 单列的生命周期跟Application保持一致，
     * 1.接口回调未取消注册
     * 2.持有Fragment和Activity的引用
     */
    private void LeakCanarySingleton() {

    }
}
