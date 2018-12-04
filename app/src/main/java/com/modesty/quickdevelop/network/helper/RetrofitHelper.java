package com.modesty.quickdevelop.network.helper;


import com.modesty.quickdevelop.bean.ActionListBean;
import com.modesty.quickdevelop.bean.HomeBannerBean;
import com.modesty.quickdevelop.bean.StarListBean;
import com.modesty.quickdevelop.network.api.ApiService;
import com.modesty.quickdevelop.network.response.HttpResponse;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Administrator on 2017/11/22 0022.
 */

public class RetrofitHelper {
    private final ApiService actionService;

    public RetrofitHelper(ApiService actionService) {
        this.actionService = actionService;
    }

    public Flowable<HomeBannerBean> getHomeBanner() {
        return actionService.getHomeBanner();
    }

    public Flowable<HttpResponse<List<ActionListBean>>> getActionList(int page, int type, String starCode) {
        return actionService.getActionList(page, type, starCode);

    }

    public Flowable<HttpResponse<List<StarListBean>>> getSeachList(String name) {
        return actionService.getSeachList(name);
    }

    public Flowable<HttpResponse<String>> getLoginCode(String phone) {
        return actionService.getLoginCode(phone);
    }

}
