package com.xsjqzt.module_main.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.jbb.library_common.basemvp.ActivityManager;
import com.jbb.library_common.basemvp.BaseMvpActivity;
import com.jbb.library_common.comfig.InterfaceConfig;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.other.DefaultRationale;
import com.jbb.library_common.utils.CommUtil;
import com.jbb.library_common.utils.DeviceUtil;
import com.jbb.library_common.utils.MD5Util;
import com.jbb.library_common.utils.SharePreferensUtil;
import com.jbb.library_common.utils.ToastUtil;
import com.jbb.library_common.utils.Utils;
import com.jbb.library_common.utils.log.LogUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.dataSource.UserDataUtil;
import com.xsjqzt.module_main.faceSdk.FaceSet;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.model.user.UserInfoSerializUtil;
import com.xsjqzt.module_main.modle.FaceResult;
import com.xsjqzt.module_main.presenter.TokenPresenter;
import com.xsjqzt.module_main.util.SharedPrefUtils;
import com.xsjqzt.module_main.view.TokenView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Setting;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.functions.Consumer;

public class SplashActivity extends BaseMvpActivity<TokenView, TokenPresenter> implements TokenView {


    String macAddress = "2047DAF3E9AB";//测试写死的mac地址，绑定3出入口

    @Override
    public void init() {
        EMHelper.getInstance().init(getApplicationContext());//环信初始化，有注册过就会直接登录
        //检查服务器配置的url是否改变
        String url_root = SharePreferensUtil.getString(KeyContacts.URL_ROOT, KeyContacts.SP_NAME_JPUSH);
        if(!TextUtils.isEmpty(url_root)){
            InterfaceConfig.BASEURL = url_root;
        }

        initMac();
        initView();

    }

    private void checkFirstInstall() {
        boolean firstInstall = SharePreferensUtil.getBoolean(KeyContacts.SP_KEY_FIRSTINSTALL, true, KeyContacts.SP_NAME_USERINFO);
        if (firstInstall) {
            SharePreferensUtil.putBoolean(KeyContacts.SP_KEY_FIRSTINSTALL, false, KeyContacts.SP_NAME_USERINFO);
            FaceSet faceSet = new FaceSet(getApplication());
            faceSet.startTrack(0);
            faceSet.removeAllUser();
            faceSet.stopTrack();
        }
    }

    private void initView() {
        ActivityManager.getInstance().setAppStatus(KeyContacts.STATUS_NORMAL);

        UserInfoSerializUtil.initUserInstance();
        if (!UserInfoInstance.getInstance().hasLogin()) {
            loadKey();
//            bindDevice();
        } else {
            next(4000);
        }
//        loadKey();
    }


    private void initMac() {

        macAddress = DeviceUtil.getEthernetMac();
        if (TextUtils.isEmpty(macAddress)) {
            ToastUtil.showCustomToast("未获取到mac地址");
            return;
        }
        macAddress = macAddress.replaceAll(":", "").toUpperCase();
        String str1 = macAddress;
        String str2 = MD5Util.md5(macAddress).substring(0, 4).toUpperCase();

        UserInfoInstance.getInstance().setSn1(str1);
        UserInfoInstance.getInstance().setSn2(str2);
        UserInfoInstance.getInstance().setMacAddress(macAddress);

//        LogUtil.w("序列号 wifiaddr = " + wifiaddr);
        LogUtil.w("序列号 macAddress = " + macAddress);
        LogUtil.w("序列号 sn1 = " + str1);
        LogUtil.w("序列号 sn1 = " + str2);

        JPushInterface.setAlias(this, CommUtil.getRandomInt(9), (str1 + str2));

    }


    private void bindDevice() {

        int eid = 7;
        presenter.bindDevice(UserInfoInstance.getInstance().getSn1(), UserInfoInstance.getInstance().getSn2(), eid);
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
        timer.schedule(task, time);
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
        presenter.getToken(UserInfoInstance.getInstance().getSn1(), skey);
    }

    @Override
    public void loadKeyFail() {
        goToForResult(SystemInfoActivity.class, 10);
    }

    @Override
    public void getTokenSuccess() {
        String registrationID = JPushInterface.getRegistrationID(this);
        if (!TextUtils.isEmpty(registrationID)) {
            presenter.registrationId(registrationID);
        }
//        next(3000);
        registHX(UserInfoInstance.getInstance().getMacAddress().toLowerCase(), "123456");
    }

    @Override
    public void bindDeviceSuccess() {
        loadKey();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            loadKey();
        }
    }



    /**
     * 注册环信用户
     *
     * @param username mac地址注册
     * @param psw
     */

    public void registHX(final String username, final String psw) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // call method in SDK
//                    EMClient.getInstance().check();

                    EMClient.getInstance().createAccount(username, psw);
                    loginHX(username, psw);

                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            int errorCode = e.getErrorCode();
                            if (errorCode == EMError.NETWORK_ERROR) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ALREADY_EXIST) {
//                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.EXCEED_SERVICE_LIMIT) {
                                Toast.makeText(SplashActivity.this, getResources().getString(R.string.register_exceed_service_limit), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    loginHX(username, psw);
                }
            }
        }).start();

    }


    private void loginHX(String userName, String psw) {
        EMClient.getInstance().login(userName, psw, new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        next(3000);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        next(3000);
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }
}
