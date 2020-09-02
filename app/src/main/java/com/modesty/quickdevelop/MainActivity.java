package com.modesty.quickdevelop;

import android.app.Activity;
import android.content.Intent;
import android.os.Trace;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.modesty.quickdevelop.bean.User;
import com.modesty.quickdevelop.ui.activitys.AdapterActivity;
import com.modesty.quickdevelop.ui.activitys.AnimationActivity;
import com.modesty.quickdevelop.ui.activitys.ButterKnifeActivity;
import com.modesty.quickdevelop.ui.activitys.CollectionActivity;
import com.modesty.quickdevelop.ui.activitys.EncryptActivity;
import com.modesty.quickdevelop.ui.activitys.FragmentActivity;
import com.modesty.quickdevelop.ui.activitys.FrameActivity;
import com.modesty.quickdevelop.ui.activitys.HandlerActivity;
import com.modesty.quickdevelop.ui.activitys.LottieActivity;
import com.modesty.quickdevelop.ui.activitys.MvpDagger2Activity;
import com.modesty.quickdevelop.ui.activitys.OkHttpActivity;
import com.modesty.quickdevelop.ui.activitys.RxJavaActivity;
import com.modesty.quickdevelop.ui.activitys.ViewActivity;
import com.modesty.quickdevelop.ui.activitys.VolleyActivity;
import com.modesty.quickdevelop.ui.activitys.WebViewActivity;
import com.modesty.quickdevelop.ui.activitys.WindowManagerTestActivity;
import com.modesty.quickdevelop.volley.VolleyManage;
import com.tencent.mars.Mars;
import com.tencent.mars.xlog.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.reactivestreams.Subscription;


import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author 1
 * /*
 * api：跟2.x版本的 compile完全相同
 * provided（compileOnly）
 * 只在编译时有效，不会参与打包
 * 可以在自己的moudle中使用该方式依赖一些比如com.android.support，gson这些使用者常用的库，避免冲突。
 * <p>
 * 总结起来：如果api依赖，一个module发生变化，这条依赖链上所有的module都需要重新编译；而implemention，只有直接依赖这个module需要重新编译。
 * 如果都是本地依赖，implementation相比api，主要优势在于减少build time
 * <p>
 * 全部远程依赖模式下，无论是api还是implemention都起不到依赖隔离的作用
 */
public class MainActivity extends Activity {
    public static final String TAG = "MAIN_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Trace.beginSection("aa");
        }


    /**
     * RecyclerView和ListView
     *
     * @param view
     */
    public void viewmain(View view) {
        Intent intent = new Intent(this, ViewActivity.class);
        startActivity(intent);
    }

    /**
     * Okhttp
     *
     * @param view
     */
    public void okhttpmain(View view) {
        startActivity(new Intent(this, OkHttpActivity.class));
    }

    /**
     * rxJava
     *
     * @param view
     */
    public void rxjavamain(View view) {
        startActivity(new Intent(this, RxJavaActivity.class));
    }


    public void volley(View view) {
        startActivity(new Intent(this, VolleyActivity.class));
    }

    public void fragment(View view) {
        startActivity(new Intent(this, FragmentActivity.class));
    }

    public void Handler(View view) {
        startActivity(new Intent(this, HandlerActivity.class));
    }

    public void collection(View view) {
        startActivity(new Intent(this, CollectionActivity.class));
    }

    public void encrypt(View view) {
        startActivity(new Intent(this, EncryptActivity.class));
    }

    public void animation(View view) {
        startActivity(new Intent(this, AnimationActivity.class));
    }

    public void webview(View view) {
        startActivity(new Intent(this, WebViewActivity.class));
    }

    /**
     * 分析Activity的启动流程
     * 1.startActivity有很多重载方法最后都会调用startActivityForResult()方法
     * 2.ApplicationThread在ActivityThread中初始化，并且ApplicationThread是ActivityThread的内部类，
     * ApplicationThread是一个binder对象。
     * 3.Instrumentation中调用ActivityManagerNative.getDefault().startActivity()来真正启动Activity
     * AMS继承AMN，AMN继承binder并实现IActivityManager，所以AMS是一个binder，所以ActivityManagerNative.getDefault()
     * 就是一个binderl;通过Singleton是一个单例封装类，第一次调用它的get方法时它会通过create方法来初始化AMS这个binder对象。
     * 4.checkStartActivityResult(result, intent)检查启动结果，无法正常启动报异常，如：没有在AndroidManifest中注册。
     * 5.继续分析AMS的startActivity()方法,realStartActivityLocked()方法中app.thread.scheduleLaunchActivity(),
     * app.thread的类型是IApplicationThread（继承IInterface接口，它是一个binder类型的接口。其中包含大量启动，停止Activity的方法，
     * 此外还包含了启动和停止服务的方法。由此可以猜出IApplicationThread这个Binder接口的实现者完成了大量Activity已经Service启动/停止
     * 相关的功能）;绕了一大圈最终回到ApplicationThread中的scheduleLaunchActivity()方法来启动Activity,这个方法发送一个启动Activity
     * 的消息交给ActivityThread中的H这个Handler处理。
     * 6.H中的 handleLaunchActivity()---->Activity MVPActivityModelImpl = performLaunchActivity(r, customIntent);创建Activity对象。
     * 7.performLaunchActivity这个方法主要干了这么几件事：
     * 1.从ActivityClientRecord中获取待启动的Activity的组件信息。
     * 2.通过Instrumenttation的newActivity方法使用类加载器创建Activity对象。
     * 3.LoadedApk的makeApplication(false, mInstrumentation)方法尝试创建Application对象，如果被创建就不再创建（和Activity一样
     * 都是通过类加载器来创建的），会通过instrumentation.callApplicationOnCreate(app)方法来调用Application的onCreate()方法，
     * 4.通过ContextImpl对象并通过Activity的attach方法来完成一些重要数据初始化。ContextImpl是一个很重要的数据结构，集成Context，
     * Context中大部分逻辑都是有ContextImpl来完成的。ContextImpl是通过Activity的attach方法来和Activity建立关联的，除此之外在
     * attach方法中Activity还会完成Window的创建并建立自己和Window的关联。这样当Window接收到外部输入事件后就可以将事件传递给Activity。
     * 5.调用Activity的onCreate()方法，mInstrumentation.callActivityOnCreate(activity, r.state, r.persistentState)，由于
     * Activity的onCreate()方法已经被调用，这就意味着Activity已经完成了整个启动过程
     */
    public void adapter(View view) {
        startActivity(new Intent(this, AdapterActivity.class));
    }


    public void mvp(View view) {
        startActivity(new Intent(this, MvpDagger2Activity.class));
    }

    public void WindowManager(View view) {
        startActivity(new Intent(this, WindowManagerTestActivity.class));
    }

    public void ButterKnife(View view) {
        startActivity(new Intent(this, ButterKnifeActivity.class));
    }

    public void lottie(View view) {
        startActivity(new Intent(this, LottieActivity.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.appenderClose();
        Mars.onDestroy();

    }

    public void frame(View view) {
        startActivity(new Intent(this, FrameActivity.class));
    }
}
