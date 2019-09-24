package com.xsjqzt.module_main.activity.base;

import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jbb.library_common.BaseApplication;
import com.jbb.library_common.BuildConfig;
import com.jbb.library_common.comfig.AppConfig;
import com.jbb.library_common.utils.log.LogUtil;
//import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.bugly.crashreport.CrashReport;
import com.xsjqzt.module_main.util.CrashHandler;

import cn.jpush.android.api.JPushInterface;

public class ExApplication extends BaseApplication {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        LogUtil.init().writeToFile(false).setDebug(AppConfig.LOG_SWITCH_FLAG == LogUtil.LOG_ON);

        if (BuildConfig.DEBUG) {
            // These two lines must be written before init, otherwise these configurations will be invalid in the init process
            ARouter.openLog();     // Print log
            ARouter.openDebug();   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        }
        ARouter.init(this);

        JPushInterface.init(getApplicationContext());

        CrashReport.initCrashReport(getApplicationContext(),AppConfig.BUGLY_APPID,false);

        CrashHandler.getInstance().init(getApplicationContext());//异常捕捉，重启应用
    }

    /**
     * 获取全局上下文
     */
    public static Context getContext() {
        return context;
    }


//    //解决方法数超过64k问题
//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }
}
