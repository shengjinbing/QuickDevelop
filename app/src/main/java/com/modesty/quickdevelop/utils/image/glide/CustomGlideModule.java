package com.modesty.quickdevelop.utils.image.glide;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * Created by Administrator on 2018/1/11 0011.
 */

@GlideModule
public class CustomGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        super.applyOptions(context, builder);
    }
}