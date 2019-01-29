package com.modesty.quickdevelop.network.api;

import com.modesty.quickdevelop.Constants;
import com.modesty.quickdevelop.bean.ActionListBean;
import com.modesty.quickdevelop.bean.HomeBannerBean;
import com.modesty.quickdevelop.bean.StarListBean;
import com.modesty.quickdevelop.bean.StarListData;
import com.modesty.quickdevelop.bean.User;
import com.modesty.quickdevelop.network.response.HttpResponse;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * annotations: GET, POST, PUT, DELETE,  HEAD
 * <p>
 * <p>
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

    @FormUrlEncoded
    @POST(Constants.STAR_SEARCH)
    Flowable<StarListData> getSeachList1(@Field("name") String name);


    /**********************GET******************************/
    @GET("group/{id}/users")
    Call<List<User>> groupList(@Path("id") int groupId, @Query("sort") String sort);

    @GET("group/{id}/users")
    Call<List<User>> groupList(@Path("id") int groupId, @QueryMap Map<String, String> options);

    /**********************Headers******************************/
    @Headers("Cache-Control: max-age=640000")
    @GET("widget/list")
    Call<List<User>> widgetList();

    @Headers({
            "Accept: application/vnd.github.v3.full+json",
            "User-Agent: Retrofit-Sample-App"
    })
    @GET("users/{username}")
    Call<User> getUser(@Path("username") String username);


    @GET("user")
    Call<User> getUser1(@Header("Authorization") String authorization);

    @GET("user")
    Call<User> getUser(@HeaderMap Map<String, String> headers);

    /**********************POST******************************/

    /**
     * 实现传递json参数，@Body用于传输非表单数据
     * Map<String, String[]> map = new HashMap<>();
     * map.put("accountNames", accounts);
     * String json = new Gson().toJson(map);
     * RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
     */
    @POST("users/new")
    Call<User> createUser(@Body RequestBody body);

    @POST("users/new")
    Call<User> createUser(@Body User user);

    @FormUrlEncoded
    @POST("user/edit")
    Call<User> updateUser(@Field("first_name") String first, @Field("last_name") String last);

    /**
     * 表明是一个表单格式的请求（Content-Type:application/x-www-form-urlencoded）
     * <code>Field("username")</code> 表示将后面的 <code>String name</code> 中name的取值作为 username 的值
     */
    @POST("/form")
    @FormUrlEncoded
    Call<ResponseBody> testFormUrlEncoded1(@Field("username") String name, @Field("age") int age);

    /**
     * Map的key作为表单的键
     */
    @POST("/form")
    @FormUrlEncoded
    Call<ResponseBody> testFormUrlEncoded2(@FieldMap Map<String, Object> map);


    /**********************Multipart使用******************************/

    @Multipart
    @PUT("user/photo")
    Call<User> updateUser(@Part("photo") RequestBody photo, @Part("description") RequestBody description);

    /**
     * {@link Part} 后面支持三种类型，
     * 1.{@link RequestBody}、
     * 2.{@link okhttp3.MultipartBody.Part}
     * 3.任意类型
     * 除 {@link okhttp3.MultipartBody.Part} 以外，其它类型都必须带上表单字段
     * ({@link okhttp3.MultipartBody.Part} 中已经包含了表单字段的信息)，
     */
    @POST("/form")
    @Multipart
    Call<ResponseBody> testFileUpload1(@Part("name") RequestBody name,
                                       @Part("age") RequestBody age,
                                       @Part MultipartBody.Part file);

    /**
     * PartMap 注解支持一个Map作为参数，支持 {@link RequestBody } 类型，
     * 如果有其它的类型，会被{@link retrofit2.Converter}转换，如后面会介绍的
     * 使用{@link com.google.gson.Gson} 的 {@link retrofit2.converter.gson.GsonRequestBodyConverter}
     * 所以{@link MultipartBody.Part} 就不适用了,所以文件只能用<b> @Part MultipartBody.Part </b>
     */
    @POST("/form")
    @Multipart
    Call<ResponseBody> testFileUpload2(@PartMap Map<String, RequestBody> args, @Part MultipartBody.Part file);

    @POST("/form")
    @Multipart
    Call<ResponseBody> testFileUpload3(@PartMap Map<String, RequestBody> args);
}

  /*  // 1.具体使用
    MediaType textType = MediaType.parse("text/plain");
    RequestBody name = RequestBody.create(textType, "Carson");
    RequestBody age = RequestBody.create(textType, "24");
    RequestBody file = RequestBody.create(MediaType.parse("application/octet-stream"), "这里是模拟文件的内容");

    // 2.@Part
    MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "test.txt", file);
    Call<ResponseBody> call3 = service.testFileUpload1(name, age, filePart);


    // 3.@PartMap
    // 实现和上面同样的效果
    Map<String, RequestBody> fileUpload2Args = new HashMap<>();
    fileUpload2Args.put("name", name);
    fileUpload2Args.put("age", age);

    //这里并不会被当成文件，因为没有文件名(包含在Content-Disposition请求头中)，但上面的 filePart 有
    //fileUpload2Args.put("file", file);


    Call<ResponseBody> call4 = service.testFileUpload2(fileUpload2Args, filePart); //单独处理文件
    ResponseBodyPrinter.printResponseBody(call4);*/


  //批量上传图片
    /*public Flowable<UploadsBean> uploadFiles(File... files) {
        //组装partMap对象
        LogUtils.d("BBBBB", files.length + "文件长度");
        Map<String, RequestBody> partMap = new HashMap<>();
        for (File file : files) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("image*//*"), file);
            partMap.put("files\";filename=\"" + file.getName() + "\"", fileBody);
        }
    }*/


