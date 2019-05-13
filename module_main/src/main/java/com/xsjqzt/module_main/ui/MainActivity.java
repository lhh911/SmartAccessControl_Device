package com.xsjqzt.module_main.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jbb.library_common.basemvp.BaseMvpActivity;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.other.DefaultRationale;
import com.jbb.library_common.utils.FileUtil;
import com.jbb.library_common.utils.GlideUtils;
import com.jbb.library_common.utils.MD5Util;
import com.jbb.library_common.utils.Utils;
import com.jbb.library_common.utils.log.LogUtil;
import com.xiao.nicevideoplayer.SimpleVideoPlayer;
import com.xiao.nicevideoplayer.SimpleVideoPlayerManager;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.presenter.TokenPresenter;
import com.xsjqzt.module_main.view.TokenView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Setting;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Route(path = "/module_main/main")
public class MainActivity extends BaseMvpActivity<TokenView,TokenPresenter> implements TokenView  {

    private Banner banner ;
    private SimpleVideoPlayer videoPlayer;

    private int showType = 2;// 1 图片广告，2 视频广告
    private MyBroadcastReceiver mReceiver;

    private String sn1;//序列号1
    private String sn2;//序列号2


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int action = intent.getIntExtra(KeyContacts.START_LAUNCH_ACTION, KeyContacts.STATUS_NORMAL);
        switch (action) {
            case KeyContacts.STATUS_FORCE_KILLED:
                restartApp();
                break;
            case KeyContacts.STATUS_NORMAL:
                break;
            default:
                break;
        }

    }

    @Override
    protected void restartApp() {
        startActivity(new Intent(this,SplashActivity.class));
        finish();
    }

    @Override
    public void init() {
        banner = findViewById(R.id.banner);
        videoPlayer = findViewById(R.id.videoplayer);
        requestPermiss();

        registReceiver();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public String getATitle() {
        return null;
    }

    private void initView(){
        if(showType == 1){
            initImageAd();
        }else{
            initVideo();
        }
    }


    private void initImageAd() {

        banner.setVisibility(View.VISIBLE);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(getImages());

        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    private void initVideo() {
        videoPlayer.setVisibility(View.VISIBLE);
//        AssetFileDescriptor assetFileDescriptor = getAssets().openFd("ad_movice.mp4");
//        Uri mUri = Uri.parse("android.resource://" + getPackageName() + "/"+ R.raw.ad_movice);
        String path = FileUtil.getAppDownLoadFilePath(this);
        path = path + File .separator + "123.mp4";

        videoPlayer.setUp(path ,null);//设置地址
        videoPlayer.start();
    }



    public void btn1Click(View view) {
        goTo(RoomNumPswUnlockActivity.class);
    }

    public void btn2Click(View view) {
        goTo(RegistICCardActivity.class);
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

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void error(Exception e) {

    }


    public class GlideImageLoader extends ImageLoader {

        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            GlideUtils.display(context, (String) path,imageView);
        }

    }

    private List<String> getImages(){
        List<String> images = new ArrayList<>();
        images.add("http://i1.mopimg.cn/img/dzh/2015-05/1288/20150502115717123.jpg");
        images.add("http://i1.mopimg.cn/img/dzh/2015-05/855/2015050211571275.jpg");
        images.add("http://i1.mopimg.cn/img/dzh/2015-05/389/20150502115712989.jpg");
        return images;
    }



    @Override
    protected void onResume() {
        super.onResume();
        SimpleVideoPlayerManager.instance().resumeNiceVideoPlayer();
        if(banner != null)
            banner.startAutoPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SimpleVideoPlayerManager.instance().suspendNiceVideoPlayer();
        if(banner != null)
            banner.stopAutoPlay();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SimpleVideoPlayerManager.instance().releaseNiceVideoPlayer();

        if(mReceiver != null)
            unregisterReceiver(mReceiver);
    }

    @Override
    public TokenPresenter initPresenter() {
        return new TokenPresenter();
    }

    private void requestPermiss(){

        DefaultRationale rationale = new DefaultRationale();
        AndPermission.with(this)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE,Permission.CAMERA)
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

                        if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, data)) {//点击了不再提示后，不会弹出申请框，需要手动跳转设置权限页面
                            List<String> permissionNames = Permission.transformText(MainActivity.this, data);
                            String message = MainActivity.this.getString(R.string.message_permission_rationale)+ permissionNames.toString();
                            new AlertDialog.Builder(MainActivity.this)
                                    .setCancelable(false)
                                    .setTitle("提示")
                                    .setMessage(message)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            AndPermission.with(MainActivity.this)
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


    private void registReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(KeyContacts.ACTION_API_KEY_INVALID);
        filter.addAction(Manifest.permission.ACCESS_NETWORK_STATE);
        filter.addAction(Manifest.permission.CHANGE_NETWORK_STATE);
        mReceiver = new MyBroadcastReceiver();
        registerReceiver(mReceiver,filter);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == KeyContacts.ACTION_API_KEY_INVALID){
                int code = intent.getIntExtra("code", 0);
                if(code == 2001){//token过期，刷新
                    refreshToken();
                }else if(code == 2002){//refresh_code 过期，跳到登录页
                    //
                    UserInfoInstance.getInstance().reset();
                    login();
                }
            }else if(intent.getAction() == Manifest.permission.ACCESS_NETWORK_STATE || intent.getAction() == Manifest.permission.CHANGE_NETWORK_STATE ){
                //监听网络变化
                if(Utils.getNetWorkState(MainActivity.this)){
                    LogUtil.w("NetWorkState = " + true);
                    login();
                }
            }
        }
    }

    private void login() {
        if(UserInfoInstance.getInstance().hasLogin())//没登录需要登录下
            return;
        //获取加密key
        presenter.loadKey(sn1);
    }

    private void refreshToken() {
        presenter.refreshToken();
    }



}

