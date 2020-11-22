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

import java.io.InputStream;

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
        registry.append(GlideUrl.class, InputStream.class,new OkHttpUriLoader.Factory());
    }
}