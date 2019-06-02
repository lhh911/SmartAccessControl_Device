package com.xsjqzt.module_main.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.ToastUtil;
import com.jbb.library_common.utils.log.LogUtil;
import com.xsjqzt.module_main.service.DownAllDataService;
import com.xsjqzt.module_main.service.OpenRecordService;
import com.xsjqzt.module_main.ui.SplashActivity;

public class AppStartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//            ToastUtil.showCustomToast("22全掌通自动启动了");
            LogUtil.w("AppStartReceiver =  22全掌通自动启动了");
            Intent i = new Intent(context, SplashActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
