package com.modesty.quickdevelop.ui.activitys;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.modesty.logger.simplelog.Logger;
import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.utils.image.ImageOperateUtil;
import com.modesty.quickdevelop.utils.image.glide.GlideApp;

/**
 * 1.注解(V4新特性)和自定义方法:Glide使用了annotation processor来生成API，
 * 允许应用修改RequestBuilder、RequestOptions和任意的包含在单一流式API库中的方法。
 * 2.区分RequestBuilder和RequestOptions
 * RequestBuilder：Glide.with()获取;GlideApp.with()生成的事GlideRequests
 * 指定加载类型。asBitmap()、asGif()、asDrawable()、asFile()。
 * 指定要加载url/model。
 * 指定要加载到那个View。
 * 指定要应用的RequestOption
 * 指定要应用的TransitionOption
 * 指定要加载的缩略图
 * RequestOptions：Glide中的大多请求参数都可以通过RequestOptions类和apply()方法来设置。
 * Placeholders 占位符
 * Transformations 变换
 * Caching Strategies 缓存策略
 * 组件特定参数：编码质量，解码参数等
 * 3.因为 Glide 的 with() 方法不光接受 Context，还接受 Activity 和 Fragment。此外，with() 方法还能自动地从你放
 *   入的各种东西里面提取出 Context，供它自己使用.
 * 4.glide磁盘缓存ImageView大小的图片，也可以自己调整.diskCacheStrategy(DiskCacheStrategy.ALL)
 * 5.Glide可以加载GIF动态图，而Picasso不能。
 */
public class ImageActivity extends AppCompatActivity {
    private ImageView mImageView;
    private ImageView mImageView1;
    private RequestOptions mOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mImageView = (ImageView) findViewById(R.id.image);
        mImageView1 = (ImageView) findViewById(R.id.image1);
        initRequestOptions();
        initView();
        initGlide();
        initPicasso();
    }


    /**
     * glide相关
     */
    private void initGlide() {
        GlideApp.with(this)
                .asBitmap()
                .load(R.drawable.toolbar_bg)
                .centerCrop()
                .miniThumb()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new CustomImageViewTarget(mImageView));
    }

    /**
     * Picasso相关
     */
    private void initPicasso() {

    }

    private void initRequestOptions() {
        mOptions = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
    }

    private void initView() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.toolbar_bg);
        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        mImageView.setImageBitmap(ImageOperateUtil.gerZoomRotateBitmap(bitmap,30));
    }

    public class CustomImageViewTarget extends ImageViewTarget<Bitmap> {

        public CustomImageViewTarget(ImageView view) {
            super(view);
        }

        @Override
        protected void setResource(@Nullable Bitmap resource) {
           view.setImageBitmap(resource);
        }

        /**
         * 改变图片大小
         * @param cb
         */
        @Override
        public void getSize(SizeReadyCallback cb) {
            cb.onSizeReady(dip2px(getApplicationContext(), 300), dip2px(getApplicationContext(), 300));
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 加载缩略图
     * RequestListener
     */
    public void loadThumbnail() {
        Glide.with(this)
                .load(ImageConfig.URL_WEBP)
                .thumbnail(Glide.with(this).load(ImageConfig.URL_JPEG))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Logger.e("onLoadFailed --->" + e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Logger.e("onResourceReady---> " + resource);
                        return false;
                    }
                })
                .into(mImageView);
    }

    /**
     * Transitions被要求用在 RequestBuilder
     */
    public void loadTransition() {
        //使用变换效果
        Glide.with(this)
                .load(ImageConfig.URL_WEBP)
                .apply(mOptions)
                .transition(new DrawableTransitionOptions().crossFade(2000))
                .thumbnail(Glide.with(this)
                        .load(ImageConfig.URL_JPEG))
                .into(mImageView);
    }

    public final static class ImageConfig {
        static String URL_WEBP = "";
        static String URL_JPEG = "";
    }
}
