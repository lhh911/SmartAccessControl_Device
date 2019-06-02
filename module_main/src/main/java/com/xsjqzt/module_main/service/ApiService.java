package com.xsjqzt.module_main.service;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

    //设备绑定
    @FormUrlEncoded
    @POST("entrance/auth/bind")
    Observable<ResponseBody> bindDevice(@Field("sn1") String sn1, @Field("sn2") String sn2, @Field("eid") int eid);

    //获取加密key
    @FormUrlEncoded
    @POST("entrance/auth/get_key")
    Observable<ResponseBody> loadKey(@Field("sn") String sn);

    //获取token
    @FormUrlEncoded
    @POST("entrance/auth/get_token")
    Observable<ResponseBody> getToken(@Field("sn") String sn1, @Field("skey") String skey);

    //刷新token
    @FormUrlEncoded
    @POST("entrance/auth/refresh_token")
    Observable<ResponseBody> refreshToken(@Field("refresh_token") String refresh_token);

    //设置音量成功后通知服务器
    @FormUrlEncoded
    @POST("entrance/device/volume")
    Observable<ResponseBody> setVoice(@Header("Authorization") String token, @Field("volume") int volume);


    //上传IC卡开门记录图片
    @Multipart
    @POST("entrance/upload/upload_image")
    Observable<ResponseBody> uploadCardRecordByImage(@Header("Authorization") String token, @PartMap Map<String, RequestBody> params, @Part MultipartBody.Part file);


    //上传IC卡开门记录不含图片
    @FormUrlEncoded
    @POST("entrance/upload/iccard_record")
    Observable<ResponseBody> uploadICCardRecordNoImage(@Header("Authorization") String token, @Field("sn") String sn, @Field("status") int status);

    //上传ID卡开门记录不含图片
    @FormUrlEncoded
    @POST("entrance/upload/idcard_record")
    Observable<ResponseBody> uploadIDCardRecordNoImage(@Header("Authorization") String token, @Field("sn") String sn, @Field("status") int status);



    //获取当前进出口信息
    @POST("entrance/data/entrance_detail")
    Observable<ResponseBody> entranceDetail(@Header("Authorization") String token);

    //获取身份证数据
    @POST("entrance/data/idcards")
    Observable<ResponseBody> loadIDCards(@Header("Authorization") String token, @Query("update_time") int update_time);

    //获取IC卡数据
    @POST("entrance/data/iccards")
    Observable<ResponseBody> loadICCards(@Header("Authorization") String token, @Query("update_time") int update_time);

    //获取临时开门密码数据
    @POST("entrance/data/invite_codes")
    Observable<ResponseBody> downOpenCode(@Header("Authorization") String token, @Query("update_time") int update_time);

    //获取人脸注册数据
    @FormUrlEncoded
    @POST("entrance/data/user_faces")
    Observable<ResponseBody> loadFaceImage(@Header("Authorization") String token, @Field("update_time") int update_time);

    //下载人脸图片
    @Streaming
    @GET
    Observable<ResponseBody> downFaceImage(@Header("Authorization") String token, @Url String image);

    //上传人脸照片状态
    @FormUrlEncoded
    @POST("entrance/data/user_faces")
    Observable<ResponseBody> updateFacesStatus(@Header("Authorization") String token, @Field("status") int status, @Field("user_id") int user_id, @Field("code") String code);


}
