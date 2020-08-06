package com.modesty.quickdevelop.utils.image.glide;

import android.annotation.SuppressLint;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.annotation.GlideType;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;


/**
 * 被 @GlideExtension 注解的类应以工具类的思维编写。这种类应该有一个私有的、空的构造方法，应为 final 类型，
 * 并且仅包含静态方法。被注解的类可以含有静态变量，可以引用其他的类或对象。
 * Created by Administrator on 2018/1/11 0011.
 */

@GlideExtension
public class CustomGlideExtension {
    /*GlideExtension
    为了添加新的方法，修改已有的方法或者添加对其他类型格式的支持，
    你需要在扩展中使用加了注解的静态方法。
    GlideOption用来添加自定义的方法，GlideType用来支持新的格式*/


    //缩略图的最小尺寸，单位：px
    private static final int MINI_THUMB_SIZE = 100;

    private static final RequestOptions DECODE_TYPE_GIF = GlideOptions.decodeTypeOf(GifDrawable.class).lock();

    private CustomGlideExtension() {
    }

    /**
     * 1.自己新增的方法的第一个参数必须是RequestOptions options
     * 2.方法必须是静态的
     * 3.你可以为方法任意添加参数，但要保证第一个参数为 RequestOptions。
     * 4.这些生成的方法在标准的 Glide 和 RequestOptions 类里不可用，只存在于生成的等效类中。
     *
     * @param options
     */
    @SuppressLint("CheckResult")
    @GlideOption
    public static void miniThumb(RequestOptions options) {
        options.fitCenter()
                .override(MINI_THUMB_SIZE);

    }

    /**
     * 1.注解的方法允许你添加对新的资源类型的支持，包括指定默认选项。
     * @param requestBuilder
     */
    @SuppressLint("CheckResult")
    @GlideType(GifDrawable.class)
    public static void asGIF(RequestBuilder<GifDrawable> requestBuilder) {
        requestBuilder
                .transition(new DrawableTransitionOptions())
                .apply(DECODE_TYPE_GIF);
    }
}
