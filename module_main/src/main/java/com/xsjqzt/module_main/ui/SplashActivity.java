package com.xsjqzt.module_main.ui;

import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jbb.library_common.BaseApplication;
import com.jbb.library_common.basemvp.ActivityManager;
import com.jbb.library_common.basemvp.BaseMvpActivity;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.DeviceUtil;
import com.jbb.library_common.utils.MD5Util;
import com.jbb.library_common.utils.ToastUtil;
import com.jbb.library_common.utils.log.LogUtil;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.model.user.UserInfoSerializUtil;
import com.xsjqzt.module_main.presenter.TokenPresenter;
import com.xsjqzt.module_main.view.TokenView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends BaseMvpActivity<TokenView,TokenPresenter> implements TokenView {

//    private String sn1 = "12345678900001";//序列号1
//    private String sn2 = "12345678900002";//序列号2

    @Override
    public void init() {
        initMac();

        ActivityManager.getInstance().setAppStatus(KeyContacts.STATUS_NORMAL);
        EMHelper.getInstance().init(getApplicationContext());

        UserInfoSerializUtil.initUserInstance();
        if(!UserInfoInstance.getInstance().hasLogin()){
            loadKey();
        }else{
            next(1500);
        }
    }


    private void initMac() {
        //获取加密key
        String macAddress = DeviceUtil.getMacAddress(this);
        if(TextUtils.isEmpty(macAddress)) {
            ToastUtil.showCustomToast("未获取到mac地址");
            return;
        }
        String str1 = macAddress.substring(0,macAddress.length() /2);
        String str2 = macAddress.substring(macAddress.length() /2 , macAddress.length());

        UserInfoInstance.getInstance().setSn1(str1);
        UserInfoInstance.getInstance().setSn2(str2);
        UserInfoInstance.getInstance().setMacAddress(macAddress);

        LogUtil.w("macAddress = " + macAddress);
        LogUtil.w("序列号 sn1 = " + str1);
        LogUtil.w("序列号 sn1 = " + str2);
    }



    private void bindDevice(){
        //获取加密key
//        String macAddress = DeviceUtil.getMacAddress(this);
//        if(TextUtils.isEmpty(macAddress)) {
//            ToastUtil.showCustomToast("mac地址为空");
//            return;
//        }
//        String str1 = macAddress.substring(0,macAddress.length() /2);
//        String str2 = macAddress.substring(macAddress.length() /2 , macAddress.length());
////        sn1 =  MD5Util.md5(str1);
////        sn2 =  MD5Util.md5(str2);
//        sn1 =  str1;
//        sn2 =  str2;
//        LogUtil.w("macAddress = " + macAddress);
//        LogUtil.w("序列号 sn1 = " + sn1);
//        LogUtil.w("序列号 sn1 = " + sn2);
//
//        int eid = 2;
//        presenter.bindDevice(sn1,sn2,eid);
    }

    //获取加密key
    private void loadKey() {
        presenter.loadKey(UserInfoInstance.getInstance().getSn1());
    }

    private void next(long time) {
        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
//                goTo(MainActivity.class);
                ARouter.getInstance().build("/module_main/main").navigation();//
                finish();
            }
        };
        timer.schedule(task,time);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public String getATitle() {
        return null;
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public TokenPresenter initPresenter() {
        return new TokenPresenter();
    }

    @Override
    public void showLoading() {
        show("数据初始化中...");
    }

    @Override
    public void hideLoading() {
        dismiss();
    }

    @Override
    public void error(Exception e) {

    }

    @Override
    public void loadKeySuccess(String key) {
        UserInfoInstance.getInstance().setKey(key);
        //获取token
        //获取token的key，生成规则：skey = md5(sn1+sn2+key)
        String skey = MD5Util.md5(UserInfoInstance.getInstance().getSn1() + UserInfoInstance.getInstance().getSn2() + key);
        presenter.getToken(UserInfoInstance.getInstance().getSn1(),skey);
    }

    @Override
    public void loadKeyFail() {
        goToForResult(SystemInfoActivity.class,10);
    }

    @Override
    public void getTokenSuccess() {
        next(50);
    }

    @Override
    public void bindDeviceSuccess() {
        loadKey();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 10){
            loadKey();
        }
    }
}
