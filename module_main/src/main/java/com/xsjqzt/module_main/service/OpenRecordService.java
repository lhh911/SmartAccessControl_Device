package com.xsjqzt.module_main.service;

import android.app.IntentService;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.jbb.library_common.BaseApplication;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.other.OkHttpClineUtils;
import com.jbb.library_common.retrofit.RetrofitManager;
import com.jbb.library_common.retrofit.other.BaseBean;
import com.jbb.library_common.retrofit.other.HttpRespStatus;
import com.jbb.library_common.retrofit.other.NetException;
import com.jbb.library_common.retrofit.other.NetListeren;
import com.jbb.library_common.retrofit.other.SubscribeUtils;
import com.jbb.library_common.utils.FileUtil;
import com.jbb.library_common.utils.log.LogUtil;
import com.xsjqzt.module_main.greendao.DbManager;
import com.xsjqzt.module_main.greendao.OpenRecordDao;
import com.xsjqzt.module_main.greendao.entity.OpenRecord;
import com.xsjqzt.module_main.model.user.UserInfoInstance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OpenRecordService extends IntentService {

    LinkedList<OpenRecord> queue = new LinkedList<>();
    private boolean isStart = false;

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
        if (isStart)
            return;
        isStart = true;
        queryAllRecord();
    }

    private void queryAllRecord() {
        LogUtil.w("OpenRecordService启动了");

        List<OpenRecord> records = DbManager.getInstance().getDaoSession().getOpenRecordDao()
                .queryBuilder()
                .where(OpenRecordDao.Properties.UploadStatus.eq(false))
                .list();

        LogUtil.w("OpenRecordService  records.size = " + records.size());
        if (!records.isEmpty()) {

            queue.addAll(records);

            uploadRecord();

        }


    }


    private void uploadRecord() {
        final OpenRecord record = queue.poll();
        LogUtil.w("OpenRecordService = " + record.getImage() + "，" + record.getSid());

        if (record != null) {

//            Map<String, RequestBody> params = new HashMap<>();
//            params.put("id", convertToRequestBody(record.getSid() + ""));
////            params.put("type", convertToRequestBody(record.getType()));
//            File file = new File(record.getImage());
//            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);


            //1.创建MultipartBody.Builder对象
            File file = new File(record.getImage());
//            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);

            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);//表单类型
            builder.addFormDataPart("id", record.getSid() + "");
            builder.addFormDataPart("image", file.getName(), requestFile);

            List<MultipartBody.Part> parts = builder.build().parts();


            subscribe(RetrofitManager.getInstance().getService(ApiService.class).uploadCardRecordByImage(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), parts), record);
//            subscribe(RetrofitManager.getInstance().getService(ApiService.class).uploadCardRecordByImage(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), params, body), record);



        }
    }


    public void subscribe(Observable<ResponseBody> observable, final OpenRecord record) {
        SubscribeUtils.subscribe4(observable, BaseBean.class, new NetListeren<BaseBean>() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                record.setUploadStatus(true);
                DbManager.getInstance().getDaoSession().getOpenRecordDao().delete(record);
                //删除文件
                FileUtil.deleteFilesByDirectory(new File(record.getImage()));
                LogUtil.w("上传成功：" + record.getImage());
                uploadRecord();
            }

            @Override
            public void onError(Exception e) {
                uploadRecord();
            }
        });



