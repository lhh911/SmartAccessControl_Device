package com.xsjqzt.module_main.presenter;

import com.jbb.library_common.basemvp.BaseMvpPresenter;
import com.jbb.library_common.retrofit.RetrofitManager;
import com.jbb.library_common.retrofit.other.BaseBean;
import com.jbb.library_common.retrofit.other.NetListeren;
import com.jbb.library_common.retrofit.other.SubscribeUtils;
import com.xsjqzt.module_main.model.KeyResBean;
import com.xsjqzt.module_main.model.RefreshTokenResBean;
import com.xsjqzt.module_main.model.TokenResBean;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.model.user.UserInfoSerializUtil;
import com.xsjqzt.module_main.service.ApiService;
import com.xsjqzt.module_main.view.TokenView;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class TokenPresenter extends BaseMvpPresenter<TokenView> {

    public void bindDevice(String sn1,String sn2,int eid) {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .bindDevice(sn1,sn2,eid), BaseBean.class, new NetListeren<BaseBean>() {
            @Override
            public void onSuccess(BaseBean bean) {
                if (mView != null) {
                    mView.bindDeviceSuccess();
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


    public void loadKey(String sn) {


        SubscribeUtils.subscribe2(RetrofitManager.getInstance().getService(ApiService.class)
                .loadKey(sn), KeyResBean.class, new NetListeren<KeyResBean>() {
            @Override
            public void onSuccess(KeyResBean bean) {
                if (mView != null) {
                    if(bean.getCode() == 0)
                        mView.loadKeySuccess(bean.getData());
                    else{
                        mView.loadKeyFail();
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
}
