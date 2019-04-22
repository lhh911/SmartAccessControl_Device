package com.xsjqzt.module_main.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;

import com.jbb.library_common.basemvp.BaseActivity;
import com.jbb.library_common.other.DefaultRationale;
import com.jbb.library_common.utils.FileUtil;
import com.jbb.library_common.utils.GlideUtils;
import com.xiao.nicevideoplayer.SimpleVideoPlayer;
import com.xiao.nicevideoplayer.SimpleVideoPlayerManager;
import com.xsjqzt.module_main.R;
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

public class MainActivity extends BaseActivity {

    private Banner banner ;
    private SimpleVideoPlayer videoPlayer;

    private int showType = 2;// 1 图片广告，2 视频广告


    @Override
    public void init() {
        banner = findViewById(R.id.banner);
        videoPlayer = findViewById(R.id.videoplayer);
        requestPermiss();
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

    }

    private void requestPermiss(){

        DefaultRationale rationale = new DefaultRationale();
        AndPermission.with(this)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
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
}

