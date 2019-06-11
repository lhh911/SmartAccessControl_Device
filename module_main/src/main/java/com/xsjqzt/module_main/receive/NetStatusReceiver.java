//package com.xsjqzt.module_main.receive;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//
//import com.jbb.library_common.utils.log.LogUtil;
//import com.xsjqzt.module_main.service.DownAllDataService;
//
//public class NetStatusReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        //获取联网状态的NetworkInfo对象
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//
//        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//        if (mobNetInfo.isConnected() || wifiNetInfo.isConnected()) {
//            //WIFI和移动网络均未连接
//            context.startService(new Intent(context, DownAllDataService.class));
//        }
//        LogUtil.w("网络变化了");
//
//    }
//}
