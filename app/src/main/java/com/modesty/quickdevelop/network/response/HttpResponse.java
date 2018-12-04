package com.modesty.quickdevelop.network.response;

/**
 * 描述:统一处理HttpResponse
 */

public class HttpResponse<T> {
    public T data;//数据
    public T result;//数据
    public String message;//信息
    public String code;//服务器状态
    public long t;//时间戳

}
