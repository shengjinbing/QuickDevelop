package com.modesty.quickdevelop.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Trace;
import android.util.Log;

import com.github.moduth.blockcanary.BlockCanary;
import com.github.moduth.blockcanary.BlockCanaryContext;
import com.github.moduth.blockcanary.internal.BlockInfo;
import com.modesty.logger.simplelog.LogLevel;
import com.modesty.logger.simplelog.Logger;
import com.modesty.quickdevelop.di.component.AppComponent;
import com.modesty.quickdevelop.di.component.DaggerAppComponent;
import com.modesty.quickdevelop.di.module.ApiModule;
import com.modesty.quickdevelop.di.module.AppModule;
import com.modesty.quickdevelop.network.NetConfig;
import com.modesty.quickdevelop.utils.TraceUtil;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * 启动优化
 *
 * 启动过程：
 * 1.预览窗口显示
 * 2.闪屏显示
 * 3.主页显示
 * 4.界面可操作
 *
 * 启动问题分析：
 * 1.点击图标很久不响应
 * 2.首页显示太慢
 * 3.首页显示后无法操作
 *
 * 具体优化方式分为：闪屏优化，业务梳理，业务优化，线程优化，GC优化，系统调用优化
 * 1、闪屏优化，可以将预览窗口实现成闪屏的效果，这样在高端手机上效果明显，在低端手机上会把总得闪屏时间变长，
 *    所有可以选择在android6.0，7.0上开启预览闪屏方案。将闪屏页和首页合并可以减少一个Activity会给线上带来
 *    100ms左右的优化，但是管理会变得复杂。
 * 2、业务梳理，懒加载要防止集中化，否则容易出现首页显示后用户无法操作的情形。
 * 3、业务优化，注意过多的是线程预加载会让我们的线程变得更加复杂
 * 3、线程优化，线程优化主要是减少cpu调度带来的波动，让应用的启动时间更加稳定；使用统一线程池进行控制线程数量，线程太多会互相竞争cpu资源
 *    防止主线程空转进行锁等待
 * 4，GC优化，启动过程中避免大量字符串操作，特别是序列号和反序列化过程，一些频繁创建对象，例如网络库和图片库中的byte数组
 *    Buffer可以复用，如果一些模块实在需要频繁创建对象，可以移到Native实现。java对象逃逸也容易引起GC问题，我们应该保证生命周期尽可能的短，在栈上进行销毁。
 * 5，通过systrace的System service类型，我们可以看到启动过程中System service的CPU工作情况。在启动过程中我们尽量不要做系统调用，列如PackageManageService
 *    操作，Binder调用等待。在启动过程中也不要过早的拉起应用的其他进程，System service和新的进程会竞争CPU资源。特别是系统内存不足的时候，
 *    我们拉起一个进程可能会出发low memory killer机制，导致系统杀和拉起（保活）大量进场，从而影响前台进程CPU。
 *
 * 进阶启动优化
 * 与业务无关
 * 1、i/o优化，启动过程不建议出现网络i/o，但是磁盘i/o需要进行优化，sharedPreference在初始化的时候还是要所有数据全部解析，
 *   如果数据超过1000条，解析需要100ms，如果只解析启动过程中用到的数据，这样大大减少了解析时间，启动过程适合使用随机读写的数据结构
 *   ArrayMap
 * 2、数据重排，Dex文件用到的类和安装包APK里面的各种资源都比较小，但是读的很频繁，我们可以利用系统这个机制按照读写顺序从新排列，减少磁盘i/o。
 * 3、类重排，启动过程中类加载顺序可以通过复写ClassLoader得到。通过ReDex来重排列类在dex中的顺序。
 * 4、资源文件重排
 * 5、类的加载，类加载过程中有个verify class的步骤，它需要校验方法的没一个指令毕竟耗时，可以hook将 classVerifyMode 设为 VERIFY_MODE_NONE
 *    Atlas可以实现，但是不支持ART平台。
 *
 *
 *Tinker在加载补丁后，应用的启动会降低5%-10%
 *应用加固对启动速度是灾难性质的
 *
 *
 * Created by 李想
 * on 2018/11/26
 */
public class BaseApplication extends Application {

    public static Application context;
    private static BaseApplication instance;
    private AppComponent appComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        String rootdir = MMKV.initialize(this);
        Log.d("BBBBB",rootdir);

        TraceUtil.i("BaseApplication:onCreate()");
        instance = this;
        context = this;
        //配置日志
        Logger.init("MODESTY_LOGG", LogLevel.FULL);
        NetConfig.instance().setLoggable(true);

        //配置LeakCanary
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...

        //配置BlockCanary
        BlockCanary.install(this,new AppBlockCanaryContext()).start();

        //配置dagger2
        initComponent();
        TraceUtil.o();

    }

    /**
     * 添加依赖关系，连接Module之间的关系
     */
    private void initComponent() {
        appComponent = DaggerAppComponent.builder()
                .apiModule(new ApiModule())
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public static Application getAppContext(){
        return context;
    }

    public static BaseApplication getInstance(){
        return instance;
    }



}
