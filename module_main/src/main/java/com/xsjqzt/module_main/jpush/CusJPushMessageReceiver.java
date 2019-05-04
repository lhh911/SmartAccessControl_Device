package com.xsjqzt.module_main.jpush;

import android.content.Context;

import java.util.Set;

import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.service.JPushMessageReceiver;

public class CusJPushMessageReceiver extends JPushMessageReceiver {

    public CusJPushMessageReceiver() {
        super();
    }

    //tag 增删查改的操作会在此方法中回调结果。
    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onTagOperatorResult(context, jPushMessage);
        int errorCode = jPushMessage.getErrorCode();
        if(errorCode == 0){
            Set<String> tags = jPushMessage.getTags();

        }
    }


    //查询某个 tag 与当前用户的绑定状态的操作会在此方法中回调结果
    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onCheckTagOperatorResult(context, jPushMessage);
    }

    //alias 相关的操作会在此方法中回调结果。
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);
    }

    //设置手机号码会在此方法中回调结果。
    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }
}
