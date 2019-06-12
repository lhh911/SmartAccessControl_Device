package com.xsjqzt.module_main.service;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.jbb.library_common.comfig.InterfaceConfig;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.retrofit.RetrofitManager;
import com.jbb.library_common.retrofit.other.BaseBean;
import com.jbb.library_common.retrofit.other.NetListeren;
import com.jbb.library_common.retrofit.other.SubscribeUtils;
import com.jbb.library_common.utils.DeviceUtil;
import com.jbb.library_common.utils.FileUtil;
import com.jbb.library_common.utils.SharePreferensUtil;
import com.jbb.library_common.utils.log.LogUtil;
import com.xsjqzt.module_main.faceSdk.FaceSet;
import com.xsjqzt.module_main.greendao.DbManager;
import com.xsjqzt.module_main.greendao.FaceImageDao;
import com.xsjqzt.module_main.greendao.entity.FaceImage;
import com.xsjqzt.module_main.model.FaceImageResBean;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.modle.DownVideoSuccessEventBus;
import com.xsjqzt.module_main.modle.FaceResult;
import com.xsjqzt.module_main.util.DataConversionUtil;

import org.greenrobot.eventbus.EventBus;

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

public class VideoAdDownService extends IntentService {

    private List<String> videoPaths = new ArrayList<>();
    LinkedList<String> queue = new LinkedList<>();
    private boolean isStart;

    public VideoAdDownService() {
        super("VideoAdDownService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        ArrayList<String> dataBeans = bundle.getStringArrayList("data");
        if (!queue.containsAll(dataBeans)) {
            queue.addAll(dataBeans);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.w("VideoAdDownService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //三步：下载人脸图片，注册阅面，后将注册状态成功与否传给后台，
        if (isStart)
            return;
        isStart = true;
        downLoad();
    }

    private void downLoad() {

        String url = queue.poll();

        if (!TextUtils.isEmpty(url)) {
            subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                    .downImage(InterfaceConfig.BASEURL + url));
        }else{
            if(videoPaths.size() > 0){
                //有下载数据
                String str = JSON.toJSONString(videoPaths);
                SharePreferensUtil.putString(KeyContacts.SP_KEY_VIDEO_DATA,str,KeyContacts.SP_NAME_USERINFO);
                EventBus.getDefault().post(new DownVideoSuccessEventBus());
            }
        }

    }


    public void subscribe(Observable<ResponseBody> observable) {

        if (!DeviceUtil.isNetWorkEnable()) {
            downLoad();
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
                        writeFile(inputStream);
                    }

                    @Override
                    public void onError(Throwable e) {
                        downLoad();
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }


    private void writeFile(InputStream inputString) {
        boolean downSuccess = true;

        //存到本地文件，按日期建文件夹
        String facePath = FileUtil.getAppVideoPath(this);
        File file = new File(facePath, new Date().getTime() + ".jpg");
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            byte[] b = new byte[1024];

            int len = -1;
            while ((len = inputString.read(b)) != -1) {
                fos.write(b, 0, len);
            }

        } catch (Exception e) {
            downSuccess = false;
        } finally {
            try {
                fos.flush();
                inputString.close();
                fos.close();
            } catch (Exception e) {
            }
        }
        if(downSuccess){
            videoPaths.add(file.getAbsolutePath());
        }
        downLoad();
    }


}
