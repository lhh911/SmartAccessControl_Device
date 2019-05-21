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
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

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

    PriorityQueue<OpenRecord> queue = new PriorityQueue<>();

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
                .where(OpenRecordDao.Properties.UploadStatus.eq(true))
                .list();

        if (!records.isEmpty()) {
            LogUtil.w("OpenRecordService  records.size = " + records.size());

            queue.addAll(records);
            uploadRecord();
        }


    }

    private void uploadRecord() {
        OpenRecord record = queue.poll();

        if (record != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("sn", record.getSn());
            params.put("status", record.getStatus());
            params.put("createTime", record.getCreateTime());
            File file = new File("");
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);

            if (record.getICOrID() == 1) {//ic卡记录
                subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                        .uploadICCardRecord(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), params, body), record);
            }else{  //身份证记录
                subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                        .uploadIDCardRecord(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), params, body), record);
            }
        }
    }


    public void subscribe(Observable<ResponseBody> observable, final OpenRecord record) {

        observable.flatMap(new Function<ResponseBody, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(ResponseBody response) throws Exception {
                BaseBean baseBean = null;
                String result = response.string();
                baseBean = JSON.parseObject(result, BaseBean.class);

                if(baseBean.getCode() == 0){
                    //修改记录
                    record.setUploadStatus(true);
                    DbManager.getInstance().getDaoSession().getOpenRecordDao().update(record);

                    //删除文件
                    FileUtil.deleteFilesByDirectory(new File(record.getImage()));
                }
                //单线程不断取出queue中的记录，上传并修改状态
                uploadRecord();
                return null;
            }
        })
        .subscribe();
    }


}
