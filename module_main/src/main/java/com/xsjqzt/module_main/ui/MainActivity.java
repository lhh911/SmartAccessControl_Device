package com.xsjqzt.module_main.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Gpio;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.jbb.library_common.basemvp.BaseMvpActivity;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.other.DefaultRationale;
import com.jbb.library_common.utils.BitmapUtil;
import com.jbb.library_common.utils.CommUtil;
import com.jbb.library_common.utils.FileUtil;
import com.jbb.library_common.utils.GlideUtils;
import com.jbb.library_common.utils.MD5Util;
import com.jbb.library_common.utils.ToastUtil;
import com.jbb.library_common.utils.Utils;
import com.jbb.library_common.utils.log.LogUtil;
import com.readsense.cameraview.camera.CameraView;
import com.readsense.cameraview.camera.Size;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiao.nicevideoplayer.SimpleVideoPlayer;
import com.xiao.nicevideoplayer.SimpleVideoPlayerManager;
import com.xsjqzt.module_main.Config.DemoConfig;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.activity.FaceDemoActivity;
import com.xsjqzt.module_main.activity.base.ExApplication;
import com.xsjqzt.module_main.dataSource.UserDataUtil;
import com.xsjqzt.module_main.faceSdk.FaceSet;
import com.xsjqzt.module_main.greendao.DbManager;
import com.xsjqzt.module_main.greendao.FaceImageDao;
import com.xsjqzt.module_main.greendao.ICCardDao;
import com.xsjqzt.module_main.greendao.IDCardDao;
import com.xsjqzt.module_main.greendao.OpenCodeDao;
import com.xsjqzt.module_main.greendao.entity.FaceImage;
import com.xsjqzt.module_main.greendao.entity.ICCard;
import com.xsjqzt.module_main.greendao.entity.IDCard;
import com.xsjqzt.module_main.greendao.entity.OpenCode;
import com.xsjqzt.module_main.greendao.entity.OpenRecord;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.modle.FaceResult;
import com.xsjqzt.module_main.modle.FaceSuccessEventBean;
import com.xsjqzt.module_main.modle.User;
import com.xsjqzt.module_main.presenter.MainPresenter;
import com.xsjqzt.module_main.receive.AlarmReceiver;
import com.xsjqzt.module_main.service.DownAllDataService;
import com.xsjqzt.module_main.util.MyToast;
import com.xsjqzt.module_main.util.SharedPrefUtils;
import com.xsjqzt.module_main.util.TrackDrawUtil;
import com.xsjqzt.module_main.view.MainView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Setting;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.functions.Consumer;
import mobile.ReadFace.YMFace;
import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;
import tp.xmaihh.serialport.utils.ByteUtil;

@Route(path = "/module_main/main")
public class MainActivity extends BaseMvpActivity<MainView, MainPresenter> implements MainView {

    private Banner banner;
    private SimpleVideoPlayer videoPlayer;

    //视频呼叫layout
    private LinearLayout callVideoLayout;
    private TextView callNumTv, callStatusTv, callTipTv;

    private View toolsBar;

    //房号密码号输入layout
    private LinearLayout roomNumLayout;//房号输入布局
    private EditText roomNumEt;
    private LinearLayout inputNumLayout;//输入框父布局
//    private ImgTextView successLayout, errorLayout;//成功和识别布局
//    private int showSucOrError = 1; // 1 开门成功 ，2 开门失败


    private int showType = 1;// 1 图片广告，2 视频广告
    private MyBroadcastReceiver mReceiver;

    private PendingIntent pi;
    private AlarmManager am;

    //串口
    private SerialHelper serialHelper;
    private String sPort = "/dev/ttyS3";
    private int iBaudRate = 115200;

    private int mType;// 0 默认什么都没显示， 1 密码开锁布局显示，2 视频电话显示
    private MyHandler doorHandler;

