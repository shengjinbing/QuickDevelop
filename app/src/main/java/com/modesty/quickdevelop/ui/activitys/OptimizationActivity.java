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
 *
 *
 * 防止内存泄露
 * 1)静态变量直接或者间接地引用了Activity对象就会造成内存泄露
 * 2)Activity使用了静态的View(View会持有Activity的对象的引用)
 * 3)Activity定义了静态View变量???
 * 4)ImageSpan引用了Activity Context
 * 5)单例中引用了Activity的Context(需要使用Application的Context)
 * 6)对于使用了BraodcastReceiver，ContentObserver，File，Cursor，Stream，Bitmap等资源，应该在Activity销毁时及时关闭或者注销，否则这些资源将不会被回收，从而造成内存泄漏。
 * 7)静态集合保存的对象没有及时消除(不使用的时候置为null)
 * 8)在Java中,非静态(匿名)内部类会引用外部类对象,而静态内部类不会引用外部类对象
 * 9)在Activity中,创建了非静态内部类(内部类直接或者间接引用了Activity)的静态成员变量
 * 10)线程包括AsyncTask的使用,Activity退出后线程还在运行(线程在死循环),并且在线程中使用了Activity或view对象(解决方法:不要直接写死循环,可以设置一个布尔类型的TAG,当activity推出的时候,设置TAG为False)
 * 11)Handler对象的使用,Activity退出后Handler还是有消息需要处理(解决方法:在退出activity之后,移除消息)
 * 12)WebView造成的内存泄漏(在onDestory中销毁)
 *
 *
 *
 *
 *
 *
 * APP启动优化
 * 一、应用的启动方式
 * 1、冷启动：当启动应用时，后台没有该应用的进程，这时系统会首先会创建一个新的进程分配给该应用，这种启动方式就是冷启动。
 * 2、热启动：当启动应用时，后台已有该应用的进程，比如按下home键，这种在已有进程的情况下，这种启动会从已有的进程中来启动
 *    应用，这种启动方式叫热启动。
 * 3、温启动 ：当启动应用时，后台已有该应用的进程，但是启动的入口Activity被干掉了，比如按了back键，应用虽然退出了，但是
 *    该应用的进程是依然会保留在后台，这种启动方式叫温启动。
 *
 * 二、adb shell am start -W [PackageName]/[PackageName.MainActivity]
 * 1、ThisTime:一般和TotalTime时间一样，除非在应用启动时开了一个透明的Activity预先处理一些事再显示出主Activity，这样将比TotalTime小。
 * 2、TotalTime:应用的启动时间，包括创建进程+Application初始化+Activity初始化到界面显示。
 * 3、WaitTime:一般比TotalTime大点，包括系统影响的耗时
 *
 * 三、getWindow().getDecorView().post()的懒加载机制在窗口完成以后进行加载，这里面的run方法是在onResume之后运行的
 * 四、WindowManager会先加载APP里的主题样式里的窗口背景（windowBackground）作为预览元素，然后才去真正的加载布局。如
 *    果加载windowBackground时间过长，而默认的背景又是黑色或者白色，这样会给用户造成一种错觉，APP不流畅，影响用户体验。
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
