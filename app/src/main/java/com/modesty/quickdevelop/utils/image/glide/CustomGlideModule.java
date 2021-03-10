package com.modesty.quickdevelop.utils.image.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.modesty.quickdevelop.utils.image.glide.progress.ProgressInterceptor;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2018/1/11 0011.
 * 为什么需要自定义glide？
 * 1.大多数情况下，我们想要实现的图片加载效果只需要一行代码就能解决了。但是Glide过于简洁的API也造成了一个问题，就是如果我们
 * 想要更改Glide的某些默认配置项应该怎么操作呢？
 * 2.自定义模块功能可以将更改Glide配置，替换Glide组件等操作独立出来，使得我们能轻松地对Glide的各种配置进行自定义，并且又
 * 和Glide的图片加载逻辑没有任何交集，这也是一种低耦合编程方式的体现。
 * 3.createGlide()方法中创建任何对象的时候都做了一个空检查，只有在对象为空的时候才会去创建它的实例。也就是说，
 * 如果我们可以在applyOptions()方法中提前就给这些对象初始化并赋值，那么在createGlide()方法中就不会再去重新创建它们的实例了，从而也就实现了更改Glide配置的功能
 *
 */
@GlideModule
public class CustomGlideModule extends AppGlideModule {
    /**
     *
     * setMemoryCache()
     * 用于配置Glide的内存缓存策略，默认配置是LruResourceCache。
     *
     * setBitmapPool()
     * 用于配置Glide的Bitmap缓存池，默认配置是LruBitmapPool。
     *
     * setDiskCache()
     * 用于配置Glide的硬盘缓存策略，默认配置是
     * InternalCacheDiskCacheFactory。getCacheDir(私有目录)-->/data/user/0/com.modesty.quickdevelop/cache
     * ExternalPreferredCacheDiskCacheFactory -> getExternalCacheDir（SD卡）->/storage/emulated/0/Android/data/com.modesty.quickdevelop/cache
     *
     * setDiskCacheService()
     * 用于配置Glide读取缓存中图片的异步执行器，默认配置是FifoPriorityThreadPoolExecutor，也就是先入先出原则。
     *
     * setResizeService()
     * 用于配置Glide读取非缓存中图片的异步执行器，默认配置也是FifoPriorityThreadPoolExecutor。
     *
     * setDecodeFormat()
     * 用于配置Glide加载图片的解码模式，默认配置是RGB_565。
     *
     * @param context
     * @param builder
     */
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        Log.d("CustomGlide_log",context.getExternalCacheDir().getAbsolutePath());
        Log.d("CustomGlide_log",context.getCacheDir().getAbsolutePath());
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        new RequestOptions().format(DecodeFormat.PREFER_RGB_565);

        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context)
                .setMemoryCacheScreens(2)
                .build();
        builder.setMemoryCache(new LruResourceCache(calculator.getMemoryCacheSize()));
        //builder.setBitmapPool(new LruBitmapPool(calculator.getMemoryCacheSize()));
        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context));
    }

    /**
     * 可以加入替换Glide的组件的逻辑
     * 默认网络处理是HttpURLConnection实现，这里可以替换网络模块
     * @param context
     * @param glide
     * @param registry
     */
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        Log.d("CustomGlide_log","registerComponents");
        //registry.append(GlideUrl.class, InputStream.class,new OkHttpUriLoader.Factory());
        //替换掉之前的网络框架
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new ProgressInterceptor())
                .build();
        registry.replace(GlideUrl.class, InputStream.class,new OkHttpUriLoader.Factory(okHttpClient));
    }
}
/*glide源码分析
        面试官：简历上最好不要写Glide，不是问源码那么简单 https://www.jianshu.com/p/330fa6422938
        首先，当下流行的图片加载框架有那么几个，可以拿 Glide 跟Fresco对比，例如这些：

        对比优缺点
        Glide：
        1.多种图片格式的缓存，适用于更多的内容表现形式（如Gif、WebP、缩略图、Video）
        2.生命周期集成（根据Activity或者Fragment的生命周期管理图片加载请求）
        3.高效处理Bitmap（bitmap的复用和主动回收，减少系统回收压力）
        4.高效的缓存策略，灵活（Picasso只会缓存原始尺寸的图片，Glide缓存的是多种规格），加载速度快且内存开销小
        （默认Bitmap格式的不同，使得内存开销是Picasso的一半）
        Fresco：
        1.最大的优势在于5.0以下(最低2.3)的bitmap加载。在5.0以下系统，Fresco将图片放到一个特别的内存区域(Ashmem区) ==>
        这个Ashmem区是一块匿名共享内存，Fresco 将Bitmap像素放到共享内存去了，共享内存是属于native堆内存。
        2.大大减少OOM（在更底层的Native层对OOM进行处理，图片将不再占用App的内存）
        3.适用于需要高性能加载大量图片的场景

        二、假如让你自己写个图片加载框架，你会考虑哪些问题？
        首先，梳理一下必要的图片加载框架的需求：
        1.异步加载：线程池
        2.切换线程：Handler，没有争议吧
        3.缓存：LruCache、DiskLruCache
        4.防止OOM：软引用、LruCache、图片压缩、Bitmap像素存储位置
        方法1：软引用
        方法2：onLowMemory 当内存不足的时候，Activity、Fragment会调用onLowMemory方法，可以在这个方法里去清除缓存，Glide使用的就是这一种方式来防止OOM。
        方法3：从Bitmap 像素存储位置考虑
        8.0 的Bitmap创建就两个点：
        创建native层Bitmap，在native堆申请内存。
        通过JNI创建java层Bitmap对象，这个对象在java堆中分配内存。
        像素数据是存在native层Bitmap，也就是证明8.0的Bitmap像素数据存在native堆中。
        5.内存泄露：注意ImageView的正确引用，生命周期管理
        当然，修改也比较简单粗暴，将ImageView用WeakReference修饰就完事了。
        事实上，这种方式虽然解决了内存泄露问题，但是并不完美，例如在界面退出的时候，我们除了希望ImageView被回收，同时希望加载图片的任务可以取消，队未执行的任务可以移除。
        Glide的做法是监听生命周期回调，看 RequestManager 这个类在Activity/fragment 销毁的时候，取消图片加载任务，细节大家可以自己去看源码。
        6.列表滑动加载的问题：加载错乱、队满任务过多问题
        图片错乱：由于RecyclerView或者LIstView的复用机制，网络加载图片开始的时候ImageView是第一个item的，加载成功之后ImageView由于复用可能跑到第10个item去了，在第10个item显示第一个item的图片肯定是错的。
        常规的做法是给ImageView设置tag，tag一般是图片地址，更新ImageView之前判断tag是否跟url一致。
        当然，可以在item从列表消失的时候，取消对应的图片加载任务。要考虑放在图片加载框架做还是放在UI做比较合适
        队满任务过多问题：列表滑动，会有很多图片请求，如果是第一次进入，没有缓存，那么队列会有很多任务在等待。所以在请求网络图片之前，需要判断队列中是否已经存在该任务，存在则不加到队列去。
        7.当然，还有一些不是必要的需求，例如加载动画等。

        2.1 异步加载：
        线程池，多少个？
        缓存一般有三级，内存缓存、硬盘、网络。
        由于网络会阻塞，所以读内存和硬盘可以放在一个线程池，网络需要另外一个线程池，网络也可以采用Okhttp内置的线程池。
        读硬盘和读网络需要放在不同的线程池中处理，所以用两个线程池比较合适。
        Glide 必然也需要多个线程池，看下源码是不是这样
public final class GlideBuilder {
  ...
    private GlideExecutor sourceExecutor; //加载源文件的线程池，包括网络加载
    private GlideExecutor diskCacheExecutor; //加载硬盘缓存的线程池
  ...
    private GlideExecutor animationExecutor; //动画线程池
  ....
}
Glide使用了三个线程池，不考虑动画的话就是两个

        1.自定义moulde
@GlideModule
public class CustomGlideModule extends AppGlideModule {
    /**
     *全局配置
     */
