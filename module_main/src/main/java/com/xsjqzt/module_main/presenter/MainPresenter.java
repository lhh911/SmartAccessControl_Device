package com.xsjqzt.module_main.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jbb.library_common.basemvp.BaseMvpPresenter;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.retrofit.RetrofitManager;
import com.jbb.library_common.retrofit.other.BaseBean;
import com.jbb.library_common.retrofit.other.NetListeren;
import com.jbb.library_common.retrofit.other.SubscribeUtils;
import com.xsjqzt.module_main.greendao.DbManager;
import com.xsjqzt.module_main.greendao.entity.ICCard;
import com.xsjqzt.module_main.greendao.entity.IDCard;
import com.xsjqzt.module_main.model.CardResBean;
import com.xsjqzt.module_main.model.FaceImageResBean;
import com.xsjqzt.module_main.model.ICCardResBean;
import com.xsjqzt.module_main.model.EntranceDetailsResBean;
import com.xsjqzt.module_main.model.IDCardResBean;
import com.xsjqzt.module_main.model.KeyResBean;
import com.xsjqzt.module_main.model.RefreshTokenResBean;
import com.xsjqzt.module_main.model.TokenResBean;
import com.xsjqzt.module_main.model.UploadCardResBean;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.model.user.UserInfoSerializUtil;
import com.xsjqzt.module_main.service.ApiService;
import com.xsjqzt.module_main.service.FaceImageDownService;
import com.xsjqzt.module_main.view.MainView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
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
                .getToken(sn1, skey), TokenResBean.class, new NetListeren<TokenResBean>() {
            @Override
            public void onSuccess(TokenResBean bean) {
                if (mView != null) {
                    if (bean.getData() != null) {
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
                .setVoice(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), volume), BaseBean.class, new NetListeren<BaseBean>() {
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

    public void uploadIDCardRecord(final int type, final String sn) {

        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .uploadIDCardRecordNoImage(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), sn, 1), UploadCardResBean.class, new NetListeren<UploadCardResBean>() {
            @Override
            public void onSuccess(UploadCardResBean bean) {
                if (mView != null)
                    mView.uploadCardSuccess(type, bean.getData().getId(), sn);
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


    public void uploadICCardRecord(final int type, final String sn) {

        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .uploadICCardRecordNoImage(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), sn, 1), UploadCardResBean.class, new NetListeren<UploadCardResBean>() {
            @Override
            public void onSuccess(UploadCardResBean bean) {
                if (mView != null)
                    mView.uploadCardSuccess(type, bean.getData().getId(), sn);
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

//    //获取当前进出口信息
//    public void entranceDetail() {
//        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
//                .entranceDetail(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken()), EntranceDetailsResBean.class, new NetListeren<EntranceDetailsResBean>() {
//            @Override
//            public void onSuccess(EntranceDetailsResBean bean) {
//                if (mView != null)
//                    mView.entranceDetailSuccess(bean);
//            }
//
//            @Override
//            public void onStart() {
//            }
//
//            @Override
//            public void onEnd() {
//            }
//
//            @Override
//            public void onError(Exception e) {
//                super.onError(e);
//            }
//        });
//    }


    //获取身份证数据
    public void loadIDCards(int sid) {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .loadIDCards(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), sid), IDCardResBean.class, new NetListeren<IDCardResBean>() {
            @Override
            public void onSuccess(final IDCardResBean info) {
//                if (mView != null)
//                    mView.loadIDCardsSuccess(bean);
                //插入数据库
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<CardResBean> data = info.getData();
                        List<IDCard> lists = new ArrayList<>();
                        for (CardResBean bean : data) {
                            IDCard card = new IDCard();
                            card.setSid(bean.getId());
                            card.setSn(bean.getSn());
                            card.setUser_id(bean.getUser_id());
                            card.setUser_name(bean.getUser_name());
                            lists.add(card);
                        }

                        DbManager.getInstance().getDaoSession().getIDCardDao().insertOrReplaceInTx(lists);
                    }
                }).start();

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
    public void loadICCards(int sid) {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .loadICCards(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), sid), ICCardResBean.class, new NetListeren<ICCardResBean>() {
            @Override
            public void onSuccess(final ICCardResBean info) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<CardResBean> data = info.getData();
                        List<ICCard> lists = new ArrayList<>();
                        for (CardResBean bean : data) {
                            ICCard card = new ICCard();
                            card.setSid(bean.getId());
                            card.setSn(bean.getSn());
                            card.setUser_id(bean.getUser_id());
                            card.setUser_name(bean.getUser_name());
                            lists.add(card);
                        }

                        DbManager.getInstance().getDaoSession().getICCardDao().insertOrReplaceInTx(lists);
                    }
                }).start();

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

    //拉取业主端注册的人脸图片，并注册到阅面
    public void loadFaceImage(final Context context, int sid) {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .loadFaceImage(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), 1), FaceImageResBean.class, new NetListeren<FaceImageResBean>() {
            @Override
            public void onSuccess(final FaceImageResBean info) {
                if(mView != null){
                    ArrayList<FaceImageResBean.DataBean> data = info.getData();

                    Intent it = new Intent(context,FaceImageDownService.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("data",data);
                    it.putExtras(bundle);
                    context.startService(it);
                }


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
