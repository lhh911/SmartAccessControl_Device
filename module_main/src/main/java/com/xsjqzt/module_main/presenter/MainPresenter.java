package com.xsjqzt.module_main.presenter;

import com.jbb.library_common.basemvp.BaseMvpPresenter;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.retrofit.RetrofitManager;
import com.jbb.library_common.retrofit.other.BaseBean;
import com.jbb.library_common.retrofit.other.NetListeren;
import com.jbb.library_common.retrofit.other.SubscribeUtils;
import com.xsjqzt.module_main.model.CardResBean;
import com.xsjqzt.module_main.model.EntranceDetailsResBean;
import com.xsjqzt.module_main.model.KeyResBean;
import com.xsjqzt.module_main.model.RefreshTokenResBean;
import com.xsjqzt.module_main.model.TokenResBean;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.model.user.UserInfoSerializUtil;
import com.xsjqzt.module_main.service.ApiService;
import com.xsjqzt.module_main.view.MainView;
import com.xsjqzt.module_main.view.TokenView;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainPresenter extends BaseMvpPresenter<MainView> {

    public void loadKey(String sn) {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .loadKey(sn), KeyResBean.class, new NetListeren<KeyResBean>() {
            @Override
            public void onSuccess(KeyResBean bean) {
                if (mView != null) {
                    mView.loadKeySuccess(bean.getData());
                }

            }

            @Override
            public void onStart() {
                if (mView != null)
                    mView.showLoading();
            }

            @Override
            public void onEnd() {
                if (mView != null)
                    mView.hideLoading();
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
            }
        });
    }

    public void getToken(String sn1, String skey) {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .getToken(sn1,skey), TokenResBean.class, new NetListeren<TokenResBean>() {
            @Override
            public void onSuccess(TokenResBean bean) {
                if (mView != null) {
                    if(bean.getData() != null) {
                        UserInfoInstance.getInstance().setToken(bean.getData().getToken());
                        UserInfoInstance.getInstance().setRefresh_token(bean.getData().getRefresh_token());
                        UserInfoSerializUtil.serializUserInstance();
                        mView.getTokenSuccess();
                    }
                }

            }

            @Override
            public void onStart() {
                if (mView != null)
                    mView.showLoading();
            }

            @Override
            public void onEnd() {
                if (mView != null)
                    mView.hideLoading();
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
            }
        });
    }

    public void refreshToken() {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .refreshToken(UserInfoInstance.getInstance().getRefresh_token()), RefreshTokenResBean.class, new NetListeren<RefreshTokenResBean>() {
            @Override
            public void onSuccess(RefreshTokenResBean bean) {
                String token = bean.getData().getToken();
                UserInfoInstance.getInstance().setToken(token);
                UserInfoSerializUtil.serializUserInstance();

            }

            @Override
            public void onStart() {
            }

            @Override
            public void onEnd() {
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
            }
        });
    }

    //通知调节音量成功后告诉后台
    public void setVoice(int volume) {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .setVoice(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken() ,volume), RefreshTokenResBean.class, new NetListeren<RefreshTokenResBean>() {
            @Override
            public void onSuccess(RefreshTokenResBean bean) {
                String token = bean.getData().getToken();
                UserInfoInstance.getInstance().setToken(token);
                UserInfoSerializUtil.serializUserInstance();

            }

            @Override
            public void onStart() {
            }

            @Override
            public void onEnd() {
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
            }
        });
    }

    public void uploadIDCardRecord(Map<String,Object> params , MultipartBody.Part file) {
        //创建rquestbody对象
        File file1 = new File("");
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file1.getName(), requestFile);


        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .uploadIDCardRecord(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken() ,params ,file), BaseBean.class, new NetListeren<BaseBean>() {
            @Override
            public void onSuccess(BaseBean bean) {


            }

            @Override
            public void onStart() {
            }

            @Override
            public void onEnd() {
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
            }
        });
    }


    public void uploadICCardRecord(Map<String,Object> params , MultipartBody.Part file) {
        //创建rquestbody对象
        File file1 = new File("");
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file1.getName(), requestFile);


        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .uploadICCardRecord(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken() ,params ,file), BaseBean.class, new NetListeren<BaseBean>() {
            @Override
            public void onSuccess(BaseBean bean) {


            }

            @Override
            public void onStart() {
            }

            @Override
            public void onEnd() {
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
            }
        });
    }

    //获取当前进出口信息
    public void entranceDetail() {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .entranceDetail(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken()), EntranceDetailsResBean.class, new NetListeren<EntranceDetailsResBean>() {
            @Override
            public void onSuccess(EntranceDetailsResBean bean) {
                if(mView != null)
                    mView.entranceDetailSuccess(bean);
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onEnd() {
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
            }
        });
    }


    //获取身份证数据
    public void loadIDCards(int page,int page_size) {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .loadIDCards(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(),page,page_size), CardResBean.class, new NetListeren<CardResBean>() {
            @Override
            public void onSuccess(CardResBean bean) {
                if(mView != null)
                    mView.loadIDCardsSuccess(bean);
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onEnd() {
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
            }
        });
    }

    //获取身份证数据
    public void loadICCards(int page,int page_size) {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .loadICCards(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(),page,page_size), CardResBean.class, new NetListeren<CardResBean>() {
            @Override
            public void onSuccess(CardResBean bean) {
                if(mView != null)
                    mView.loadICCardsSuccess(bean);
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onEnd() {
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
            }
        });
    }

}
