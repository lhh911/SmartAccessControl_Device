package com.xsjqzt.module_main.service;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.jbb.library_common.comfig.InterfaceConfig;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.retrofit.RetrofitManager;
import com.jbb.library_common.retrofit.other.BaseBean;
import com.jbb.library_common.retrofit.other.HttpRespStatus;
import com.jbb.library_common.retrofit.other.NetException;
import com.jbb.library_common.retrofit.other.NetListeren;
import com.jbb.library_common.retrofit.other.SubscribeUtils;
import com.jbb.library_common.utils.DeviceUtil;
import com.jbb.library_common.utils.FileUtil;
import com.jbb.library_common.utils.Utils;
import com.jbb.library_common.utils.log.LogUtil;
import com.xsjqzt.module_main.faceSdk.FaceSet;
import com.xsjqzt.module_main.greendao.DbManager;
import com.xsjqzt.module_main.greendao.FaceImageDao;
import com.xsjqzt.module_main.greendao.entity.FaceImage;
import com.xsjqzt.module_main.model.FaceImageResBean;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.modle.FaceResult;
import com.xsjqzt.module_main.util.DataConversionUtil;

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
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

public class FaceImageDownService extends IntentService {

    LinkedList<FaceImageResBean.DataBean> queue = new LinkedList<>();
    LinkedList<FaceImageResBean.DataBean> reQueue = new LinkedList<>();//对操作识别的数据收集，做重试一次
    List<FaceImage> deleteList = new ArrayList<>();
    private boolean isStart = false;
    private FaceSet faceSet;
    private boolean isReExecute;

    public FaceImageDownService() {
        super("FaceImageDownService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        faceSet = new FaceSet(getApplication());
        faceSet.startTrack(0);
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
        if (isStart)
            return;
        isStart = true;
        downImage();
    }

    private void downImage() {
        isReExecute = false;

        FaceImageResBean.DataBean poll = queue.poll();

        if (poll != null) {
            if(TextUtils.isEmpty(poll.getImage()) ) {
                downImage();
                return;
            }
            if(poll.isIs_delete() ) {
                FaceImage unique = DbManager.getInstance().getDaoSession().getFaceImageDao().queryBuilder().where(FaceImageDao.Properties.User_id.eq(poll.getUser_id())).unique();
                if(unique != null){
                    DbManager.getInstance().getDaoSession().getFaceImageDao().delete(unique);
                    boolean delete = faceSet.deleteUserByPersonId(unique.getPersonId());//删除阅面数据库数据
                    if(!delete){
                        deleteList.add(unique);
                    }
                }

                downImage();
                return;
            }
            subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                    .downFaceImage(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(),
                            InterfaceConfig.BASEURL + poll.getImage()), poll.getUser_id(), poll);


//            if(poll.isIs_delete()){
//                FaceImage unique = DbManager.getInstance().getDaoSession().getFaceImageDao().queryBuilder()
//                        .where(FaceImageDao.Properties.User_id.eq(poll.getUser_id())).unique();
//                if(unique != null){
//                    DbManager.getInstance().getDaoSession().getFaceImageDao().delete(unique);
//                }
//                downImage();
//            }else{
//                if (poll.getStatus() == 1) {//未注册
//                    if(TextUtils.isEmpty(poll.getImage())) {
//                        downImage();
//                        return;
//                    }
//                    subscribe(RetrofitManager.getInstance().getService(ApiService.class)
//                            .downFaceImage(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(),
//                                    InterfaceConfig.BASEURL + poll.getImage()), poll.getUser_id(), poll);
//
//                } else {//已注册，插入人脸数据
//                    insert(poll);
//                    downImage();
//                }
//            }
        }else{
            reExecute();
            delectData();
        }
    }

    //删除本地人脸数据和阅面人脸库
    private void delectData() {
        for(FaceImage unique : deleteList){
            if (unique != null) {
                DbManager.getInstance().getDaoSession().getFaceImageDao().delete(unique);
                boolean delete = faceSet.deleteUserByPersonId(unique.getPersonId());//删除阅面数据库数据
            }
        }
    }

