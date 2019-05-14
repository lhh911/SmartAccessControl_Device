package com.xsjqzt.module_main.service;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface ApiService {

    //获取加密key
    @FormUrlEncoded
    @POST("entrance/auth/get_key")
    Observable<ResponseBody> loadKey(@Field("sn") String sn);

    //获取token
    @FormUrlEncoded
    @POST("entrance/auth/get_key")
    Observable<ResponseBody> getToken(@Field("sn") String sn1, @Field("skey") String skey);

    //刷新token
    @FormUrlEncoded
    @POST("entrance/auth/refresh_token")
    Observable<ResponseBody> refreshToken(@Field("refresh_token") String refresh_token);

    //设置音量成功后通知服务器
    @FormUrlEncoded
    @POST("entrance/device/volume")
    Observable<ResponseBody> setVoice(@Header("Authorization") String token, @Field("volume") int volume);

    //上传身份证开门记录
    @POST("entrance/upload/idcard_record")
    Observable<ResponseBody> uploadIDCardRecord(@Header("Authorization") String token , @PartMap Map<String ,Object> params, @Part MultipartBody.Part file);

    //上传IC卡开门记录
    @POST("entrance/upload/iccard_record")
    Observable<ResponseBody> uploadICCardRecord(@Header("Authorization") String token , @PartMap Map<String ,Object> params, @Part MultipartBody.Part file);

    //获取当前进出口信息
    @POST("entrance/data/entrance_detail")
    Observable<ResponseBody> entranceDetail(@Header("Authorization") String token);

    //获取身份证数据
    @POST("entrance/data/idcards")
    Observable<ResponseBody> loadIDCards(@Header("Authorization") String token, @Query("page") int page, @Query("page_size") int page_size);

    //获取IC卡数据
    @POST("entrance/data/iccards")
    Observable<ResponseBody> loadICCards(@Header("Authorization") String token, @Query("page") int page, @Query("page_size") int page_size);
}
