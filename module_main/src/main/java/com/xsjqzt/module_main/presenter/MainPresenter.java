package com.xsjqzt.module_main.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.jbb.library_common.basemvp.BaseMvpPresenter;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.retrofit.RetrofitManager;
import com.jbb.library_common.retrofit.other.BaseBean;
import com.jbb.library_common.retrofit.other.NetListeren;
import com.jbb.library_common.retrofit.other.SubscribeUtils;
import com.jbb.library_common.utils.SharePreferensUtil;
import com.xsjqzt.module_main.greendao.DbManager;
import com.xsjqzt.module_main.greendao.ICCardDao;
import com.xsjqzt.module_main.greendao.IDCardDao;
import com.xsjqzt.module_main.greendao.OpenCodeDao;
import com.xsjqzt.module_main.greendao.entity.ICCard;
import com.xsjqzt.module_main.greendao.entity.IDCard;
import com.xsjqzt.module_main.greendao.entity.OpenCode;
import com.xsjqzt.module_main.model.ADResBean;
import com.xsjqzt.module_main.model.CardResBean;
import com.xsjqzt.module_main.model.EntranceDetailsResBean;
import com.xsjqzt.module_main.model.FaceImageResBean;
import com.xsjqzt.module_main.model.ICCardResBean;
import com.xsjqzt.module_main.model.IDCardResBean;
import com.xsjqzt.module_main.model.KeyResBean;
import com.xsjqzt.module_main.model.PswCodeResBean;
import com.xsjqzt.module_main.model.RefreshTokenResBean;
import com.xsjqzt.module_main.model.RoomNumByUserIdResBean;
import com.xsjqzt.module_main.model.TokenResBean;
import com.xsjqzt.module_main.model.UploadCardResBean;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.model.user.UserInfoSerializUtil;
import com.xsjqzt.module_main.service.ApiService;
import com.xsjqzt.module_main.service.DownAllDataService;
import com.xsjqzt.module_main.service.FaceImageDownService;
import com.xsjqzt.module_main.service.VideoAdDownService;
import com.xsjqzt.module_main.view.MainView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

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
//                super.onError(e);
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
//                super.onError(e);
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
//                super.onError(e);
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
//                super.onError(e);
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
//                super.onError(e);
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
//                super.onError(e);
            }
        });
    }

