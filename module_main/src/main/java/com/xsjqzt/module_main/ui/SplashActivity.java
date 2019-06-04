package com.xsjqzt.module_main.ui;

import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
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


    String macAddress = "2047DAF3E9AE";//测试写死的mac地址，绑定3出入口

    @Override
    public void init() {
        initMac();

        ActivityManager.getInstance().setAppStatus(KeyContacts.STATUS_NORMAL);
        EMHelper.getInstance().init(getApplicationContext());

        UserInfoSerializUtil.initUserInstance();
        if(!UserInfoInstance.getInstance().hasLogin()){
            loadKey();
//            bindDevice();
        }else{
            next(4000);
        }
//        next(1500);
    }


    private void initMac() {
//      String wifiaddr = DeviceUtil.getMacDefault(this);
        macAddress = DeviceUtil.getEthernetMac();
        if(TextUtils.isEmpty(macAddress)) {
            ToastUtil.showCustomToast("未获取到mac地址");
            return;
        }
        macAddress = macAddress.replaceAll(":","").toUpperCase();
        String str1 = macAddress;
        String str2 = MD5Util.md5(macAddress).substring(0 , 4).toUpperCase();

        UserInfoInstance.getInstance().setSn1(str1);
        UserInfoInstance.getInstance().setSn2(str2);
        UserInfoInstance.getInstance().setMacAddress(macAddress);

//        LogUtil.w("序列号 wifiaddr = " + wifiaddr);
        LogUtil.w("序列号 macAddress = " + macAddress);
        LogUtil.w("序列号 sn1 = " + str1);
        LogUtil.w("序列号 sn1 = " + str2);
    }



    private void bindDevice(){

        int eid = 6;
        presenter.bindDevice(UserInfoInstance.getInstance().getSn1(),UserInfoInstance.getInstance().getSn2() ,eid);
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
        next(3000);
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
