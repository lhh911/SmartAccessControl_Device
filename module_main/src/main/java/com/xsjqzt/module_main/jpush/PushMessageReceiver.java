package com.xsjqzt.module_main.jpush;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.SharePreferensUtil;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.presenter.RegistrationIdPresenter;
import com.xsjqzt.module_main.util.ThreadPoolManager;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PushMessageReceiver extends JPushMessageReceiver {
    private static final String TAG = "PushMessageReceiver";
    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        Log.e(TAG,"[onMessage] "+customMessage);
//        processCustomMessage(context,customMessage);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        Log.e(TAG,"[onNotifyMessageOpened] "+message);
        try{
            //打开自定义的Activity
//            Intent i = new Intent(context, TestActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE,message.notificationTitle);
//            bundle.putString(JPushInterface.EXTRA_ALERT,message.notificationContent);
//            i.putExtras(bundle);
//            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//            context.startActivity(i);
        }catch (Throwable throwable){

        }
    }

    @Override
    public void onMultiActionClicked(Context context, Intent intent) {
        Log.e(TAG, "[onMultiActionClicked] 用户点击了通知栏按钮");
        String nActionExtra = intent.getExtras().getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA);

        //开发者根据不同 Action 携带的 extra 字段来分配不同的动作。
        if(nActionExtra==null){
            Log.d(TAG,"ACTION_NOTIFICATION_CLICK_ACTION nActionExtra is null");
            return;
        }
        if (nActionExtra.equals("my_extra1")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮一");
        } else if (nActionExtra.equals("my_extra2")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮二");
        } else if (nActionExtra.equals("my_extra3")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮三");
        } else {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮未定义");
        }
    }

    @Override
    public void onNotifyMessageArrived(final Context context, final NotificationMessage message) {
        Log.e(TAG,"[onNotifyMessageArrived] "+message);

//        Observable.just(1)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe(new Consumer<Integer>() {
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        Bundle bundle = new Bundle();
//                        bundle.putString(JPushInterface.EXTRA_EXTRA,message.notificationExtras);
//                        Intent it = new Intent();
//                        it.setAction(KeyContacts.ACTION_RECEICE_NOTITY);
//                        it.putExtras(bundle);
//                        context.sendBroadcast(it);
//
//                        int notificationId = message.notificationId;
//                        JPushInterface.clearNotificationById(context, notificationId);
//                    }
//                });

        ThreadPoolManager.getInstance().execute(new MyRunnable(message,context));

    }


    public class MyRunnable implements Runnable{
        NotificationMessage message;
        Context context;

        public MyRunnable(NotificationMessage message,Context context) {
            this.message = message;
            this.context = context;
        }

        @Override
        public void run() {
            Bundle bundle = new Bundle();
            bundle.putString(JPushInterface.EXTRA_EXTRA,message.notificationExtras);
            Intent it = new Intent();
            it.setAction(KeyContacts.ACTION_RECEICE_NOTITY);
            it.putExtras(bundle);
            context.sendBroadcast(it);

            int notificationId = message.notificationId;
            JPushInterface.clearNotificationById(context, notificationId);
        }
    }


    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage message) {
        Log.e(TAG,"[onNotifyMessageDismiss] "+message);
    }

    @Override
    public void onRegister(Context context, String registrationId) {
        Log.e(TAG,"[onRegister] "+registrationId);

        if(UserInfoInstance.getInstance().hasLogin()) {
            RegistrationIdPresenter presenter = new RegistrationIdPresenter();
            presenter.registrationId(registrationId);
        }
    }

    @Override
    public void onConnected(Context context, boolean isConnected) {
        Log.e(TAG,"[onConnected] "+isConnected);
        if(!isConnected)//断开了
            JPushInterface.onResume(context.getApplicationContext());
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        Log.e(TAG,"[onCommandResult] "+cmdMessage);

    }

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
//        TagAliasOperatorHelper.getInstance().onTagOperatorResult(context,jPushMessage);
        super.onTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage){
//        TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context,jPushMessage);
        super.onCheckTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
//        TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context,jPushMessage);
        super.onAliasOperatorResult(context, jPushMessage);

        int errorCode = jPushMessage.getErrorCode();
        if(errorCode == 0){
            String alias = jPushMessage.getAlias();
            SharePreferensUtil.putBoolean(KeyContacts.SP_KEY_JPUSH_ALIAS,true ,KeyContacts.SP_NAME_JPUSH);
        }
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
//        TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context,jPushMessage);
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }

    //send msg to MainActivity
//    private void processCustomMessage(Context context, CustomMessage customMessage) {
//        if (MainActivity.isForeground) {
//            String message = customMessage.message;
//            String extras = customMessage.extra;
//            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//            if (!ExampleUtil.isEmpty(extras)) {
//                try {
//                    JSONObject extraJson = new JSONObject(extras);
//                    if (extraJson.length() > 0) {
//                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//                    }
//                } catch (JSONException e) {
//
//                }
//
//            }
//            LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
//        }
//    }

    @Override
    public void onNotificationSettingsCheck(Context context, boolean isOn, int source) {
        super.onNotificationSettingsCheck(context, isOn, source);
        Log.e(TAG,"[onNotificationSettingsCheck] isOn:"+isOn+",source:"+source);
    }
    
}
