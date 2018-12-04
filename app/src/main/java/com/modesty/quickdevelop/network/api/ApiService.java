package com.modesty.quickdevelop.network.api;

import com.modesty.quickdevelop.Constants;
import com.modesty.quickdevelop.bean.ActionListBean;
import com.modesty.quickdevelop.bean.HomeBannerBean;
import com.modesty.quickdevelop.bean.StarListBean;
import com.modesty.quickdevelop.network.response.HttpResponse;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by 李想
 * on 2018/11/26
 */
public interface ApiService {
    @FormUrlEncoded
    @POST(Constants.ACTION_LIST)
    Flowable<HttpResponse<List<ActionListBean>>> getActionList(@Field("currePageNumber") int currePageNumber,
                                                               @Field("type") int type,
                                                               @Field("starCode") String starCode);

    @POST(Constants.HOME_BANNER)
    Flowable<HomeBannerBean> getHomeBanner();

    @FormUrlEncoded
    @POST(Constants.LOGIN_LOGINCODE)
    Flowable<HttpResponse<String>> getLoginCode(@Field("mobile") String mobile);

    @FormUrlEncoded
    @POST(Constants.STAR_SEARCH)
    Flowable<HttpResponse<List<StarListBean>>> getSeachList(@Field("name") String name);
}