    private boolean inputLayoutShow;//底部输入布局是否显示出来，显示出来后10秒无操作自动掩藏
    private long startShowTime;//记录底部布局显示的开始时间，10秒无操作自动掩藏
    //    private Timer inputLayoutShowTime;//10秒内检查操作定时器
//    private TimerTask inputLayoutShowTask;
    private InutLayoutShowTimeRunnable inutLayoutShowTimeRunnable;


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
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }

    @Override
    public void init() {
        //视频通话
        callVideoLayout = findViewById(R.id.call_video_layout);
        callNumTv = findViewById(R.id.call_num_tv);
        callStatusTv = findViewById(R.id.call_status_tv);
        callTipTv = findViewById(R.id.call_tip_tv);
        toolsBar = findViewById(R.id.tools_bar);

        //房号输入
        roomNumLayout = findViewById(R.id.input_num_layout);
        roomNumEt = findViewById(R.id.room_psw_et);
        TextView bottomTv = findViewById(R.id.bottom_tv);
        inputNumLayout = findViewById(R.id.input_layout);
//        successLayout = findViewById(R.id.success_layout);
//        errorLayout = findViewById(R.id.error_layout);

        Drawable drawable = getResources().getDrawable(R.mipmap.icon_gth);
        drawable.setBounds(0, 0, CommUtil.dp2px(13), CommUtil.dp2px(13));
        bottomTv.setCompoundDrawables(drawable, null, null, null);


        banner = findViewById(R.id.banner);
        videoPlayer = findViewById(R.id.videoplayer);
        requestPermiss();

        registReceiver();


        setAlarm();
        startMeasuing();

//        loadCardData();

        EventBus.getDefault().register(this);
//        test();

        initFaceCamera();
        initFaceEvent();
    }

    private void loadCardData() {
//        downICCardData();
//        downIDCardData();
//        startService(new Intent(this,DownAllDataService.class));
    }

    private void test() {
        List<ICCard> list = DbManager.getInstance().getDaoSession().getICCardDao().queryBuilder().list();
        StringBuffer sf = new StringBuffer();
        sf.append("数据库的IC卡").append("\n");
        for (ICCard card : list) {
            sf.append(card.getUser_name() + "，" + card.getSn() + "，" + card.getSid()).append("\n");
        }

        new AlertDialog.Builder(this).setMessage(sf.toString()).show();

        int type = 1;
        String sn = "ic20190501";
        if (type == 1) {
            presenter.uploadICCardRecord(type, sn);
        } else {
            presenter.uploadIDCardRecord(type, sn);
        }


    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public String getATitle() {
        return null;
    }

    private void initView() {
        if (showType == 1) {
            initImageAd();
        } else {
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
        String path = FileUtil.getAppVideoPath(this);
        path = path + File.separator + "123.mp4";

        videoPlayer.setUp(path, null);//设置地址
        videoPlayer.start();
    }


    public void btn1Click(View view) {
        if (inputLayoutShow) {
            String inputNum = roomNumEt.getText().toString().trim();
            if (TextUtils.isEmpty(inputNum))
                return;

            checkInput(inputNum);
        } else {
            showRoomNumOpen();
        }
    }

    public void btn2Click(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("mType", 1);
        goTo(RegistICCardActivity.class, bundle);
    }

    public void btn3Click(View view) {
        goTo(SystemInfoActivity.class);
    }

    public void btn4Click(View view) {
        goTo(FaceDemoActivity.class);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mType == 1) {
                hideRoomNumLayout();
            } else if (mType == 2) {
                hideCallVideoLayout();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_0) {
            String oldNum = roomNumEt.getText().toString().trim();
            setInputData(oldNum, "0");
            showRoomNumOpen();
        } else if (keyCode == KeyEvent.KEYCODE_1) {
            String oldNum = roomNumEt.getText().toString().trim();
            setInputData(oldNum, "1");
            showRoomNumOpen();
        } else if (keyCode == KeyEvent.KEYCODE_2) {
            String oldNum = roomNumEt.getText().toString().trim();
            setInputData(oldNum, "2");
            showRoomNumOpen();
        } else if (keyCode == KeyEvent.KEYCODE_3) {
            String oldNum = roomNumEt.getText().toString().trim();
            setInputData(oldNum, "3");
            showRoomNumOpen();
        } else if (keyCode == KeyEvent.KEYCODE_4) {
            String oldNum = roomNumEt.getText().toString().trim();
            setInputData(oldNum, "4");
            showRoomNumOpen();
        } else if (keyCode == KeyEvent.KEYCODE_5) {
            String oldNum = roomNumEt.getText().toString().trim();
            setInputData(oldNum, "5");
            showRoomNumOpen();
        } else if (keyCode == KeyEvent.KEYCODE_6) {
            String oldNum = roomNumEt.getText().toString().trim();
            setInputData(oldNum, "6");
            showRoomNumOpen();
        } else if (keyCode == KeyEvent.KEYCODE_7) {
            String oldNum = roomNumEt.getText().toString().trim();
            setInputData(oldNum, "7");
            showRoomNumOpen();
        } else if (keyCode == KeyEvent.KEYCODE_8) {
            String oldNum = roomNumEt.getText().toString().trim();
            setInputData(oldNum, "8");
            showRoomNumOpen();
        } else if (keyCode == KeyEvent.KEYCODE_9) {
            String oldNum = roomNumEt.getText().toString().trim();
            setInputData(oldNum, "9");
            showRoomNumOpen();
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {//删除
            deleteInputData();
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            String inputNum = roomNumEt.getText().toString().trim();
            if (TextUtils.isEmpty(inputNum))
                return true;

            checkInput(inputNum);
            return true;
        }

        return super.onKeyDown(keyCode, event);
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
            GlideUtils.display(context, (String) path, imageView);
        }

    }

    private List<String> getImages() {
        List<String> images = new ArrayList<>();
        images.add("http://i1.mopimg.cn/img/dzh/2015-05/1288/20150502115717123.jpg");
        images.add("http://i1.mopimg.cn/img/dzh/2015-05/855/2015050211571275.jpg");
        images.add("http://i1.mopimg.cn/img/dzh/2015-05/389/20150502115712989.jpg");
        return images;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        SimpleVideoPlayerManager.instance().releaseNiceVideoPlayer();

        if (mReceiver != null)
            unregisterReceiver(mReceiver);


        if (am != null)
            am.cancel(pi);

        removeTask();
        endCall();
        stopMeasuing();
    }

    @Override
    public MainPresenter initPresenter() {
        return new MainPresenter();
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

                        if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, data)) {//点击了不再提示后，不会弹出申请框，需要手动跳转设置权限页面
                            List<String> permissionNames = Permission.transformText(MainActivity.this, data);
                            String message = MainActivity.this.getString(R.string.message_permission_rationale) + permissionNames.toString();
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
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        filter.addAction(Manifest.permission.CHANGE_NETWORK_STATE);
        filter.addAction(KeyContacts.ACTION_RECEICE_NOTITY);
        mReceiver = new MyBroadcastReceiver();
        registerReceiver(mReceiver, filter);


    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == KeyContacts.ACTION_API_KEY_INVALID) {
                int code = intent.getIntExtra("code", 0);
                if (code == 2001) {//token过期，刷新
                    refreshToken();
                } else if (code == 2002) {//refresh_code 过期，跳到登录页
                    //
                    UserInfoInstance.getInstance().reset();
                    login();
                }
            } else if (intent.getAction() == ConnectivityManager.CONNECTIVITY_ACTION) {
                //监听网络变化
                if (Utils.getNetWorkState(MainActivity.this)) {
                    LogUtil.w("NetWorkState = " + true);
                    startService(new Intent(context, DownAllDataService.class));
                    login();
                }
            } else if (intent.getAction() == KeyContacts.ACTION_RECEICE_NOTITY) {
                handleNotity(intent.getExtras());
            }
        }
    }

    private void handleNotity(final Bundle bundle) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
                    JSONObject json = new JSONObject(extras);
                    int type = json.optInt("type");

                    if (type == 1) {//更新身份证
                        downIDCardData();
                    } else if (type == 2) {//更新ic卡信息
                        downICCardData();
                    } else if (type == 3) {//临时密码
                        downOpenCode();
                    } else if (type == 4) {//下载人脸图片，并注册到阅面的人脸库，将注册状态发送给后台服务器
                        downFaceImage();
                    } else if (type == 5) {//设置音量
                        setVoice(json.getInt("volume"));
                    } else if (type == 6) {//设备重启

                    }
                } catch (Exception e) {

                }
            }
        }).start();


    }

    private void downOpenCode() {
        OpenCodeDao openCodeDao = DbManager.getInstance().getDaoSession().getOpenCodeDao();
        OpenCode unique = openCodeDao.queryBuilder().limit(1).orderDesc(OpenCodeDao.Properties.Update_time).unique();
        int update_time = 0;
        if (unique != null) {
            update_time = unique.getUpdate_time();
        }
        presenter.downOpenCode(update_time);
    }


    private void downFaceImage() {
        FaceImageDao faceImageDao = DbManager.getInstance().getDaoSession().getFaceImageDao();
        FaceImage faceImage = faceImageDao.queryBuilder().limit(1).orderDesc(FaceImageDao.Properties.Update_time).unique();
        int update_time = 0;
        if (faceImage != null) {
            update_time = faceImage.getUpdate_time();
        }
        presenter.loadFaceImage(this, update_time);
    }

    private void setVoice(final int volume) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //设置音量
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);

                //通知服务器设置成功
                presenter.setVoice(volume);
            }
        });

    }

    private void downICCardData() {
        ICCardDao icCardDao = DbManager.getInstance().getDaoSession().getICCardDao();
        ICCard iccard = icCardDao.queryBuilder().limit(1).orderDesc(ICCardDao.Properties.Update_time).unique();
        int update_time = 0;
        if (iccard != null) {
            update_time = iccard.getUpdate_time();
        }
        presenter.loadICCards(update_time);
    }

    private void downIDCardData() {
        IDCardDao idCardDao = DbManager.getInstance().getDaoSession().getIDCardDao();
        IDCard idcard = idCardDao.queryBuilder().limit(1).orderDesc(IDCardDao.Properties.Update_time).unique();
        int update_time = 0;
        if (idcard != null) {
            update_time = idcard.getUpdate_time();
        }
        presenter.loadIDCards(update_time);

    }


    private void login() {
        if (UserInfoInstance.getInstance().hasLogin())//没登录需要登录下
            return;
        //获取加密key
        presenter.loadKey(UserInfoInstance.getInstance().getSn1());
    }

    private void refreshToken() {
        presenter.refreshToken();
    }


    /**
     * 检查输入的是房号还是开门密码
     *
     * @param inputNum 4位或6位位房号 ， 5位为开门密码
     */
    private void checkInput(String inputNum) {
        if (inputNum.length() == 5) {//密码开门
            //请求接口验证密码是否正确，是就开门
            OpenCode openCode = DbManager.getInstance().getDaoSession().getOpenCodeDao().queryBuilder()
                    .where(OpenCodeDao.Properties.Code.eq(inputNum)).unique();

            if (openCode != null) {
                int expiry_time = openCode.getExpiry_time();
                long now = System.currentTimeMillis();
                if (expiry_time < (now / 1000)) {//过期了
//                    showSucOrError = 2;
                    setShowSucOrError(false, inputNum);
                } else {
//                    showSucOrError = 1;
                    setShowSucOrError(true, inputNum);
                }

            } else {
                setShowSucOrError(false, inputNum);
            }
        } else if (inputNum.length() == 4 || inputNum.length() == 6) {
            showCallVideoLayout();

            //根据房号获取userid，再拨视频通话
            callVideo(inputNum);
        } else {
            ToastUtil.showCustomToast("请输入正确的房间号或者临时密码");
        }
    }


    //密码开门显示结果
    private void setShowSucOrError(boolean success, String code) {
        if (success) {//开门成功
//            inputNumLayout.setVisibility(View.GONE);
//            successLayout.setVisibility(View.VISIBLE);
//            errorLayout.setVisibility(View.GONE);
            hideRoomNumLayout();
            Message msg = Message.obtain();
            msg.what = 1;
            msg.arg1 = 3;
            msg.obj = code;
            doorHandler.sendMessage(msg);
        } else {
            MyToast.showToast("密码错误", R.mipmap.icon_error, "#FF0000");
//            inputNumLayout.setVisibility(View.GONE);
//            successLayout.setVisibility(View.GONE);
//            errorLayout.setVisibility(View.VISIBLE);
        }

//        starTime();
    }

    //密码开门时错误情况下3秒后退回输入状态
