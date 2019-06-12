package com.xsjqzt.module_main.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jbb.library_common.basemvp.ActivityManager;
import com.jbb.library_common.basemvp.BaseMvpActivity;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.other.DefaultRationale;
import com.jbb.library_common.utils.DeviceUtil;
import com.jbb.library_common.utils.MD5Util;
import com.jbb.library_common.utils.SharePreferensUtil;
import com.jbb.library_common.utils.ToastUtil;
import com.jbb.library_common.utils.log.LogUtil;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.faceSdk.FaceSet;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.model.user.UserInfoSerializUtil;
import com.xsjqzt.module_main.presenter.TokenPresenter;
import com.xsjqzt.module_main.view.TokenView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Setting;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

public class SplashActivity extends BaseMvpActivity<TokenView,TokenPresenter> implements TokenView {


    String macAddress = "2047DAF3E9AB";//测试写死的mac地址，绑定3出入口

    @Override
    public void init() {
        checkFirstInstall();
        initMac();
        initView();
//        requestPermiss();
    }

    private void checkFirstInstall(){
        boolean firstInstall = SharePreferensUtil.getBoolean(KeyContacts.SP_KEY_FIRSTINSTALL, true, KeyContacts.SP_NAME_USERINFO);
        if(firstInstall){
            SharePreferensUtil.putBoolean(KeyContacts.SP_KEY_FIRSTINSTALL, false, KeyContacts.SP_NAME_USERINFO);
            FaceSet faceSet = new FaceSet(getApplication());
            faceSet.startTrack(0);
            faceSet.removeAllUser();
            faceSet.stopTrack();
        }
    }

    private void  initView(){
        ActivityManager.getInstance().setAppStatus(KeyContacts.STATUS_NORMAL);
        EMHelper.getInstance().init(getApplicationContext());

        UserInfoSerializUtil.initUserInstance();
        if(!UserInfoInstance.getInstance().hasLogin()){
            loadKey();
//            bindDevice();
        }else{
            next(4000);
        }
    }


    private void initMac() {

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

        int eid = 7;
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
        String registrationID = JPushInterface.getRegistrationID(this);
        if(!TextUtils.isEmpty(registrationID)) {
            presenter.registrationId(registrationID);
        }
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

    private void requestPermiss() {

        DefaultRationale rationale = new DefaultRationale();
        AndPermission.with(this)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.CAMERA)
                .rationale(rationale)//如果用户拒绝过该权限，则下次会走showRationale方法
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        initView();
                                           }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                        if (AndPermission.hasAlwaysDeniedPermission(SplashActivity.this, data)) {//点击了不再提示后，不会弹出申请框，需要手动跳转设置权限页面
                            List<String> permissionNames = Permission.transformText(SplashActivity.this, data);
                            String message = SplashActivity.this.getString(R.string.message_permission_rationale) + permissionNames.toString();
                            new AlertDialog.Builder(SplashActivity.this)
                                    .setCancelable(false)
                                    .setTitle("提示")
                                    .setMessage(message)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            AndPermission.with(SplashActivity.this)
                                                    .runtime()
                                                    .setting()
                                                    .onComeback(new Setting.Action() {
                                                        @Override
                                                        public void onAction() {
                                                            //返回
                                                            requestPermiss();
                                                        }
                                                    }).start();
                                        }
                                    })
                                    .show();

                        }
                    }
                })
                .start();
    }
}
