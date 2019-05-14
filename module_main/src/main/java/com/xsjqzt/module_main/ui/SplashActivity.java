package com.xsjqzt.module_main.ui;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jbb.library_common.basemvp.ActivityManager;
import com.jbb.library_common.basemvp.BaseMvpActivity;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.MD5Util;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.model.user.UserInfoSerializUtil;
import com.xsjqzt.module_main.presenter.TokenPresenter;
import com.xsjqzt.module_main.view.TokenView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends BaseMvpActivity<TokenView,TokenPresenter> implements TokenView {

    private String sn1 = "12345678900001";//序列号1
    private String sn2 = "12345678900002";//序列号2

    @Override
    public void init() {
        ActivityManager.getInstance().setAppStatus(KeyContacts.STATUS_NORMAL);
        EMHelper.getInstance().init(getApplicationContext());

        UserInfoSerializUtil.initUserInstance();
        if(!UserInfoInstance.getInstance().hasLogin()){
            login();
        }else{
            next(1500);
        }
    }

    private void login() {
        //获取加密key
        presenter.loadKey(sn1);
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
        String skey = MD5Util.md5(sn1 + sn2 + key);
        presenter.getToken(sn1,skey);
    }

    @Override
    public void getTokenSuccess() {
        next(50);
    }
}