//    private void starTime() {
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (showSucOrError == 1) {
//                            hideRoomNumLayout();
//                        } else {
//                            mType = 1;
//                        }
//                        inputNumLayout.setVisibility(View.VISIBLE);
//                        successLayout.setVisibility(View.GONE);
//                        errorLayout.setVisibility(View.GONE);
//                    }
//                });
//            }
//        };
//
//        Timer timer = new Timer();
//        timer.schedule(task, 3000);
//    }


    //拨打视频通话
    private void callVideo(String inputNum) {

        //通过房号获取到环信的账号名（接口）

        callNumTv.setText(inputNum);
        try {//单参数
            EMClient.getInstance().callManager().makeVideoCall(inputNum, "");
        } catch (EMServiceNotReadyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EMClient.getInstance().callManager().addCallStateChangeListener(new EMCallStateChangeListener() {
            @Override
            public void onCallStateChanged(CallState callState, CallError error) {
                switch (callState) {
                    case CONNECTING: // 正在连接对方
                        ToastUtil.showCustomToast("正在连接对方");
                        callStatusTv.setText("呼叫中，请稍候...");
                        break;
                    case CONNECTED: // 双方已经建立连接
                        ToastUtil.showCustomToast("双方已经建立连接");
                        callStatusTv.setText("呼叫中，请稍候...");
                        break;

                    case ACCEPTED: // 电话接通成功
                        ToastUtil.showCustomToast("电话接通成功");
                        startTimer();
                        break;
                    case DISCONNECTED: // 电话断了
                        ToastUtil.showCustomToast("电话断了");
                        removeTask();
                        endCall();
                        break;
                    case NETWORK_UNSTABLE: //网络不稳定
                        if (error == CallError.ERROR_NO_DATA) {
                            //无通话数据
                        } else {
                        }

                        ToastUtil.showCustomToast("网络不稳定");
                        break;
                    case NETWORK_NORMAL: //网络恢复正常
                        ToastUtil.showCustomToast("网络恢复正常");
                        break;
                    default:
                        break;
                }

            }
        });
    }


    int delayMillis = 1000;
    int duration;//通话时长
    Handler timeHandler;
    Runnable timeRunnable;
    String mTimePattern = "mm:ss";

    //开始通话计时
    private void startTimer() {
        duration = 0;
        startTask();
    }

    private void startTask() {
        if (timeHandler != null && timeRunnable != null) {
            removeTask();
        }

        timeHandler = new Handler();
        timeRunnable = new TimeRunnable(this);
        timeHandler.postDelayed(timeRunnable, delayMillis);
    }

    private void removeTask() {
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
            timeHandler = null;
            timeRunnable = null;
        }
    }


    public class TimeRunnable implements Runnable {
        WeakReference<Activity> weakReference = null;

        public TimeRunnable(Activity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            if (weakReference.get() != null) {
                duration++;
                callStatusTv.setText("通话中 " + DurationFormatUtils.formatDuration(duration, mTimePattern));
                timeHandler.postDelayed(timeRunnable, delayMillis);
            }
        }
    }

    public void endCall() {
        try {
            EMClient.getInstance().callManager().endCall();
            hideCallVideoLayout();
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
        }
    }


    public void showAnim(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.callview_in);
        roomNumLayout.startAnimation(animation);
    }

    public void dismissAnim(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.callview_out);
        view.startAnimation(animation);
    }

    private void deleteInputData() {
        String num = roomNumEt.getText().toString().trim();
        if (num.length() > 0) {
            String str = num.substring(0, num.length() - 1);
            roomNumEt.setText(str);
        }
    }

    private void setInputData(String oldNum, String inputNum) {
        roomNumEt.setText(oldNum + inputNum);
    }

    //显示密码输入框开门ui
    private void showRoomNumOpen() {

        startShowTime = System.currentTimeMillis();
        startShowLayoutTime();
        if (!inputLayoutShow) {
            roomNumLayout.setVisibility(View.VISIBLE);
//            inputNumLayout.setVisibility(View.VISIBLE);
//            successLayout.setVisibility(View.GONE);
//            errorLayout.setVisibility(View.GONE);
            roomNumEt.setText("");
            showAnim(roomNumLayout);

            callVideoLayout.setVisibility(View.GONE);
            mType = 1;
            inputLayoutShow = true;
        }

    }

    //开启任务检查布局在十秒内是否有其他操作，没有就掩藏底部输入布局
    private void startShowLayoutTime() {

        if (inutLayoutShowTimeRunnable != null && inutLayoutShowTimeRunnable != null) {
            doorHandler.removeCallbacks(inutLayoutShowTimeRunnable);
        }

        if (inutLayoutShowTimeRunnable == null) {
            inutLayoutShowTimeRunnable = new InutLayoutShowTimeRunnable(this);
        }

        doorHandler.postDelayed(inutLayoutShowTimeRunnable, 10000);


    }

    public class InutLayoutShowTimeRunnable implements Runnable {
        WeakReference<Activity> weakReference = null;

        public InutLayoutShowTimeRunnable(Activity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            if (weakReference.get() != null) {
                long endTime = System.currentTimeMillis();
                if (endTime - startShowTime >= 10000) {
                    hideRoomInputLayout();

                }
            }
        }
    }


    //掩藏所有底部输入布局
    private void hideRoomInputLayout() {
        roomNumLayout.setVisibility(View.GONE);
//        inputNumLayout.setVisibility(View.VISIBLE);
//        successLayout.setVisibility(View.GONE);
//        errorLayout.setVisibility(View.GONE);
        roomNumEt.setText("");

        callVideoLayout.setVisibility(View.GONE);
        callStatusTv.setText("呼叫中");
        callNumTv.setText("");

        inputLayoutShow = false;
        mType = 0;
    }

    //单独显示视频通话ui
    private void showCallVideoLayout() {
        roomNumLayout.setVisibility(View.GONE);
        callVideoLayout.setVisibility(View.VISIBLE);
        showAnim(callVideoLayout);
        mType = 0;
    }

    //单独掩藏视频通话ui
    private void hideCallVideoLayout() {
        callVideoLayout.setVisibility(View.GONE);
        dismissAnim(callVideoLayout);
        mType = 0;
    }

    //单独掩藏密码开门ui
    private void hideRoomNumLayout() {
        roomNumLayout.setVisibility(View.GONE);
        dismissAnim(roomNumLayout);
        mType = 0;
    }


    /**
     * 保存记录到数据库
     *
     * @param type 1 IC卡开门，2 身份证开门，3 密码开门，  4 人脸开门
     * @param sn   ic卡，身份证开门时的卡号
     * @param sid  服务器上对应记录的id，上传记录图片时用
     */

    public void savaICOrIDCardRecord(final int type, final int sid, final String sn) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String picturePath = FileUtil.getAppRecordPicturePath(MainActivity.this);
                File file = new File(picturePath, new Date().getTime() + ".jpg");
                Utils.saveBitmap(file.getPath(), BitmapUtil.getViewBitmap(banner));

                OpenRecord record = new OpenRecord();
                record.setCreateTime(new Date().getTime());
                record.setImage(file.getPath());
                record.setSn(sn);
                record.setStatus(1);
                record.setUploadStatus(false);
                record.setSid(sid);
                record.setType(type);
                DbManager.getInstance().getDaoSession().getOpenRecordDao().insert(record);
            }
        }).start();


    }


    //设置定时闹钟任务，2：30：00 执行，上传开门记录
    private void setAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
