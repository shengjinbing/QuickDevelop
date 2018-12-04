package com.modesty.quickdevelop.network.response;

import java.util.List;

/**
 * 描述:统一处理HttpListResponse
 */

public class HttpListResponse<T> {
    public List<T> data;//数据
    public List<T> result;//数据
    public String message;//信息
    public int code;//服务器状态
}
