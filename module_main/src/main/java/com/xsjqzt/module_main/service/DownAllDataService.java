package com.xsjqzt.module_main.service;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.jbb.library_common.BaseApplication;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.retrofit.RetrofitManager;
import com.jbb.library_common.retrofit.other.BaseBean;
import com.jbb.library_common.retrofit.other.HttpRespStatus;
import com.jbb.library_common.retrofit.other.NetException;
import com.jbb.library_common.retrofit.other.NetListeren;
import com.jbb.library_common.retrofit.other.SubscribeUtils;
import com.jbb.library_common.utils.FileUtil;
import com.jbb.library_common.utils.log.LogUtil;
import com.xsjqzt.module_main.greendao.DbManager;
import com.xsjqzt.module_main.greendao.FaceImageDao;
import com.xsjqzt.module_main.greendao.ICCardDao;
import com.xsjqzt.module_main.greendao.IDCardDao;
import com.xsjqzt.module_main.greendao.entity.FaceImage;
import com.xsjqzt.module_main.greendao.entity.ICCard;
import com.xsjqzt.module_main.greendao.entity.IDCard;
import com.xsjqzt.module_main.greendao.entity.OpenRecord;
import com.xsjqzt.module_main.model.CardResBean;
import com.xsjqzt.module_main.model.FaceImageResBean;
import com.xsjqzt.module_main.model.ICCardResBean;
import com.xsjqzt.module_main.model.IDCardResBean;
import com.xsjqzt.module_main.model.user.UserInfoInstance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class DownAllDataService extends IntentService {


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
        //下载ic卡，id卡，人脸
        downICCard();
        downIDCard();
        downImageFace();

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

                    Intent it = new Intent(DownAllDataService.this,FaceImageDownService.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("data",data);
                    it.putExtras(bundle);
                    startService(it);

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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<CardResBean> data = info.getData();
                        if(data == null ||data.isEmpty())
                            return;
                        List<IDCard> lists = new ArrayList<>();
                        for (CardResBean bean : data) {
                            if(bean.isIs_delete()){
                                IDCard unique = DbManager.getInstance().getDaoSession().getIDCardDao().queryBuilder().where(IDCardDao.Properties.Sn.eq(bean.getSn())).unique();
                                if(unique != null)
                                    DbManager.getInstance().getDaoSession().getIDCardDao().delete(unique);
                            }else{
                                IDCard card = new IDCard();
                                card.setSid(bean.getId());
                                card.setSn(bean.getSn());
                                card.setUser_id(bean.getUser_id());
                                card.setUser_name(bean.getUser_name());
                                lists.add(card);
                            }
                        }
                        if(lists.size() > 0)
                            DbManager.getInstance().getDaoSession().getIDCardDao().insertOrReplaceInTx(lists);
                    }
                }).start();

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

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<CardResBean> data = info.getData();
                        if(data == null ||data.isEmpty())
                            return;
                        List<ICCard> lists = new ArrayList<>();
                        for (CardResBean bean : data) {
                            if(bean.isIs_delete()){
                                ICCard unique = DbManager.getInstance().getDaoSession().getICCardDao().queryBuilder().where(ICCardDao.Properties.Sn.eq(bean.getSn())).unique();
                                if(unique != null)
                                    DbManager.getInstance().getDaoSession().getICCardDao().delete(unique);
                            }else {
                                ICCard card = new ICCard();
                                card.setSid(bean.getId());
                                card.setSn(bean.getSn());
                                card.setUser_id(bean.getUser_id());
                                card.setUser_name(bean.getUser_name());
                                lists.add(card);
                            }
                        }
                        if(lists.size() > 0)
                            DbManager.getInstance().getDaoSession().getICCardDao().insertOrReplaceInTx(lists);
                    }
                }).start();

            }

        });

    }



}
