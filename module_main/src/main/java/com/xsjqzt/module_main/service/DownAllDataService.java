package com.xsjqzt.module_main.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.retrofit.RetrofitManager;
import com.jbb.library_common.retrofit.other.NetListeren;
import com.jbb.library_common.retrofit.other.SubscribeUtils;
import com.jbb.library_common.utils.log.LogUtil;
import com.xsjqzt.module_main.greendao.DbManager;
import com.xsjqzt.module_main.greendao.FaceImageDao;
import com.xsjqzt.module_main.greendao.ICCardDao;
import com.xsjqzt.module_main.greendao.IDCardDao;
import com.xsjqzt.module_main.greendao.OpenCodeDao;
import com.xsjqzt.module_main.greendao.entity.FaceImage;
import com.xsjqzt.module_main.greendao.entity.ICCard;
import com.xsjqzt.module_main.greendao.entity.IDCard;
import com.xsjqzt.module_main.greendao.entity.OpenCode;
import com.xsjqzt.module_main.model.CardResBean;
import com.xsjqzt.module_main.model.FaceImageResBean;
import com.xsjqzt.module_main.model.ICCardResBean;
import com.xsjqzt.module_main.model.IDCardResBean;
import com.xsjqzt.module_main.model.PswCodeResBean;
import com.xsjqzt.module_main.model.user.UserInfoInstance;

import java.util.ArrayList;
import java.util.List;

public class DownAllDataService extends IntentService {
    private boolean isStart = false;

    public DownAllDataService() {
        super("DownAllDataService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.w("FaceImageDownService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(isStart)
            return;
        isStart = true;
        //下载ic卡，id卡，人脸
        downICCard();
        downIDCard();
        downImageFace();
        downOpenCode();
    }

    private void downOpenCode() {
        OpenCodeDao openCodeDao = DbManager.getInstance().getDaoSession().getOpenCodeDao();
        OpenCode unique = openCodeDao.queryBuilder().limit(1).orderDesc(OpenCodeDao.Properties.Update_time).unique();
        int update_time = 0;
        if (unique != null) {
            update_time = unique.getUpdate_time();
        }

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

    private void downImageFace() {
        FaceImageDao faceImageDao = DbManager.getInstance().getDaoSession().getFaceImageDao();
        FaceImage faceImage = faceImageDao.queryBuilder().limit(1).orderDesc(FaceImageDao.Properties.Update_time).unique();
        int update_time = 0;
        if (faceImage != null) {
            update_time = faceImage.getUpdate_time();
        }

        SubscribeUtils.subscribe3(RetrofitManager.getInstance().getService(ApiService.class)
                .loadFaceImage(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), update_time), FaceImageResBean.class, new NetListeren<FaceImageResBean>() {
            @Override
            public void onSuccess(final FaceImageResBean info) {

                ArrayList<FaceImageResBean.DataBean> data = info.getData();
                if(data == null || data.isEmpty())
                    return;
                Intent it = new Intent(DownAllDataService.this, FaceImageDownService.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("data", data);
                it.putExtras(bundle);
                startService(it);

            }
            @Override
            public void onError(Exception e) {
//                super.onError(e);
            }
        });

    }

    private void downIDCard() {
        IDCardDao idCardDao = DbManager.getInstance().getDaoSession().getIDCardDao();
        IDCard idcard = idCardDao.queryBuilder().limit(1).orderDesc(IDCardDao.Properties.Update_time).unique();
        int update_time = 0;
        if (idcard != null) {
            update_time = idcard.getUpdate_time();
        }

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

    private void downICCard() {

        ICCardDao icCardDao = DbManager.getInstance().getDaoSession().getICCardDao();
        ICCard iccard = icCardDao.queryBuilder().limit(1).orderDesc(ICCardDao.Properties.Update_time).unique();
        int update_time = 0;
        if (iccard != null) {
            update_time = iccard.getUpdate_time();
        }

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


}