    //再次执行一次失败了的数据操作，不包含删除操作数据
    private void reExecute() {
        isReExecute = true;

        FaceImageResBean.DataBean poll = reQueue.poll();

        if (poll != null) {
            if (TextUtils.isEmpty(poll.getImage())) {
                reExecute();
                return;
            }

            subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                    .downFaceImage(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(),
                            InterfaceConfig.BASEURL + poll.getImage()), poll.getUser_id(), poll);
        }
    }


    public void subscribe(Observable<ResponseBody> observable, final int user_id, final FaceImageResBean.DataBean dataBean) {
//        SubscribeUtils.subscribe4(observable, InputStream.class, new NetListeren<InputStream>() {
//            @Override
//            public void onSuccess(InputStream inputStream) {
//                writeFile(inputStream, user_id, dataBean);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                downImage();//继续下一个
//            }
//        });
        if (!DeviceUtil.isNetWorkEnable()) {
            if(isReExecute)
                reExecute();
            else {
                reQueue.add(dataBean);
                downImage();
            }
            return;
        }

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
                        writeFile(inputStream, user_id, dataBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(isReExecute)
                            reExecute();
                        else {
                            reQueue.add(dataBean);
                            downImage();//继续下一个
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }


    private void writeFile(InputStream inputString, int user_id, FaceImageResBean.DataBean dataBean) {
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
        registYM(file.getPath(), user_id, dataBean);
    }


    //注册阅面，成功与否上传人脸识别状态
    public void registYM(String facePath, int user_id, FaceImageResBean.DataBean dataBean) {
        //注册阅面
        Bitmap bitmap = BitmapFactory.decodeFile(facePath);

        int status = 3;
        String code = "";//识别码
        FaceResult faceResult = null;
        //注册 10次，保证注册成功率
        for(int i = 0;i< 10;i++) {
            faceResult = faceSet.registByBitmap(bitmap, user_id + "");
            if (faceResult == null)
                continue;
            if (faceResult.code == 0 ) {//成功
                //添加成功，此返回值即为数据库对当前⼈人脸的中唯⼀一标识
                code = DataConversionUtil.floatToString(faceResult.rect);
                LogUtil.w("人脸的中唯⼀一标识 personId = " + code);
                status = 2;
               //插入本地数据
                dataBean.setCodeX(code);
                insert(dataBean, faceResult.personId);

                break;
            } else {//失败
                status = 3;
//                if (!isReExecute)//添加失败数据到重试集合
//                    reQueue.add(dataBean);
            }
        }

        updateFacesStatus(status, user_id ,code);
        downImage();//继续下一个
    }




    //上传人脸识别状态
    public void updateFacesStatus(int status, int user_id, String code) {
        SubscribeUtils.subscribe3(RetrofitManager.getInstance().getService(ApiService.class)
                .updateFacesStatus(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), status, user_id, code), BaseBean.class, new NetListeren<BaseBean>() {
            @Override
            public void onSuccess(BaseBean info) {

            }
            @Override
            public void onError(Exception e) {
//                super.onError(e);
            }
        });
    }


    private void insert(FaceImageResBean.DataBean data,int personId) {
//        if(data.isIs_delete()){
//            FaceImage unique = DbManager.getInstance().getDaoSession().getFaceImageDao().queryBuilder().where(FaceImageDao.Properties.User_id.eq(data.getUser_id())).unique();
//            if(unique != null){
//                DbManager.getInstance().getDaoSession().getFaceImageDao().delete(unique);
//            }
//        }else{
//            if(data.getStatus() == 2) {
                FaceImage faceImage = new FaceImage();
                faceImage.setCode(data.getCodeX());
                faceImage.setImage(data.getImage());
                faceImage.setStatus(data.getStatus());
                faceImage.setUpdate_time(data.getUpdate_time());
                faceImage.setUser_id(data.getUser_id());
                faceImage.setPersonId(personId);
                DbManager.getInstance().getDaoSession().getFaceImageDao().insertOrReplaceInTx(faceImage);
//            }
//        }

    }
}
