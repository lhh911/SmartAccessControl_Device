package com.xsjqzt.app;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jbb.library_common.BaseApplication;
import com.jbb.library_common.comfig.AppConfig;
import com.jbb.library_common.utils.DeviceUtil;
import com.jbb.library_common.utils.ToastUtil;
import com.jbb.library_common.utils.log.LogUtil;

import cn.jpush.android.api.JPushInterface;

public class MyAppcation extends BaseApplication {



    @Override
    public void onCreate() {
        super.onCreate();

        LogUtil.init().writeToFile(false).setDebug(AppConfig.LOG_SWITCH_FLAG == LogUtil.LOG_ON);

        if (BuildConfig.DEBUG) {
            // These two lines must be written before init, otherwise these configurations will be invalid in the init process
            ARouter.openLog();     // Print log
            ARouter.openDebug();   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        }
        ARouter.init(this);

        JPushInterface.init(getApplicationContext());


    }



    //解决方法数超过64k问题
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
