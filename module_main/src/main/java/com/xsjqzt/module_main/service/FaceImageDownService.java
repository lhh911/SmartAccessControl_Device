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
import com.jbb.library_common.comfig.InterfaceConfig;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.retrofit.RetrofitManager;
import com.jbb.library_common.retrofit.other.BaseBean;
import com.jbb.library_common.retrofit.other.HttpRespStatus;
import com.jbb.library_common.retrofit.other.NetException;
import com.jbb.library_common.retrofit.other.NetListeren;
import com.jbb.library_common.retrofit.other.SubscribeUtils;
import com.jbb.library_common.utils.BitmapUtil;
import com.jbb.library_common.utils.FileUtil;
import com.jbb.library_common.utils.Utils;
import com.jbb.library_common.utils.log.LogUtil;
import com.xsjqzt.module_main.greendao.DbManager;
import com.xsjqzt.module_main.greendao.OpenRecordDao;
import com.xsjqzt.module_main.greendao.entity.FaceImage;
import com.xsjqzt.module_main.greendao.entity.OpenRecord;
import com.xsjqzt.module_main.model.FaceImageResBean;
import com.xsjqzt.module_main.model.user.UserInfoInstance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class FaceImageDownService extends IntentService {

    LinkedList<FaceImageResBean.DataBean> queue = new LinkedList<>();

    public FaceImageDownService() {
        super("FaceImageDownService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        ArrayList<FaceImageResBean.DataBean> dataBeans = bundle.getParcelableArrayList("data");
        if (!queue.containsAll(dataBeans)) {
            queue.addAll(dataBeans);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.w("FaceImageDownService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //三步：下载人脸图片，注册阅面，后将注册状态成功与否传给后台，

        downImage();
    }

    private void downImage() {
        FaceImageResBean.DataBean poll = queue.poll();


        if (poll != null) {
            if (TextUtils.isEmpty(poll.getCode())) {//未注册
                subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                        .downFaceImage(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(),
                                InterfaceConfig.BASEURL+poll.getImage()), poll.getUser_id() ,poll);

            } else {//已注册，插入人脸数据
                insert(poll);
                downImage();
            }
        }
    }


    public void subscribe(Observable<ResponseBody> observable, final int user_id, final FaceImageResBean.DataBean dataBean) {
        observable.flatMap(new Function<ResponseBody, Observable<InputStream>>() {

            @Override
            public Observable apply(ResponseBody response) throws Exception {

                return Observable.just(response.byteStream());

            }
        })
                .subscribe(new Observer<InputStream>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(InputStream inputStream) {
                        writeFile(inputStream, user_id , dataBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        downImage();//继续下一个
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }


    private void writeFile(InputStream inputString, int user_id,FaceImageResBean.DataBean dataBean) {
        //存到本地文件，按日期建文件夹
        String facePath = FileUtil.getAppFacePicturePath(this);
        File file = new File(facePath, new Date().getTime() + ".jpg");
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] b = new byte[1024];

            int len;
            while ((len = inputString.read(b)) != -1) {
                fos.write(b, 0, len);
            }

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } finally {
            try {
                fos.flush();
                inputString.close();
                fos.close();
            } catch (Exception e) {
            }
        }
        registYM(file.getPath(), user_id,dataBean);
    }


    //注册阅面，成功与否上传人脸识别状态
    public void registYM(String facePath, int user_id,FaceImageResBean.DataBean dataBean) {
        //注册阅面
        Bitmap bitmap = BitmapFactory.decodeFile(facePath);
        int personId = 0;
        int status = 0;
        String code = "";//识别码
//        FaceSet faceSet = new FaceSet(getApplication());
//        faceSet.startTrack(0);
//        FaceResult faceResult = faceSet.registByBitmap(bitmap, name);
//        if (faceResult == null) return;
//        if (faceResult.code == 0) {//成功
//            //添加成功，此返回值即为数据库对当前⼈人脸的中唯⼀一标识
//            int personId = faceResult.personId;
//            LogUtil.w("人脸的中唯⼀一标识 personId = " + personId);
            status = 2;
            code = "";
//        }else{//失败
            status = 3;
//        }
        if(status == 2){
            insert(dataBean);
        }
        updateFacesStatus(status, user_id ,code);
        downImage();//继续下一个
    }

    //上传人脸识别状态
    public void updateFacesStatus(int status, int user_id,String code) {
        SubscribeUtils.subscribe3(RetrofitManager.getInstance().getService(ApiService.class)
                .updateFacesStatus(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), status, user_id, code), BaseBean.class, new NetListeren<BaseBean>() {
            @Override
            public void onSuccess(BaseBean info) {

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


    private void insert(FaceImageResBean.DataBean data) {
        FaceImage faceImage = new FaceImage();
        faceImage.setCode(data.getCode());
        faceImage.setImage(data.getImage());
        faceImage.setStatus(data.getStatus());
        faceImage.setUpdate_time(data.getUpdate_time());
        faceImage.setUser_id(data.getUser_id());
        DbManager.getInstance().getDaoSession().getFaceImageDao().insertOrReplaceInTx(faceImage);
    }
}