//    //获取当前进出口信息
    public void entranceDetail() {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .entranceDetail(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken()), EntranceDetailsResBean.class, new NetListeren<EntranceDetailsResBean>() {
            @Override
            public void onSuccess(EntranceDetailsResBean bean) {
                if (mView != null)
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
//                super.onError(e);
            }
        });
    }


    //获取身份证数据
    public void loadIDCards(int update_time) {
        SubscribeUtils.subscribe3(RetrofitManager.getInstance().getService(ApiService.class)
                .loadIDCards(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), update_time), IDCardResBean.class, new NetListeren<IDCardResBean>() {
            @Override
            public void onSuccess(final IDCardResBean info) {
                //插入数据库
                List<CardResBean> data = info.getData();
                if (data == null || data.isEmpty())
                    return;
                List<IDCard> lists = new ArrayList<>();
                for (CardResBean bean : data) {
                    if (bean.isIs_delete()) {
                        IDCard unique = DbManager.getInstance().getDaoSession().getIDCardDao().queryBuilder().where(IDCardDao.Properties.Sn.eq(bean.getSn())).unique();
                        if (unique != null)
                            DbManager.getInstance().getDaoSession().getIDCardDao().delete(unique);
                    } else {
                        IDCard card = new IDCard();
                        card.setSid(bean.getId());
                        card.setSn(bean.getSn());
                        card.setUser_id(bean.getUser_id());
                        card.setUser_name(bean.getUser_name());
                        card.setUpdate_time(bean.getUpdate_time());
                        lists.add(card);
                    }
                }
                if (lists.size() > 0)
                    DbManager.getInstance().getDaoSession().getIDCardDao().insertOrReplaceInTx(lists);

            }
            @Override
            public void onError(Exception e) {
//                super.onError(e);
            }
        });
    }

    //获取身份证数据
    public void loadICCards(int update_time) {
        SubscribeUtils.subscribe3(RetrofitManager.getInstance().getService(ApiService.class)
                .loadICCards(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), update_time), ICCardResBean.class, new NetListeren<ICCardResBean>() {
            @Override
            public void onSuccess(final ICCardResBean info) {


                List<CardResBean> data = info.getData();
                if (data == null || data.isEmpty())
                    return;
                List<ICCard> lists = new ArrayList<>();
                for (CardResBean bean : data) {
                    if (bean.isIs_delete()) {
                        ICCard unique = DbManager.getInstance().getDaoSession().getICCardDao().queryBuilder().where(ICCardDao.Properties.Sn.eq(bean.getSn())).unique();
                        if (unique != null)
                            DbManager.getInstance().getDaoSession().getICCardDao().delete(unique);
                    } else {
                        ICCard card = new ICCard();
                        card.setSid(bean.getId());
                        card.setSn(bean.getSn());
                        card.setUser_id(bean.getUser_id());
                        card.setUser_name(bean.getUser_name());
                        card.setUpdate_time(bean.getUpdate_time());
                        lists.add(card);
                    }
                }
                if (lists.size() > 0)
                    DbManager.getInstance().getDaoSession().getICCardDao().insertOrReplaceInTx(lists);


            }
            @Override
            public void onError(Exception e) {
//                super.onError(e);
            }
        });
    }

    //拉取业主端注册的人脸图片，并注册到阅面
    public void loadFaceImage(final Context context, int update_time) {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .loadFaceImage(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), update_time), FaceImageResBean.class, new NetListeren<FaceImageResBean>() {
            @Override
            public void onSuccess(final FaceImageResBean info) {
                ArrayList<FaceImageResBean.DataBean> data = info.getData();
                if(data == null || data.isEmpty())
                    return;
                Intent it = new Intent(context, FaceImageDownService.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("data", data);
                it.putExtras(bundle);
                context.startService(it);

            }
            @Override
            public void onError(Exception e) {
//                super.onError(e);
            }

        });
    }

    public void downOpenCode(int update_time) {

        SubscribeUtils.subscribe3(RetrofitManager.getInstance().getService(ApiService.class)
                .downOpenCode(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), update_time), PswCodeResBean.class, new NetListeren<PswCodeResBean>() {
            @Override
            public void onSuccess(final PswCodeResBean info) {
                //插入数据库

                List<PswCodeResBean.DataBean> data = info.getData();
                if (data == null || data.isEmpty())
                    return;

                List<OpenCode> lists = new ArrayList<>();
                for (PswCodeResBean.DataBean bean : data) {
                    if (bean.isIs_delete()) {
                        OpenCode unique = DbManager.getInstance().getDaoSession().getOpenCodeDao().queryBuilder().where(OpenCodeDao.Properties.Code.eq(bean.getCodeX())).unique();
                        if (unique != null)
                            DbManager.getInstance().getDaoSession().getOpenCodeDao().delete(unique);
                    } else {
                        OpenCode openCode = new OpenCode();
                        openCode.setSid(bean.getId());
                        openCode.setCode(bean.getCodeX());
                        openCode.setUser_id(bean.getUser_id());
                        openCode.setUpdate_time(bean.getUpdate_time());
                        openCode.setExpiry_time(bean.getExpiry_time());
                        lists.add(openCode);
                    }
                }
                if (lists.size() > 0)
                    DbManager.getInstance().getDaoSession().getOpenCodeDao().insertOrReplaceInTx(lists);

            }

            @Override
            public void onError(Exception e) {
//                super.onError(e);
            }
        });

    }

    public void uploadCodeRecord(final int type, final String code) {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .uploadCodeRecord(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), code, 1), UploadCardResBean.class, new NetListeren<UploadCardResBean>() {
            @Override
            public void onSuccess(UploadCardResBean bean) {
                if (mView != null)
                    mView.uploadCardSuccess(type, bean.getData().getId(), code);
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onEnd() {
            }

            @Override
            public void onError(Exception e) {
//                super.onError(e);
            }
        });
    }

    public void uploadFaceRecord(final int type, final int user_id) {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .uploadFaceRecord(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), user_id, 1), UploadCardResBean.class, new NetListeren<UploadCardResBean>() {
            @Override
            public void onSuccess(UploadCardResBean bean) {
                if (mView != null)
                    mView.uploadCardSuccess(type, bean.getData().getId(), String.valueOf(user_id));
            }


            @Override
            public void onError(Exception e) {
//                super.onError(e);
            }
        });
    }


    public void getUseridByRoom(final String inputNum) {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .getUseridByRoom(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), inputNum), RoomNumByUserIdResBean.class, new NetListeren<RoomNumByUserIdResBean>() {
            @Override
            public void onSuccess(RoomNumByUserIdResBean bean) {
                if (mView != null)
                    mView.getUseridByRoomSuccess(true,bean.getData().getId()+"",inputNum);
            }



            @Override
            public void onError(Exception e) {
                super.onError(e);
                if (mView != null)
                    mView.getUseridByRoomSuccess(false,e.getMessage(),"");
            }
        });
    }

    public void loadBanner(final Context context, long update_time) {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .loadBanner(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), update_time), ADResBean.class, new NetListeren<ADResBean>() {
            @Override
            public void onSuccess(ADResBean bean) {
                if(bean.getData() != null){
                    int type = bean.getData().getType();
                    int update_time1 = bean.getData().getUpdate_time();
                    List<ADResBean.DataBean.ListBean> list = bean.getData().getList();

                    ArrayList<String> datas = new ArrayList<>();
                    if(list != null){
                        for(ADResBean.DataBean.ListBean data : list){
                            String path = data.getPath();
                            datas.add(path);
                        }
                    }

                    if(type == 1){//banner
                        if(datas.size()>0){
                            String str = JSON.toJSONString(datas);
                            SharePreferensUtil.putString(KeyContacts.SP_KEY_BANNER_DATA,str,KeyContacts.SP_NAME_USERINFO);
                            if(mView != null){
                                mView.loadBannerSuccess();
                            }
                        }

                    }else if(type == 2){//视频广告
                        if(datas.size()>0) {
                            if(mView != null) {
                                Intent it = new Intent(context, VideoAdDownService.class);
                                Bundle bundle = new Bundle();
                                bundle.putStringArrayList("data", datas);
                                it.putExtras(bundle);
                                context.startService(it);
                            }
                        }
                    }
                    SharePreferensUtil.putInt(KeyContacts.SP_KEY_BANNER_OR_VIDEO,type ,KeyContacts.SP_NAME_USERINFO);
                    SharePreferensUtil.putLong(KeyContacts.SP_KEY_BANNER_UPDATE_TIME,update_time1,KeyContacts.SP_NAME_USERINFO);
                }

            }



            @Override
            public void onError(Exception e) {
                super.onError(e);
                if (mView != null)
                    mView.getUseridByRoomSuccess(false,e.getMessage(),"");
            }
        });
    }
}
