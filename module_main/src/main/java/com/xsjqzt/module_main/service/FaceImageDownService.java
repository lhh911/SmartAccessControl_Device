package com.xsjqzt.module_main.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.jbb.library_common.BaseApplication;
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
            subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                    .downFaceImage(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(),poll.getImage()) ,poll.getId() );
        }
    }


    public void subscribe(Observable<ResponseBody> observable ,final int id) {
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
                writeFile(inputStream ,id);
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



    private void writeFile(InputStream inputString ,int id) {
        //存到本地文件，按日期建文件夹
        String facePath = FileUtil.getAppFacePicturePath(this);
        File file = new File(facePath, new Date().getTime()+".jpg");
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] b = new byte[1024];

            int len;
            while ((len = inputString.read(b)) != -1) {
                fos.write(b,0,len);
            }

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }finally {
            try {
                fos.flush();
                inputString.close();
                fos.close();
            }catch (Exception e){}
        }
        registYM(facePath ,id);
    }



    //注册阅面，成功与否上传人脸识别状态
    public void registYM(String facePath ,int id){
        //注册阅面

        updateFacesStatus(2 ,id);
        downImage();//继续下一个
    }

    //上传人脸识别状态
    public void updateFacesStatus(int status ,int id){
        SubscribeUtils.subscribe3(RetrofitManager.getInstance().getService(ApiService.class)
                .updateFacesStatus(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), status,id), BaseBean.class, new NetListeren<BaseBean>() {
            @Override
            public void onSuccess( BaseBean info) {

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
