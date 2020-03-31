package com.xsjqzt.module_main.activity.base;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jbb.library_common.BaseApplication;
import com.jbb.library_common.BuildConfig;
import com.jbb.library_common.basemvp.ActivityManager;
import com.jbb.library_common.comfig.AppConfig;
import com.jbb.library_common.utils.FileUtil;
import com.jbb.library_common.utils.log.LogUtil;
import com.softwinner.Gpio;
import com.tencent.bugly.crashreport.CrashReport;
import com.xsjqzt.module_main.ui.SplashActivity;
import com.xsjqzt.module_main.util.CrashHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

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

        CrashHandler.getInstance().init(getApplicationContext());//异常捕捉，重启应用

        CrashReport.initCrashReport(getApplicationContext(),AppConfig.BUGLY_APPID,false);

//        initBuglyCrash();

    }

    /**
     * 获取全局上下文
     */
    public static Context getContext() {
        return context;
    }


    //设置Crash回调
    private void initBuglyCrash(){
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
            public Map<String, String> onCrashHandleStart(int crashType, String errorType,
                                                          String errorMessage, String errorStack) {

                closeDoor();
                saveCatchInfo2File(errorMessage + "\n" + errorStack);
                reStartApp();


                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                map.put("Key", "Value");
                return map;
            }

            @Override
            public byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType,
                                                           String errorMessage, String errorStack) {
                try {
                    return "Extra data.".getBytes("UTF-8");
                } catch (Exception e) {
                    return null;
                }
            }

        });

        CrashReport.initCrashReport(getApplicationContext(),AppConfig.BUGLY_APPID,true,strategy);
    }


    private void reStartApp(){
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Intent intent = new Intent(mContext, SplashActivity.class);
                PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, restartIntent);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ActivityManager.getInstance().finishAllActivity();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                },1500);
                Looper.loop();
            }
        }.start();
    }

    private void closeDoor(){
        Gpio.setPull('0', 4, 1);
        Gpio.setMulSel('O', 4, 1);//0 做为输入，1做为输出
        Gpio.writeGpio('O', 4, 0);
    }


    private void saveCatchInfo2File(String error) {

        try {
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM-dd-HH-mm-ss");
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + ".log";

            String path = FileUtil.getAppCachePath(mContext) + File.separator + "crash/";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(path + fileName);
            fos.write(error.getBytes());
            fos.flush();
            fos.close();

        } catch (Exception e) {
            Log.e("info","saveCatchInfo2File() an error occured while writing file... Exception:",e);
        }
    }



//    //解决方法数超过64k问题
//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }
}
