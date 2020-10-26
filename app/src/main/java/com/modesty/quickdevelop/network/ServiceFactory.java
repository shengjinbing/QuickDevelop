package com.modesty.quickdevelop.network;


import com.modesty.quickdevelop.BuildConfig;
import com.modesty.quickdevelop.RetrofitFactory;
import com.modesty.quickdevelop.network.api.ApiService;

/**
 * @author wangzhiyuan
 * @since 2018/2/5
 */
public final class ServiceFactory {

    private ServiceFactory() {
    }

    public static synchronized <T> T newService(String baseUrl, Class<T> service) {
        return RetrofitFactory.getInstance(baseUrl).create(service);
    }

    public static synchronized ApiService newApiService() {
        return newService(BuildConfig.DEBUG ? BuildConfig.BASE_URL : BuildConfig.BASE_URL, ApiService.class);
    }


}
