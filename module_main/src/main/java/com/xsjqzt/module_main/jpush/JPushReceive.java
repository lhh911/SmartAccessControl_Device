package com.xsjqzt.module_main.jpush;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.FileUtil;
import com.jbb.library_common.utils.ToastUtil;
import com.jbb.library_common.utils.log.LogUtil;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.presenter.RegistrationIdPresenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.jpush.android.api.JPushInterface;

public class JPushReceive extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";

    private NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();
//        Logger.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + AndroidUtil.printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String registrationId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "JPush 用户注册成功 , registrationId = " + registrationId);
            ToastUtil.showCustomToast("设备接入成功");

            if(UserInfoInstance.getInstance().hasLogin()) {
                RegistrationIdPresenter presenter = new RegistrationIdPresenter();
                presenter.registrationId(registrationId);
            }


        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//            LogUtil.d(TAG, "接受到推送下来的自定义消息");

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//            LogUtil.d(TAG, "接受到推送下来的通知");

            receivingNotification(context,bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
//            LogUtil.d(TAG, "用户点击打开了通知");

            openNotification(context,bundle);

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            //极光服务的连接状态改变
            boolean connected = bundle.getBoolean(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            if(!connected){//断开了
                JPushInterface.onResume(context.getApplicationContext());
            }
        }
    }

    private void receivingNotification(Context context, Bundle bundle){
//        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
//        LogUtil.d(TAG, " title : " + title);
//        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
//        LogUtil.d(TAG, "message : " + message);

        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);//自定义的参数json
        LogUtil.d(TAG, "extras : " + extras);

        whiteLog(context,extras.getBytes());

        Intent it = new Intent();
        it.setAction(KeyContacts.ACTION_RECEICE_NOTITY);
        it.putExtras(bundle);
        context.sendBroadcast(it);

        int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
        JPushInterface.clearNotificationById(context, notificationId);
    }

    private void openNotification(Context context, Bundle bundle){
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);//自定义的参数json

//        Intent mIntent = new Intent(context, WebViewActivity.class);
//        mIntent.putExtras(bundle);
//        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(mIntent);

    }

    private void whiteLog(Context context ,byte[] bytes){
        String path = FileUtil.getAppLogPath(context);
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String fileName = format.format(Calendar.getInstance().getTimeInMillis());
        File file = new File(path, fileName + ".txt");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes,0,bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                fos.flush();
                fos.close();
            }catch (Exception e){

            }
        }
    }

}