//        Intent intent = new Intent();
        intent.setAction(KeyContacts.ACTION_TIMER_UPLOAD_OPENRECORD);
        pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, 2);
        ca.set(Calendar.MINUTE, 30);
        ca.set(Calendar.SECOND, 00);

        long interval = 24 * 60 * 60 * 1000;
        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, ca.getTimeInMillis(), interval, pi);

    }


    //初始化nfc串口
    public void startMeasuing() {
        LogUtil.w("SerialPort  startMeasuing");
        doorHandler = new MyHandler(this);

        serialHelper = new SerialHelper(sPort, iBaudRate) {
            @Override
            protected void onDataReceived(ComBean paramComBean) {
//                String str = bytesToHex(paramComBean.bRec);
                String str = parseCard(paramComBean);
                LogUtil.w("nfc 十六进制 = " + str);
//                BigInteger bi = new BigInteger(str, 16);//转十进制
//                str = bi.toString();
//                LogUtil.w("nfc 十进制 = " + str);

                //对比数据库，开门
                Message msg = Message.obtain();
                msg.what = 3;
                msg.obj = str;
//                msg.obj = "ic20190501";
                doorHandler.sendMessage(msg);
//                parseData(str);
            }
        };
    }


    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {

            String hex = Integer.toHexString(bytes[i] & 0xFF);
//            if (hex.length() < 2) {
//                sb.append(0);
//            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private String parseIC(byte[] bRec) {
        android.util.Log.d("wlDebug", " = " + ByteUtil.ByteArrToHex(bRec));
        byte[] cardData = new byte[4];
        cardData[0] = bRec[8];
        cardData[1] = bRec[7];
        cardData[2] = bRec[6];
        cardData[3] = bRec[5];
        String _str = ByteUtil.ByteArrToHex(cardData);
        BigInteger cardID = new BigInteger(_str, 16);
        android.util.Log.d("wlDebug", "_str = " + _str + "cardID = " + cardID.toString());

        return cardID.toString();
    }


    public String parseCard(ComBean comBean) {
        String cardID = "";
        if (comBean.bRec[1] == 0x08) {
            byte[] cardData = new byte[4];
            cardData[0] = comBean.bRec[8];
            cardData[1] = comBean.bRec[7];
            cardData[2] = comBean.bRec[6];
            cardData[3] = comBean.bRec[5];
            String _str = ByteUtil.ByteArrToHex(cardData);
            cardID = new BigInteger(_str, 16).toString();
        } else if (comBean.bRec[1] == 0x0c) {
            byte[] cardData = new byte[8];
            cardData[0] = comBean.bRec[12];
            cardData[1] = comBean.bRec[11];
            cardData[2] = comBean.bRec[10];
            cardData[3] = comBean.bRec[9];
            cardData[4] = comBean.bRec[8];
            cardData[5] = comBean.bRec[7];
            cardData[6] = comBean.bRec[6];
            cardData[7] = comBean.bRec[5];
            String _str = ByteUtil.ByteArrToHex(cardData);
            cardID = new BigInteger(_str, 16).toString();
        }
        return cardID;
    }


    private void parseData(String str) {
//        ToastUtil.showCustomToast(str);
        LogUtil.w("nfc数据 = " + str);
//        str = str.substring(0, 20);
        //ic卡
        ICCard iccard = DbManager.getInstance().getDaoSession().getICCardDao().queryBuilder()
                .where(ICCardDao.Properties.Sn.eq(str)).unique();
        if (iccard != null) {
            Message msg = Message.obtain();
            msg.what = 1;
            msg.arg1 = 1;
            msg.obj = str;
            doorHandler.sendMessage(msg);
            return;
        }

        IDCard idcard = DbManager.getInstance().getDaoSession().getIDCardDao().queryBuilder()
                .where(IDCardDao.Properties.Sn.eq(str)).unique();
        if (idcard != null) {
            Message msg = Message.obtain();
            msg.what = 1;
            msg.arg1 = 2;
            msg.obj = str;
            doorHandler.sendMessage(msg);
            return;
        }

        ToastUtil.showCustomToast("未注册的卡或无法识别的卡，请用已注册的ic卡或身份证开门");

    }


    public void stopMeasuing() {
        if (serialHelper != null && serialHelper.isOpen()) {
            serialHelper.close();
        }
    }

    public void open() {
        try {
            if (serialHelper != null)
                serialHelper.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        SimpleVideoPlayerManager.instance().resumeNiceVideoPlayer();
        if (banner != null)
            banner.startAutoPlay();

        open();
        onFaceResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SimpleVideoPlayerManager.instance().suspendNiceVideoPlayer();
        if (banner != null)
            banner.stopAutoPlay();

        stopMeasuing();
        onFacePause();
    }

    public class MyHandler extends Handler {
        private WeakReference<Activity> weakReference;

        public MyHandler(Activity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (weakReference.get() != null) {
                switch (msg.what) {
                    case 1:
                        // open door;
                        int type = msg.arg1;
                        String sn = (String) msg.obj;
                        Gpio.RelayOnOff(1);
                        uploadRecord(type, sn);
                        MyToast.showToast("开门成功", R.mipmap.icon_success, "#0ABA07");
                        doorHandler.removeMessages(2);
                        doorHandler.sendEmptyMessageDelayed(2, 5000);
                        break;
                    case 2:
                        // close door;
                        Gpio.RelayOnOff(0);
                        break;
                    case 3:
                        String str = (String) msg.obj;

                        parseData(str);
                        break;

                }
            }
        }
    }

    /**
     * 上传不带图片的记录
     *
     * @param type 1 IC卡开门，2 身份证开门，3 密码开门
     * @param sn   ic卡，身份证开门时的卡号, 密码开门临时密码，人脸开门user_id
     */

    private void uploadRecord(int type, String sn) {
        if (type == 1) {//IC卡开门
            presenter.uploadICCardRecord(type, sn);
        } else if (type == 2) {//身份证开门
            presenter.uploadIDCardRecord(type, sn);
        } else if (type == 3) {//密码开门
            presenter.uploadCodeRecord(type, sn);
        } else if (type == 4) {//人脸开门
            presenter.uploadFaceRecord(type, Integer.parseInt(sn));//sn对应user_id
        }
    }

    @Override
    public void uploadCardSuccess(int type, int id, String sn) {
        savaICOrIDCardRecord(type, id, sn);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void faceOpenSuccess(FaceSuccessEventBean bean) {
        Message msg = Message.obtain();
        msg.what = 1;
        msg.arg1 = 4;
        msg.obj = String.valueOf(bean.user_id);
        doorHandler.sendMessage(msg);
    }


    // -----------------------------------------------------------
    // For Face start..
    private SurfaceView sfv_draw_view; //人脸框绘画层
    public FaceSet faceSet = null; //sdk逻辑层
    public CameraView mIRCameraView; //红外摄像头
    public CameraView mCameraView; //RGB摄像头
    private byte[] irData; //ir视频流
    private int screenW = 480;//屏幕分辨率w
    private final Object lock = new Object();
    private Size ratio;//摄像头预览分辨率
    private float scale_bit = 1;
    private List<YMFace> ymFaces;

    public DemoConfig mConfig;
    // For Face end..

    private void onFaceResume() {
        userMap = UserDataUtil.updateDataSource(true);
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            //预览适配
                            mCameraView.setAdjustViewBounds(mConfig.isAdjustView);
                            //设置cameraId
                            mCameraView.setFacing(mConfig.cameraFacing);
                            //设置分辨率
                            mCameraView.setCameraResolution(mConfig.previewSizeWidth, mConfig.previewSizeHeight);
                            //设置预览方向
                            mConfig.cameraAngle = mConfig.cameraAngle == -1 ? mCameraView.getDisplayOrientation() : mConfig.cameraAngle;
                            mCameraView.setDisplayOrientation(mConfig.cameraAngle);
                            //横竖屏调整
                            mCameraView.adjustVertical(mConfig.screenrRotate90);
                            //开启相机
                            mCameraView.start();
                            mConfig.sdkAngle = mConfig.sdkAngle == -1 ? getSdkOrientation(mConfig.cameraFacing) : mConfig.sdkAngle;
                            //初始化算法sdk
                            FaceResult result = faceSet.startTrack(mConfig.sdkAngle);
                            showShortToast(getApplicationContext(), "code:" + result.code + "  " + result.msg);
                            //保存配置
                            SharedPrefUtils.putObject(getApplicationContext(), "DEMO_CONFIG", mConfig);


                            if (isDoubleEyes) openIRCamera();
                        } else {
                            // showLongToast(getApplicationContext(), "请同意软件的权限，才能继续使用");
                        }
                    }
                });
    }

    private void onFacePause() {
        faceSet.stopTrack();
        if (mCameraView != null) {
            mCameraView.stop();
        }
        if (mIRCameraView != null) {
            if (mIRCameraView.isCameraOpened()) {
                mIRCameraView.stop();
            }
        }
    }

    // 初始化人脸所需的UI;
    private void initFaceCamera() {
        initConfig();

        mCameraView = findViewById(R.id.camera);
        sfv_draw_view = findViewById(R.id.sfv_draw_view);
        sfv_draw_view.setZOrderOnTop(true);
        sfv_draw_view.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        //算法sdk
        faceSet = new FaceSet(this);
    }

    // 设置CameraPreviewFrame CallBack;
    protected void initFaceEvent() {
        if (mCameraView != null) {
            mCameraView.addCallback(new CameraView.Callback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    final byte[] mdata = data;
                    synchronized (lock) {
                        //调用sdk获取人脸集合
                        ymFaces = onCameraPreviewFrame(mdata, irData,
                                mCameraView.getCameraResolution().getWidth(), mCameraView.getCameraResolution().getHeight(), mConfig.isMulti);
                        //获取预览分辨率
                        ratio = mCameraView.getCameraResolution();
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            ratio = Size.inverse(ratio);
                        }
                        // android.util.Log.d("wlDebug", "ymFacesIsNull = " + (ymFaces == null));

                        //获取缩放比例
                        mConfig.screenZoon = mCameraView.getScale();
                        initDrawViewSize();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 显示/隐藏 Camera界面；
                                if (ymFaces != null && !isFaceViewShow) {
                                    isFaceViewShow = true;
                                    mCameraView.bringToFront();
                                    // if (mIRCameraView != null) mIRCameraView.bringToFront();
                                } else if (ymFaces == null && isFaceViewShow) {
                                    isFaceViewShow = false;
                                    banner.bringToFront();
                                    toolsBar.bringToFront();
                                }
                                // 绘画人脸框
                                drawView(ymFaces, mConfig, sfv_draw_view, scale_bit, mCameraView.getFacing(), "");
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * 相机帧回调
     *
     * @param bytes   rgb视频流
     * @param irBytes ir视频流
     * @param iw      camera分辨率
     * @param ih      camera分辨率
     * @param isMulti
     * @return
     */
    private byte[] mBytes;
    private byte[] mBytesIr;
    private int mWidth;
    private int mHeight;
    private boolean openFaceTrack = false;//追踪
    private boolean openFaceReco = true;//识别
    private boolean openFaceLiveness = false; //红外活体
    private boolean openFaceRgbLiveness = true;  //可见光活体
    private boolean openFaceBinoculareLiveness = false; //双目活体（可见光+红外）
    private Map<Integer, User> userMap;

    private boolean isFaceViewShow = false;
    private boolean isDoubleEyes = true;

    protected List<YMFace> onCameraPreviewFrame(final byte[] bytes, final byte[] irBytes, final int iw, final int ih, final boolean isMulti) {
        if (bytes == null) return null;
        mBytes = bytes;
        mBytesIr = irBytes;
        mWidth = iw;
        mHeight = ih;
        return faceSet.logic(bytes, irBytes, iw, ih, isMulti, openFaceTrack, openFaceReco, getLivenessType());
    }

    /**
     * 人脸绘画
     *
     * @param ymFaces
     * @param mConfig
     * @param draw_view 绘画view
     * @param scale_bit
     * @param cameraId  cameraId
     * @param fps
     */
    protected void drawView(List<YMFace> ymFaces, DemoConfig mConfig, SurfaceView draw_view, float scale_bit, int cameraId, String fps) {
        try {
            //打开配置界面时不做绘画处理
            /*
            if (fragment != null) {
                TrackDrawUtil.drawFaceTracking(null, null, draw_view, 0, 0, "", false, null);
                return;
            }
            */
            //当开启追踪时，不做识别与活体
            if (openFaceTrack) {
                TrackDrawUtil.drawFace(ymFaces, mConfig, draw_view, scale_bit, cameraId, fps, false);
                return;
            }
            TrackDrawUtil.drawFaceTracking(ymFaces, mConfig, draw_view, scale_bit, cameraId, fps, false, userMap);
        } catch (Exception e) {
            Log.d("wlDebug", "" + e);
        }
    }

    private void initConfig() {
        mConfig = SharedPrefUtils.getObject(ExApplication.getContext(), "DEMO_CONFIG", DemoConfig.class);
        if (mConfig == null) {
            mConfig = new DemoConfig();
            SharedPrefUtils.putObject(ExApplication.getContext(), "DEMO_CONFIG", mConfig);
        }
    }

    /**
     * 更新DrawView 大小
     */
    public void initDrawViewSize() {
        int viewW;
        int viewH;
        scale_bit = 1;
        if (mCameraView.getAdjustViewBounds()) {
            if (ratio.getWidth() <= screenW) {
                scale_bit = (screenW / (float) ratio.getWidth());
                viewW = (screenW);
            } else {
                viewW = (screenW);
                scale_bit = (screenW / (float) ratio.getWidth());
            }
            viewH = (int) (viewW / ratio.toFloat());
        } else {
            viewW = (int) (ratio.getWidth() * scale_bit);
            viewH = (int) (viewW / ratio.toFloat());
        }
        //设置sfv_draw_view 大小与cameraView预览一致
        /*
        sfv_draw_view.getLayoutParams().height = viewH;
        sfv_draw_view.getLayoutParams().width = viewW;
        sfv_draw_view.requestLayout();
        */
    }

    /**
     * 获取当前活体开启类型
     * 双目：0 可见光：1 红外：2 都不开启:-1
     *
     * @return
     */
    public int getLivenessType() {
//        return (openFaceBinoculareLiveness && !openFaceLiveness && !openFaceRgbLiveness) ? 0 : (!openFaceBinoculareLiveness && !openFaceLiveness
//                && openFaceRgbLiveness) ? 1 : (openFaceLiveness && !openFaceRgbLiveness && !openFaceBinoculareLiveness) ? 2 : -1;
        return (isDoubleEyes ? 0 : 1);
    }

    public void showShortToast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取sdk识别方向
     *
     * @param facing
     * @return
     */
    private int getSdkOrientation(int facing) {
        int sdkOrientation;
        /*
         * 横屏：
         * -前/后摄像头 sdkOrientation=0;
         * 竖屏：
         * -前摄像头 sdkOrientation=270;
         * -后摄像头 sdkOrientation=90;
         */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            sdkOrientation = 0;  //横屏
        else {
            if (facing == 0) sdkOrientation = 90;  //竖屏后置
            else sdkOrientation = 270;//竖屏前置
        }
        return 0;
    }


    /**
     * 开启IR摄像头
     */
    public void openIRCamera() {
        if (mIRCameraView == null) {
            mIRCameraView = findViewById(R.id.ir_camera);
            mIRCameraView.addCallback(new CameraView.Callback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    //将IR回调的视频流帧赋值给irData
                    irData = data;
                }
            });
        }
        int cameraFacing = mCameraView.getFacing();
        int facing = (cameraFacing == CameraView.FACING_FRONT ? CameraView.FACING_BACK : CameraView.FACING_FRONT);
        //设置摄像头
        // mIRCameraView.setVisibility(View.VISIBLE);
        //设置与可见光一样的分辨率
        Size size = mCameraView.getCameraResolution();
        mIRCameraView.start(facing, mConfig.screenIrZoon, size, true);
    }

    /**
     * 关闭IR摄像头
     */
    public void closeIRCamera() {
        if (mIRCameraView == null) return;
        if (mIRCameraView.isCameraOpened())
            mIRCameraView.stop();
        // mIRCameraView.setVisibility(View.GONE);
    }
}