//        observable.flatMap(new Function<ResponseBody, Observable<BaseBean>>() {
//
//            @Override
//            public Observable apply(ResponseBody response) throws Exception {
//                BaseBean baseBean = null;
//                String result = response.string();
//                baseBean = JSON.parseObject(result, BaseBean.class);
//                if (baseBean == null) {
//                    return Observable.error(new Throwable(HttpRespStatus.MSG_UNKNOWN_ERROR));
//                }
//
//                if (baseBean.getCode() == 0) {
//                    return Observable.just(baseBean);
//                } else {
//                    if (baseBean.getCode() == 2001 || baseBean.getCode() == 2002) {//2001 token 过期， 2002 Refresh Token 过期
//                        Intent it = new Intent(KeyContacts.ACTION_API_KEY_INVALID);
//                        it.putExtra("code", baseBean.getCode());
//                        BaseApplication.getContext().sendBroadcast(it);
//                    }
//                    return Observable.error(new NetException(baseBean.getCode(), baseBean.getMessage()));
//                }
//            }
//        })
//                .subscribe(new Observer<BaseBean>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                    }
//
//                    @Override
//                    public void onNext(BaseBean o) {
//                        record.setUploadStatus(true);
//                        DbManager.getInstance().getDaoSession().getOpenRecordDao().delete(record);
//                        //删除文件
//                        FileUtil.deleteFilesByDirectory(new File(record.getImage()));
//                        LogUtil.w("上传成功：" + record.getImage());
//                        uploadRecord();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        uploadRecord();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                    }
//                });


    }


//    private RequestBody convertToRequestBody(String param) {
//        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), param);
//        return requestBody;
//    }


//    public static void uploadForOctetstream(final String urlStr, final String filePath, final NetListeren listener) {
//
//
//        HttpURLConnection conn = null;
//        try {
//            URL url = new URL(urlStr);
//
//            final String httpMethod = "POST";
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//            conn.setUseCaches(false);
//            conn.setConnectTimeout(10000);
//            conn.setReadTimeout(30000);
//            conn.setRequestMethod(httpMethod);
//            conn.setRequestProperty("Content-Type", "application/octet-stream");
//            conn.setRequestProperty("API_KEY", UserInfoInstance.getInstance().getKey());
//
//
//            FileInputStream inputStream = new FileInputStream(filePath);
//            long length = inputStream.getChannel().size();
//
////                    conn.setChunkedStreamingMode(MAX_BUFFER_SIZE);//未知输出流长度，达到最大缓存就直接发送
//            conn.connect();
//
//            OutputStream sendStream = null;
//            try {
//                sendStream = conn.getOutputStream();
//
//                int count = 0;
//                byte[] buffer = new byte[8 * 1024]; // 8k
//                while ((count = inputStream.read(buffer)) != -1) {
//                    sendStream.write(buffer, 0, count);
//                }
//
//                sendStream.flush();
//            } finally {
//                inputStream.close();
//                sendStream.close();
//
//            }
//
//            String responseString;
//            InputStream inStream = null;
//            try {
//                inStream = conn.getInputStream();
//
//                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                byte[] buffer = new byte[1024];
//                int bytesRead = 0;
//                // write bytes to file
//                while ((bytesRead = inStream.read(buffer)) != -1) {
//                    out.write(buffer, 0, bytesRead);
//                }
//                responseString = out.toString("UTF-8");
//            } finally {
//                inStream.close();
//            }
//
//
//        } catch (Exception e) {
//
//        } finally {
//
//        }
//    }



//    private void uploadMultiFile(String imgUrl,String url,String id,OpenRecord record) {
//        String imageType = "multipart/form-data";
//        File file = new File(imgUrl);//imgUrl为图片位置
//        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), file);
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("file", file.getName(), fileBody)
//                .addFormDataPart("id", id)
//                .addFormDataPart("imagetype", imageType)
//                .build();
//        Request request = new Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .build();
//
//
//        OkHttpClient okHttpClient =  OkHttpClineUtils.getHttpClient();
//        try {
//            Response response = okHttpClient.newCall(request).execute();
//            if(response.code() == 200){
//                String result = response.body().toString();
//                BaseBean baseBean = JSON.parseObject(result, BaseBean.class);
//                if(baseBean.getCode() == 0){
//                    record.setUploadStatus(true);
//                    DbManager.getInstance().getDaoSession().getOpenRecordDao().delete(record);
//                    //删除文件
//                    FileUtil.deleteFilesByDirectory(new File(record.getImage()));
//                    LogUtil.w("上传成功：" + record.getImage());
//                    uploadRecord();
//                }else{
//                    uploadRecord();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            uploadRecord();
//        }
//    }

}
