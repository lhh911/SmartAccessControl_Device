package com.xsjqzt.module_main.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
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
import com.jbb.library_common.utils.DeviceUtil;
import com.jbb.library_common.utils.FileUtil;
import com.jbb.library_common.utils.log.LogUtil;
import com.xsjqzt.module_main.greendao.DbManager;
import com.xsjqzt.module_main.greendao.OpenRecordDao;
import com.xsjqzt.module_main.greendao.entity.ICCard;
import com.xsjqzt.module_main.greendao.entity.OpenRecord;
import com.xsjqzt.module_main.model.ICCardResBean;
import com.xsjqzt.module_main.model.user.UserInfoInstance;

import org.greenrobot.greendao.query.WhereCondition;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class OpenRecordService extends IntentService {

    LinkedList<OpenRecord> queue = new LinkedList<>();

    public OpenRecordService() {
        super("OpenRecordService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.w("OpenRecordService销毁了");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        queryAllRecord();
    }

    private void queryAllRecord() {
        LogUtil.w("OpenRecordService启动了");

        List<OpenRecord> records = DbManager.getInstance().getDaoSession().getOpenRecordDao()
                .queryBuilder()
                .where(OpenRecordDao.Properties.UploadStatus.eq(false))
                .list();

        if (!records.isEmpty()) {
            LogUtil.w("OpenRecordService  records.size = " + records.size());

            queue.addAll(records);
            uploadRecord();
        }


    }

    private void uploadRecord() {
        OpenRecord record = queue.poll();
        LogUtil.w("OpenRecordService = " + record.getImage() + "，" + record.getSid());

        if (record != null) {
            Map<String, RequestBody> params = new HashMap<>();
            params.put("id", convertToRequestBody(record.getSid() + ""));
//            params.put("type", convertToRequestBody(record.getType()));

            File file = new File(record.getImage());
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);


            subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                    .uploadCardRecordByImage(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), params, body), record);

        }
    }


    public void subscribe(Observable<ResponseBody> observable, final OpenRecord record) {
        observable.flatMap(new Function<ResponseBody, Observable<BaseBean>>() {

            @Override
            public Observable apply(ResponseBody response) throws Exception {
                BaseBean baseBean = null;
                String result = response.string();
                baseBean = JSON.parseObject(result, BaseBean.class);
                if (baseBean == null) {
                    return Observable.error(new Throwable(HttpRespStatus.MSG_UNKNOWN_ERROR));
                }

                if (baseBean.getCode() == 0) {
                    return Observable.just(baseBean);
                } else {
                    if (baseBean.getCode() == 2001 || baseBean.getCode() == 2002) {//2001 token 过期， 2002 Refresh Token 过期
                        Intent it = new Intent(KeyContacts.ACTION_API_KEY_INVALID);
                        it.putExtra("code", baseBean.getCode());
                        BaseApplication.getContext().sendBroadcast(it);
                    }
                    return Observable.error(new NetException(baseBean.getCode(), baseBean.getMessage()));
                }
            }
        })
        .subscribe(new Observer<BaseBean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(BaseBean o) {
                record.setUploadStatus(true);
                DbManager.getInstance().getDaoSession().getOpenRecordDao().update(record);
                //删除文件
                FileUtil.deleteFilesByDirectory(new File(record.getImage()));
                uploadRecord();
            }

            @Override
            public void onError(Throwable e) {
                uploadRecord();
            }

            @Override
            public void onComplete() {
            }
        });


    }


    private RequestBody convertToRequestBody(String param){
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), param);
        return requestBody;
    }

}
