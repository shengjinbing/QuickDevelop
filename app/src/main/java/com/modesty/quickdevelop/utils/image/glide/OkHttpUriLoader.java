package com.modesty.quickdevelop.utils.image.glide;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * Created by lixiang on 2020/11/18
 * Describe:
 */
public class OkHttpUriLoader implements ModelLoader<GlideUrl, InputStream> {


    public static final Option<Integer> TIMEOUT = Option.memory(
            "com.bumptech.glide.load.model.stream.HttpGlideUrlLoader.Timeout", 2500);

    private OkHttpClient okHttpClient;

    @Nullable
    private final ModelCache<GlideUrl, GlideUrl> modelCache;

    public OkHttpUriLoader(OkHttpClient okHttpClient, ModelCache<GlideUrl, GlideUrl> modelCache) {
        this.okHttpClient = okHttpClient;
        this.modelCache = modelCache;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(GlideUrl model, int width, int height, Options options) {
        // GlideUrls memoize parsed URLs so caching them saves a few object instantiations and time
        // spent parsing urls.
        GlideUrl url = model;
        if (modelCache != null) {
            url = modelCache.get(model, 0, 0);
            if (url == null) {
                modelCache.put(model, 0, 0, model);
                url = model;
            }
        }
        int timeout = options.get(TIMEOUT);
        return new LoadData<>(model, new OkHttpUrlFetcher(okHttpClient, model, timeout));

    }

    @Override
    public boolean handles(GlideUrl model) {
        return true;
    }

    /**
     * Factory for loading {@link InputStream}s from http/https {@link Uri}s.
     */
    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
        private OkHttpClient client;
        private final ModelCache<GlideUrl, GlideUrl> modelCache = new ModelCache<>(500);

        private synchronized OkHttpClient getOkHttpClient() {
            if (client == null) {
                client = new OkHttpClient();
            }
            return client;
        }

        @Override
        public ModelLoader<GlideUrl, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new OkHttpUriLoader(getOkHttpClient(), modelCache);
        }

        @Override
        public void teardown() {
            // Do nothing.
        }
    }
}
