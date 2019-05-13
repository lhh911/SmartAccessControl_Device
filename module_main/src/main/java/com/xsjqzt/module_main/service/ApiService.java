package com.xsjqzt.module_main.service;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
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
}
