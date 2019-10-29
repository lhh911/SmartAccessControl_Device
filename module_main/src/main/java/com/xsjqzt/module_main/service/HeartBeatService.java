package com.xsjqzt.module_main.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.retrofit.RetrofitManager;
import com.jbb.library_common.retrofit.other.BaseBean;
import com.jbb.library_common.retrofit.other.NetListeren;
import com.jbb.library_common.retrofit.other.SubscribeUtils;
import com.jbb.library_common.utils.CommUtil;
import com.jbb.library_common.utils.FileUtil;
import com.jbb.library_common.utils.log.LogUtil;
import com.xsjqzt.module_main.greendao.DbManager;
import com.xsjqzt.module_main.model.user.UserInfoInstance;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/***
 * 心跳服务，每隔1 分钟请求一次服务器，提醒在线
 */
public class HeartBeatService extends Service {

    private Timer timer;
    private TimerTask timerTask;
    private long intervalTime = 60 * 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        if(timer != null ){
            timer.cancel();
            timer = null;
            timerTask = null;
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                sendHeartBeat();
            }
        };

        timer = new Timer();
        timer.schedule(timerTask,intervalTime,intervalTime);
    }

    private void sendHeartBeat() {
        SubscribeUtils.subscribe4(RetrofitManager.getInstance().getService(ApiService.class)
                        .sendHeartBeat(UserInfoInstance.getInstance().getBearer(),CommUtil.getVersionName()),
                BaseBean.class, new NetListeren<BaseBean>() {
            @Override
            public void onSuccess(BaseBean baseBean) {
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer != null)
        timer.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
