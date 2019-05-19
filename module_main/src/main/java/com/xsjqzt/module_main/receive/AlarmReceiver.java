package com.xsjqzt.module_main.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.log.LogUtil;
import com.xsjqzt.module_main.service.OpenRecordService;
import com.xsjqzt.module_main.ui.SplashActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() == KeyContacts.ACTION_TIMER_UPLOAD_OPENRECORD){
            context.startService(new Intent(context,OpenRecordService.class));
            LogUtil.w("OpenRecordService AlarmReceiver");
        }
    }
}
