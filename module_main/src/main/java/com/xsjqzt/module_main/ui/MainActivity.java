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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMCallManager;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.jbb.library_common.basemvp.ActivityManager;
import com.jbb.library_common.basemvp.BaseMvpActivity;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.download.AppDownloadService;
import com.jbb.library_common.utils.BitmapUtil;
import com.jbb.library_common.utils.CommUtil;
import com.jbb.library_common.utils.DateFormateUtil;
import com.jbb.library_common.utils.DeviceUtil;
import com.jbb.library_common.utils.FileUtil;
import com.jbb.library_common.utils.GlideUtils;
import com.jbb.library_common.utils.MD5Util;
import com.jbb.library_common.utils.SharePreferensUtil;
import com.jbb.library_common.utils.StringUtil;
import com.jbb.library_common.utils.ToastUtil;
import com.jbb.library_common.utils.Utils;
import com.jbb.library_common.utils.compress.CompressImageUtil;
import com.jbb.library_common.utils.log.LogUtil;
import com.readsense.cameraview.camera.CameraView;
import com.readsense.cameraview.camera.Size;
import com.softwinner.Gpio;
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
import com.xsjqzt.module_main.greendao.OpenRecordDao;
import com.xsjqzt.module_main.greendao.entity.FaceImage;
import com.xsjqzt.module_main.greendao.entity.ICCard;
import com.xsjqzt.module_main.greendao.entity.IDCard;
import com.xsjqzt.module_main.greendao.entity.OpenCode;
import com.xsjqzt.module_main.greendao.entity.OpenRecord;
import com.xsjqzt.module_main.model.EntranceDetailsResBean;
import com.xsjqzt.module_main.model.VersionResBean;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.modle.BindCardSuccessEventBus;
import com.xsjqzt.module_main.modle.DownVideoSuccessEventBus;
import com.xsjqzt.module_main.modle.FaceResult;
import com.xsjqzt.module_main.modle.FaceSuccessEventBean;
import com.xsjqzt.module_main.modle.User;
import com.xsjqzt.module_main.presenter.MainPresenter;
import com.xsjqzt.module_main.receive.AlarmReceiver;
import com.xsjqzt.module_main.service.DownAllDataService;
import com.xsjqzt.module_main.service.HeartBeatService;
import com.xsjqzt.module_main.service.OpenRecordService;
import com.xsjqzt.module_main.util.CameraUtil;
import com.xsjqzt.module_main.util.MyToast;
import com.xsjqzt.module_main.util.SharedPrefUtils;
import com.xsjqzt.module_main.util.TrackDrawUtil;
import com.xsjqzt.module_main.view.MainView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
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

    private ImageView homebgIv;
    private TextView entranceDetailTv;
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
    private String sPort = "/dev/ttyS1";
    private int iBaudRate = 115200;

    //    private int mType;// 0 默认什么都没显示， 1 密码开锁布局显示，2 视频电话显示
    private MyHandler doorHandler;

    private boolean inputLayoutShow;//底部输入布局是否显示出来，显示出来后10秒无操作自动掩藏
    private long startShowTime;//记录底部布局显示的开始时间，10秒无操作自动掩藏
    //    private Timer inputLayoutShowTime;//10秒内检查操作定时器
//    private TimerTask inputLayoutShowTask;


    private boolean starEnterDown;// * 号被按了

    public static SoundPool mSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
//    private boolean haPermission;

    private EMMessageListener msgListener;//消息接收监听
    private EMCallStateChangeListener callStateChangeListener;//通话状态监听
    private AudioManager audioManager;
    private boolean hasCallSuccess;//拨打视频通话是否接通成功
    private boolean hasCallingVideo;//是否正在拨打视频通话
    private String remoteUserId;// 远程开门是对方userid
    private int callUserId = 0;//不通时，自动转呼一次，视频呼叫时第几个业主  0 第一个，1 第二个
    private String inputNum;//输入号码
    private EMCallManager.EMCallPushProvider pushProvider;
    private InutLayoutShowTimeRunnable inutLayoutShowTimeRunnable;//按键10秒误操作检查
    private InutLayoutShowTimeRunnable callRunnable, twoCallVideoRunnable;//第一个人拨号定时器，第二个人拨号计时器

    private boolean isCheckedCamera;

    int time8 = 800;//早上八点
    int time19 = 1900;//19点
    int time24 = 2400;//24点
    int lastVolume;//设备音量开关  0 默认白天，1 晚上
    private byte[] mPreviewBuffer;
    private boolean callSecond;//是否是呼叫的第二个人
    private long okClickTime;//记录上次点击ok健的时间戳
    private boolean isKeyEnterFirst;

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
        EventBus.getDefault().register(this);
        entranceDetailTv = findViewById(R.id.enterinfo_tv);
        homebgIv = findViewById(R.id.homebg_iv);
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
//        errorLayout = findViewById(R.id.error_layout);ƒ

        Drawable drawable = getResources().getDrawable(R.mipmap.icon_gth);
        drawable.setBounds(0, 0, CommUtil.dp2px(13), CommUtil.dp2px(13));
        bottomTv.setCompoundDrawables(drawable, null, null, null);


        banner = findViewById(R.id.banner);
        videoPlayer = findViewById(R.id.videoplayer);

        initData();

    }


    private void initData() {

        initView();
        registReceiver();
//        setAlarm();
        startMeasuing();
        initMusic();
//        test();
//        emLoginUser();

        loadDeviceInfo();
        loadBanner();


        initFaceCamera();
        initFaceEvent();
//        onFaceResume();

        if (deviceEnable()) {
            startService(new Intent(this, DownAllDataService.class));
            startService(new Intent(this, HeartBeatService.class));
        }

        String display_name = SharePreferensUtil.getString(KeyContacts.SP_KEY_DISPLAY_NAME, KeyContacts.SP_NAME_JPUSH);
        entranceDetailTv.setText(display_name);

        //延迟3秒检查版本是否需要更新
        entranceDetailTv.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkVersion();
            }
        }, 3000);
    }


    private void checkCameraEnable() {
        if (!isCheckedCamera) {
            isCheckedCamera = true;
            boolean cameraEnable = DeviceUtil.checkCameraEnable();
            if (!cameraEnable) {
                new AlertDialog.Builder(this)
                        .setTitle("提示！")
                        .setMessage("摄像头不可用或已损坏")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }
    }

    private void loadDeviceInfo() {
        presenter.entranceDetail();
    }


    /**
     * 初始化音频文件
     */
    private void initMusic() {
        //分别加入到SoundPool中
        mSound.load(this, R.raw.nobind_card, 1);// 1
        mSound.load(this, R.raw.open_success, 1);// 2
        mSound.load(this, R.raw.password_error, 1);// 3
        mSound.load(this, R.raw.noregist_face, 1);// 3
        mSound.load(this, R.raw.calling_video, 1);// 呼叫中
        mSound.load(this, R.raw.call_noreceive, 1);//业主未接听
        mSound.load(this, R.raw.call_switch, 1);// 切换拨号中
    }


    //打印环信登录状态
//    private void emLoginUser() {
//        String currentUser = EMClient.getInstance().getCurrentUser();
//        LogUtil.w("currentUser = " + currentUser);
//        ToastUtil.showCustomToast("currentUser = " + currentUser);
//        ToastUtil.showCustomToast("连接服务器：" + EMClient.getInstance().isConnected());
//        ToastUtil.showCustomToast("登录状态：" + EMClient.getInstance().isLoggedInBefore());
//    }

//    private void test() {
//        List<OpenRecord> records = DbManager.getInstance().getDaoSession().getOpenRecordDao()
//                .queryBuilder()
//                .where(OpenRecordDao.Properties.UploadStatus.eq(false))
//                .list();
//
//        List<FaceImage> faceImages = DbManager.getInstance().getDaoSession().getFaceImageDao().loadAll();
//        List<ICCard> icCards = DbManager.getInstance().getDaoSession().getICCardDao().loadAll();
//        List<IDCard> idCards = DbManager.getInstance().getDaoSession().getIDCardDao().loadAll();
//        List<OpenCode> openCodes = DbManager.getInstance().getDaoSession().getOpenCodeDao().loadAll();
//
//        String str = "";
//        for (FaceImage face : faceImages) {
//            str += face.getCode() + "\n";
//        }
//
//        StringBuffer sf = new StringBuffer();
//        sf.append("开门图片记录：" + records.size())
//                .append("\n")
//                .append("人脸注册：" + str)
//                .append("\n")
//                .append("IC卡数：" + icCards.size())
//                .append("\n")
//                .append("身份证数：" + idCards.size())
//                .append("\n")
//                .append("临时密码数：" + openCodes.size())
//                .append("\n")
//                .append("registrationId：" + JPushInterface.getRegistrationID(this));
//
//        new AlertDialog.Builder(this).setMessage(sf.toString()).show();
//
//    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public String getATitle() {
        return null;
    }

    private void initView() {
        showType = SharePreferensUtil.getInt(KeyContacts.SP_KEY_BANNER_OR_VIDEO, 1, KeyContacts.SP_NAME_USERINFO);

        if (showType == 1) {
            initImageAd();
        } else {
            initVideo();
        }
    }


    private void initImageAd() {
        String string = SharePreferensUtil.getString(KeyContacts.SP_KEY_BANNER_DATA, KeyContacts.SP_NAME_USERINFO);
        if (TextUtils.isEmpty(string)) {
            videoPlayer.setVisibility(View.GONE);
            banner.setVisibility(View.GONE);
            return;
        }
        List<String> images = JSON.parseArray(string, String.class);
        if (images == null || images.size() < 1) {
            videoPlayer.setVisibility(View.GONE);
            banner.setVisibility(View.GONE);
            return;
        }

        videoPlayer.setVisibility(View.GONE);
        banner.setVisibility(View.VISIBLE);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
//        banner.setImages(getImages());

        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    private void initVideo() {
        String string = SharePreferensUtil.getString(KeyContacts.SP_KEY_VIDEO_DATA, KeyContacts.SP_NAME_USERINFO);
        if (TextUtils.isEmpty(string)) {
            videoPlayer.setVisibility(View.GONE);
            banner.setVisibility(View.GONE);
            return;
        }
        List<String> videos = JSON.parseArray(string, String.class);
        if (videos == null || videos.size() < 1) {
            videoPlayer.setVisibility(View.GONE);
            banner.setVisibility(View.GONE);
            return;
        }

        banner.setVisibility(View.GONE);
        videoPlayer.setVisibility(View.VISIBLE);

//        AssetFileDescriptor assetFileDescriptor = getAssets().openFd("ad_movice.mp4");
//        Uri mUri = Uri.parse("android.resource://" + getPackageName() + "/"+ R.raw.ad_movice);
        String path = FileUtil.getAppVideoPath(this);
        path = path + File.separator + "123.mp4";

        videoPlayer.setUp(videos.get(0), null);//设置地址
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

//        new AlertDialog.Builder(this)
//                .setCancelable(true)
//                .setMessage("确定清空人脸库？")
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        CameraUtil.clearAllFace(faceSet);//删除阅面人脸
////                        DbManager.getInstance().getDaoSession().getFaceImageDao().getDatabase().execSQL("delete from FACE_IMAGE");//删除数据库中人脸记录数据
//                    }
//                }).show();
    }


    public void btn5Click(View view) {
        int faceSize = CameraUtil.getFaceSize(faceSet);

        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setMessage("人脸数量 ： " + faceSize)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && isKeyEnterFirst) {
            if (hasCallingVideo) {
                endCall();
                faceOnResuse();
                LogUtil.w("keycode : " + "# 号挂断");
            } else {
                LogUtil.w("keycode : " + "# 号拨号");
                inputNum = roomNumEt.getText().toString().trim();
                if (!TextUtils.isEmpty(inputNum)) {
                    checkInput(inputNum);
                }else {//当输入框为空时，监听是否是快速双击，双击是呼叫管理处
                    if ((System.currentTimeMillis() - okClickTime) < 500) {//双击ok健
                        ToastUtil.showCustomToast("双击enter键");
                    }
                    okClickTime = System.currentTimeMillis();
                }
            }

            isKeyEnterFirst = false;
            return true;
        }
        isKeyEnterFirst = true;
        return super.dispatchKeyEvent(event);
    }


    private boolean isShiftClick;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT || keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT) {
            isShiftClick = true;
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            String oldNum = roomNumEt.getText().toString().trim();
            if (!TextUtils.isEmpty(oldNum) && inputLayoutShow && !hasCallingVideo) {//输入布局显示，并且输入不为空，清空输入
                roomNumEt.setText("");
            } else if (TextUtils.isEmpty(oldNum) && inputLayoutShow && !hasCallingVideo) {//输入布局显示，并且输入为空，隐藏键盘
                hideRoomInputLayout();
                starEnterDown = false;
            } else if(!hasCallingVideo) {// 显示输入布局，并标记为* 组合输入
                roomNumEt.setText("");
                showRoomNumOpen();
                starEnterDown = true;
            }

            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {

            return true;
        } else if (keyCode == KeyEvent.KEYCODE_0) {
            if (!hasCallingVideo) {
                showRoomNumOpen();
                String oldNum = roomNumEt.getText().toString().trim();
                setInputData(oldNum, "0");
                isShiftClick = false;
            }

        } else if (keyCode == KeyEvent.KEYCODE_1) {
            if (!hasCallingVideo) {
                showRoomNumOpen();
                String oldNum = roomNumEt.getText().toString().trim();
                setInputData(oldNum, "1");
                isShiftClick = false;
            }

        } else if (keyCode == KeyEvent.KEYCODE_2) {
            if (!hasCallingVideo) {
                showRoomNumOpen();
                String oldNum = roomNumEt.getText().toString().trim();
                setInputData(oldNum, "2");
                isShiftClick = false;
            }

        } else if (keyCode == KeyEvent.KEYCODE_3) {
            if (isShiftClick) {// # 号
                isShiftClick = false;
                if (hasCallingVideo) {
                    endCall();
                    faceOnResuse();
                    LogUtil.w("keycode : " + "# 号挂断");
                } else {
                    LogUtil.w("keycode : " + "# 号拨号");
                    inputNum = roomNumEt.getText().toString().trim();
                    if (!TextUtils.isEmpty(inputNum))
                        checkInput(inputNum);
                    else {//当输入框为空时，监听是否是快速双击，双击是呼叫管理处
                        if ((System.currentTimeMillis() - okClickTime) < 500) {//双击ok健
                            ToastUtil.showCustomToast("双击enter键");
                        }
                        okClickTime = System.currentTimeMillis();
                    }
                }

            } else {
                if (!hasCallingVideo) {
                    LogUtil.w("keycode : " + "拨号 3");
                    showRoomNumOpen();
                    String oldNum = roomNumEt.getText().toString().trim();
                    setInputData(oldNum, "3");
                    isShiftClick = false;
                }
            }

        } else if (keyCode == KeyEvent.KEYCODE_4) {
            if (!hasCallingVideo) {
                showRoomNumOpen();
                String oldNum = roomNumEt.getText().toString().trim();
                setInputData(oldNum, "4");
                isShiftClick = false;
            }

        } else if (keyCode == KeyEvent.KEYCODE_5) {
            if (!hasCallingVideo) {
                showRoomNumOpen();
                String oldNum = roomNumEt.getText().toString().trim();
                setInputData(oldNum, "5");
                isShiftClick = false;
            }

        } else if (keyCode == KeyEvent.KEYCODE_6) {
            if (!hasCallingVideo) {
                showRoomNumOpen();
                String oldNum = roomNumEt.getText().toString().trim();
                setInputData(oldNum, "6");
                isShiftClick = false;
            }

        } else if (keyCode == KeyEvent.KEYCODE_7) {
            if (!hasCallingVideo) {
                showRoomNumOpen();
                String oldNum = roomNumEt.getText().toString().trim();
                setInputData(oldNum, "7");
                isShiftClick = false;
            }

        } else if (keyCode == KeyEvent.KEYCODE_8) {
            if (!hasCallingVideo) {
                if (isShiftClick) {// * 号
                    roomNumEt.setText("");
                    showRoomNumOpen();
                    starEnterDown = true;
                } else {
                    showRoomNumOpen();
                    String oldNum = roomNumEt.getText().toString().trim();
                    setInputData(oldNum, "8");
                }
                isShiftClick = false;
            }

        } else if (keyCode == KeyEvent.KEYCODE_9) {
            if (!hasCallingVideo) {
                showRoomNumOpen();
                String oldNum = roomNumEt.getText().toString().trim();
                setInputData(oldNum, "9");
                isShiftClick = false;
            }

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
        String registrationID = JPushInterface.getRegistrationID(this);
        if (!TextUtils.isEmpty(registrationID)) {
            presenter.registrationId(registrationID);
        }
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

//    private List<String> getImages() {
//        List<String> images = new ArrayList<>();
//        images.add("http://i1.mopimg.cn/img/dzh/2015-05/1288/20150502115717123.jpg");
//        images.add("http://i1.mopimg.cn/img/dzh/2015-05/855/2015050211571275.jpg");
//        images.add("http://i1.mopimg.cn/img/dzh/2015-05/389/20150502115712989.jpg");
//        return images;
//    }


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

        stopService(new Intent(this, HeartBeatService.class));
    }

    @Override
    public MainPresenter initPresenter() {
        return new MainPresenter();
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
                } else if (code == 2004) {//设备未绑定或不存在
                    clearDeviceData();
                } else if (code == 2005) {//设备禁用
                    disableDevice();
                }
            } else if (intent.getAction() == ConnectivityManager.CONNECTIVITY_ACTION) {
                //监听网络变化
                if (Utils.getNetWorkState(MainActivity.this)) {
                    LogUtil.w("NetWorkState = " + true);
                    login();
                    startService(new Intent(MainActivity.this, DownAllDataService.class));
                    startService(new Intent(MainActivity.this, HeartBeatService.class));

                    checkVersion();
                    loadDeviceInfo();
                }
                //如果监听程序没有运行，则启动监听app
                if (!DeviceUtil.isRunBackground(MainActivity.this, "com.test.monitor_appinstall")) {
                    Intent startIntent = getPackageManager().getLaunchIntentForPackage("com.test.monitor_appinstall");
                    if (startIntent != null) {
                        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startIntent);
                    }
                }
            } else if (intent.getAction() == KeyContacts.ACTION_RECEICE_NOTITY) {
                handleNotity(intent.getExtras());
            }
        }
    }

    private void handleNotity(final Bundle bundle) {
        if (bundle == null) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
                    JSONObject json = new JSONObject(extras);
                    int type = json.optInt("type");

                    if (type == 1) {//更新身份证
                        downIDCardData();
                        EventBus.getDefault().post(new BindCardSuccessEventBus());
                    } else if (type == 2) {//更新ic卡信息
                        downICCardData();
                        EventBus.getDefault().post(new BindCardSuccessEventBus());
                    } else if (type == 3) {//临时密码
                        downOpenCode();
                    } else if (type == 4) {//下载人脸图片，并注册到阅面的人脸库，将注册状态发送给后台服务器
                        downFaceImage();
                    } else if (type == 100) {//设备重启
                        rebootDevice();
                    } else if (type == 101) {//更新设备状态
                        loadDeviceInfo();
                    } else if (type == 102) {//设置音量
                        int volume = json.getInt("volume");
                        int volume_night = json.getInt("volume_night");
                        SharePreferensUtil.putInt(KeyContacts.SP_KEY_VOLUME, volume, KeyContacts.SP_NAME_JPUSH);
                        SharePreferensUtil.putInt(KeyContacts.SP_KEY_VOLUME_NIGHT, volume_night, KeyContacts.SP_NAME_JPUSH);
                        setVoice();
                    } else if (type == 104) {//开锁
                        openDoor();
                    } else if (type == 105) {//app有更新，检查更新
                        checkVersion();
                    } else if (type == 107) {
                        loadDeviceInfo();
                    }
                } catch (Exception e) {

                }
            }
        }).start();


    }

    private void rebootDevice() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showCustomToast("即将重启");

                doorHandler.postDelayed(new Runnable() {
                    public void run() {
                        DeviceUtil.rebootDevice();
                    }
                }, 1000);
            }
        });

    }

    private void checkVersion() {
        presenter.checkVersion(CommUtil.getVersionName());
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

    //设置音量，每次拨报前检查是白天还是夜间音量
    private void setVoice() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String nowHouse = new SimpleDateFormat("HHmm").format(System.currentTimeMillis());
                int now = Integer.parseInt(nowHouse);
                int isNight = 0;
                if ((now > time19 && now < time24) || (now > 0 && now < time8)) {//夜间音量
                    isNight = 1;
                } else {               //白天音量
                    isNight = 0;
                }
                lastVolume = isNight;
                float volume = lastVolume == 0 ? SharePreferensUtil.getInt(KeyContacts.SP_KEY_VOLUME, 100, KeyContacts.SP_NAME_JPUSH) : SharePreferensUtil.getInt(KeyContacts.SP_KEY_VOLUME_NIGHT, 0, KeyContacts.SP_NAME_JPUSH);

                //设置音量
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int maxSyetem = (int) (volume / 100 * audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
                int maxMusic = (int) (volume / 100 * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, maxSyetem, AudioManager.FLAG_SHOW_UI);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxMusic, AudioManager.FLAG_SHOW_UI);

                //通知服务器设置成功
                presenter.setVoice((int) volume);
            }
        });

    }

    //设置音量，每次拨报前检查是白天还是夜间音量
    private void checkVoice() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String nowHouse = new SimpleDateFormat("HHmm").format(System.currentTimeMillis());
                int now = Integer.parseInt(nowHouse);

                int isNight = 0;
                if ((now > time19 && now < time24) || (now > 0 && now < time8)) {//夜间音量
                    isNight = 1;
                } else {               //白天音量
                    isNight = 0;
                }
                if (lastVolume == isNight) {
                    return;
                }
                lastVolume = isNight;

                float volume = lastVolume == 0 ? SharePreferensUtil.getInt(KeyContacts.SP_KEY_VOLUME, 100, KeyContacts.SP_NAME_JPUSH) : SharePreferensUtil.getInt(KeyContacts.SP_KEY_VOLUME_NIGHT, 0, KeyContacts.SP_NAME_JPUSH);

                //设置音量
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int maxSyetem = (int) (volume / 100 * audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
                int maxMusic = (int) (volume / 100 * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, maxSyetem, AudioManager.FLAG_SHOW_UI);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxMusic, AudioManager.FLAG_SHOW_UI);

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

    //获取banner广告
    private void loadBanner() {
        long update_time = SharePreferensUtil.getLong(KeyContacts.SP_KEY_BANNER_UPDATE_TIME, 0, KeyContacts.SP_NAME_USERINFO);
        presenter.loadBanner(this, update_time);
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
        if (!DeviceUtil.isNetWorkEnable()) {
            ToastUtil.showCustomToast("设备未联网，暂不支持呼叫");
            return;
        }

        if (!deviceEnable()) {
            showEnableToast();
            return;
        }

        hideRoomInputLayout();
        if (starEnterDown) {//按了 * 号了，组合键，调起注册等页面

            if ("000".equals(inputNum)) {
                goTo(SystemInfoActivity.class);
            } else if ("101".equals(inputNum)) {
                goTo(RegistICCardActivity.class);
            } else if ("102".equals(inputNum)) {
                Bundle bundle = new Bundle();
                bundle.putInt("mType", 1);
                goTo(RegistICCardActivity.class, bundle);
            } else {
                ToastUtil.showCustomToast("输入错误");
                faceOnResuse();
            }
        } else {
            if (inputNum.length() == 5) {//密码开门
                //请求接口验证密码是否正确，是就开门
                OpenCode openCode = DbManager.getInstance().getDaoSession().getOpenCodeDao().queryBuilder()
                        .where(OpenCodeDao.Properties.Code.eq(inputNum)).unique();

                if (openCode != null) {
                    int expiry_time = openCode.getExpiry_time();
                    long now = System.currentTimeMillis();
                    if (expiry_time < (now / 1000)) {//过期了
                        setShowSucOrError(false, inputNum);
                    } else {
                        setShowSucOrError(true, inputNum);
                        DbManager.getInstance().getDaoSession().getOpenCodeDao().delete(openCode);
                    }
                } else {
                    setShowSucOrError(false, inputNum);
                }
                faceOnResuse();
            } else if (inputNum.length() == 4 || inputNum.length() == 6) {

                hasCallingVideo = true;

                callUserId = 0;//清零
                //根据房号获取userid，再拨视频通话
                presenter.getUseridByRoom(inputNum, callUserId);
                startMusic(5);//呼叫中语音

                //开始计时第一个人
                startCallSuccessTime();
            } else {
                ToastUtil.showCustomToast("请输入正确的房间号或者临时密码");
                faceOnResuse();
            }

        }
        starEnterDown = false;
    }


    //密码开门显示结果

    private void setShowSucOrError(boolean success, String code) {
//        hideRoomInputLayout();
        if (success) {//开门成功

            Message msg = Message.obtain();
            msg.what = 1;
            msg.arg1 = 3;
            msg.obj = code;
            doorHandler.sendMessage(msg);
        } else {
            startMusic(3);
            MyToast.showToast("密码错误", R.mipmap.icon_error, "#FF0000");

        }

    }


    //================================= 视频通话 =========================================================
    //拨打视频通话
    private void callVideo(String userId, String roomNum) {

        showCallVideoLayout();
        callNumTv.setText(roomNum);

//        onFacePause();
//        hideFaceLayout();

        setPushProviderAndListeren();//设置不在线时发送离线通知
        try {//单参数
            EMClient.getInstance().callManager().makeVideoCall(userId, UserInfoInstance.getInstance().getDoor());

        } catch (final EMServiceNotReadyException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                public void run() {
//                    String st2 = e.getMessage();
//                    if (e.getErrorCode() == EMError.CALL_REMOTE_OFFLINE) {
//                        st2 = getResources().getString(R.string.The_other_is_not_online);
//                        startMusic(6);//对方未接听
//                    } else if (e.getErrorCode() == EMError.USER_NOT_LOGIN) {
//                        st2 = getResources().getString(R.string.Is_not_yet_connected_to_the_server);
//                    } else if (e.getErrorCode() == EMError.INVALID_USER_NAME) {
//                        st2 = getResources().getString(R.string.illegal_user_name);
//                    } else if (e.getErrorCode() == EMError.CALL_BUSY) {
//                        st2 = getResources().getString(R.string.The_other_is_on_the_phone);
//                        startMusic(6);//对方未接听
//                    } else if (e.getErrorCode() == EMError.NETWORK_ERROR) {
//                        st2 = getResources().getString(R.string.can_not_connect_chat_server_connection);
//                    }
//                    Toast.makeText(MainActivity.this, st2, Toast.LENGTH_SHORT).show();

//                    endCall();
//                    faceOnResuse();
                }
            });
        }


    }

    private void setPushProviderAndListeren() {
        EMClient.getInstance().callManager().getCallOptions().setIsSendPushIfOffline(true);
        pushProvider = new EMCallManager.EMCallPushProvider() {

            void updateMessageText(final EMMessage oldMsg, final String to) {
                // update local message text
                EMConversation conv = EMClient.getInstance().chatManager().getConversation(oldMsg.getTo());
                conv.removeMessage(oldMsg.getMsgId());
            }

            @Override
            public void onRemoteOffline(final String to) {
                LogUtil.d("onRemoteOffline, to:" + to);

                final EMMessage message = EMMessage.createTxtSendMessage("您有一个视频电话呼入，请及时查看", to);
                message.setAttribute("em_apns_ext", true);
                message.setAttribute("is_voice_call", false);
                message.setMessageStatusCallback(new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        updateMessageText(message, to);
                    }

                    @Override
                    public void onError(int code, String error) {
                        updateMessageText(message, to);
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }
                });
                // send messages
                EMClient.getInstance().chatManager().sendMessage(message);
            }
        };

        EMClient.getInstance().callManager().setPushProvider(pushProvider);


        callStateChangeListener = new EMCallStateChangeListener() {
            @Override
            public void onCallStateChanged(CallState callState, CallError error) {
                switch (callState) {
                    case CONNECTING: // 正在连接对方
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ToastUtil.showCustomToast("正在连接对方");
                                callStatusTv.setText("呼叫中，请稍候...");
                            }
                        });

                        break;
                    case CONNECTED: // 双方已经建立连接
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ToastUtil.showCustomToast("双方已经建立连接");
                                callStatusTv.setText("呼叫中，请稍候...");
                            }
                        });
                        break;

                    case ACCEPTED: // 电话接通成功
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ToastUtil.showCustomToast("电话接通成功");
                                startTimer();
                                openSpeakerOn();
                                hasCallSuccess = true;
                                EMClient.getInstance().callManager().switchCamera();

                                removeCallCheck();
                            }
                        });
                        break;
                    case DISCONNECTED: // 电话断了

                        final CallError fError = error;

                        runOnUiThread(new Runnable() {
                            public void run() {
//                                ToastUtil.showCustomToast("电话断了");

//                                endCall();
//                                faceOnResuse();
//                                showFaceLayout();

//                                String s1 = getResources().getString(com.hyphenate.easeui.R.string.The_other_party_refused_to_accept);
//                                String s2 = getResources().getString(com.hyphenate.easeui.R.string.Connection_failure);
//                                String s3 = getResources().getString(com.hyphenate.easeui.R.string.The_other_party_is_not_online);
//                                String s4 = getResources().getString(com.hyphenate.easeui.R.string.The_other_is_on_the_phone_please);
//                                String s5 = getResources().getString(com.hyphenate.easeui.R.string.The_other_party_did_not_answer);

//                                String error = null;
                                startMusic(6);//对方未接听
                                if (fError == CallError.REJECTED || fError == CallError.ERROR_UNAVAILABLE
                                    || fError == CallError.ERROR_BUSY || fError == CallError.ERROR_NORESPONSE) {
//                                    error = s1;
                                    endCall();

                                    if (!callSecond) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                callSecond = true;

                                                startMusic(7);//转接第二个人语音
                                                hasCallingVideo = true;
                                                presenter.getUseridByRoom(inputNum, callUserId);
                                                startTwoCallVideoTime();

//                                                if (callRunnable != null && doorHandler != null) {
//                                                    doorHandler.removeCallbacks(callRunnable);
//                                                    callRunnable = null;
//                                                }
                                            }
                                        }, 1500);
                                    } else {
                                        faceOnResuse();
                                    }

                                } else {
//                                    String errorStr = "无法接通";
//                                    ToastUtil.showCustomToast(errorStr);
                                    endCall();
                                    faceOnResuse();
                                }

//                                else if (fError == CallError.ERROR_TRANSPORT) {
//                                    error = s2;
//                                } else if (fError == CallError.ERROR_UNAVAILABLE) {
//                                    error = s3;
//                                } else if (fError == CallError.ERROR_BUSY) {
//                                    error = s4;
//                                    startMusic(6);//对方未接听
//                                } else if (fError == CallError.ERROR_NORESPONSE) {
//                                    error = s5;
//                                    startMusic(6);//对方未接听
//                                } else if (fError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED || fError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED) {
//                                    error = getResources().getString(com.hyphenate.easeui.R.string.call_version_inconsistent);
//
//                                }

//                                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();

                            }
                        });
                        break;
                    case NETWORK_UNSTABLE: //网络不稳定
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ToastUtil.showCustomToast("网络不稳定");
                            }
                        });
                        break;
                    case NETWORK_NORMAL: //网络恢复正常
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ToastUtil.showCustomToast("网络恢复正常");
                            }
                        });
                        break;
                    default:
                        break;
                }

            }
        };
        EMClient.getInstance().callManager().addCallStateChangeListener(callStateChangeListener);

        msgListener = new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> messages) {

                //收到消息
                Message msg = Message.obtain();
                msg.what = 1;
                msg.arg1 = 5;
                msg.obj = remoteUserId;
                doorHandler.sendMessage(msg);

                //开门后自动挂断
                doorHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        endCall();
                        faceOnResuse();
                    }
                }, 10 * 1000);
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
                //收到已读回执
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
                //收到已送达回执
            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {
                //消息被撤回
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };

        EMClient.getInstance().chatManager().addMessageListener(msgListener);

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
            duration = 0;
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
                duration += delayMillis;
                callStatusTv.setText("通话中 " + DurationFormatUtils.formatDuration(duration, mTimePattern));
                timeHandler.postDelayed(timeRunnable, delayMillis);
            }
        }
    }

    public void endCall() {
        try {
            if (callStateChangeListener != null) {
                EMClient.getInstance().callManager().removeCallStateChangeListener(callStateChangeListener);
                callStateChangeListener = null;
                EMClient.getInstance().callManager().endCall();
            }
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
        }
        if (pushProvider != null) {
            EMClient.getInstance().callManager().setPushProvider(null);
            pushProvider = null;
        }
        if (msgListener != null) {
            EMClient.getInstance().chatManager().removeMessageListener(msgListener);
            msgListener = null;
        }
        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setMicrophoneMute(false);
            audioManager = null;
        }


        removeTask();
        removeCallCheck();
        hideCallVideoLayout();

        hasCallingVideo = false;
        hasCallSuccess = false;


    }

    //开启免提
    protected void openSpeakerOn() {
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        try {
            if (!audioManager.isSpeakerphoneOn())
                audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //=========================================================

    public void showAnim(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.callview_in);
        roomNumLayout.startAnimation(animation);
    }

    public void dismissAnim(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.callview_out);
        view.startAnimation(animation);
    }


    private void setInputData(String oldNum, String inputNum) {
        roomNumEt.setText(oldNum + inputNum);
    }


    //开启任务检查布局在十秒内是否有键盘操作，没有就掩藏底部输入布局
    private void startShowLayoutTime() {
        if (inutLayoutShowTimeRunnable != null && inutLayoutShowTimeRunnable != null) {
            doorHandler.removeCallbacks(inutLayoutShowTimeRunnable);
        }
        if (inutLayoutShowTimeRunnable == null) {
            inutLayoutShowTimeRunnable = new InutLayoutShowTimeRunnable(this, 1);
        }

        doorHandler.postDelayed(inutLayoutShowTimeRunnable, 10000);
    }

    //拨打第一个人，拨打视频通话，30秒计时，如果超过没接通，就中断，拨打第二个人
    private int callVideoTimeOut = 30 * 1000;
    private void startCallSuccessTime() {//拨打第一个人计时器
        if (inutLayoutShowTimeRunnable != null && doorHandler != null) {
            doorHandler.removeCallbacks(inutLayoutShowTimeRunnable);
            inutLayoutShowTimeRunnable = null;
        }
        if (callRunnable != null) {
            doorHandler.removeCallbacks(callRunnable);
        }
        if (callRunnable == null) {
            callRunnable = new InutLayoutShowTimeRunnable(this, 2);
        }

        doorHandler.postDelayed(callRunnable, callVideoTimeOut);
    }


    //切换业主第二次拨号时间检查
    private void startTwoCallVideoTime() {
        if (callRunnable != null && doorHandler != null) {
            doorHandler.removeCallbacks(callRunnable);
            callRunnable = null;
        }
        twoCallVideoRunnable = new InutLayoutShowTimeRunnable(this, 3);

        doorHandler.postDelayed(twoCallVideoRunnable, callVideoTimeOut);

    }

    private void removeCallCheck() {
        if (inutLayoutShowTimeRunnable != null && doorHandler != null) {
            doorHandler.removeCallbacks(inutLayoutShowTimeRunnable);
            inutLayoutShowTimeRunnable = null;
        }
        if (callRunnable != null && doorHandler != null) {
            doorHandler.removeCallbacks(callRunnable);
            callRunnable = null;
        }
        if (twoCallVideoRunnable != null && doorHandler != null) {
            doorHandler.removeCallbacks(twoCallVideoRunnable);
            twoCallVideoRunnable = null;
        }

    }


    public class InutLayoutShowTimeRunnable implements Runnable {
        WeakReference<Activity> weakReference = null;
        int type;// 1 按键10秒，2为拨号15秒，定时器

        public InutLayoutShowTimeRunnable(Activity activity, int type) {
            weakReference = new WeakReference<>(activity);
            this.type = type;
        }

        @Override
        public void run() {
            if (weakReference.get() != null) {
                if (type == 1) {
                    long endTime = System.currentTimeMillis();
                    if (endTime - startShowTime >= 10000) {//十秒未操作关闭输入框
                        hideRoomInputLayout();
                        faceOnResuse();
                    }
                } else if (type == 2) {
                    if (!hasCallSuccess) {
                        endCall();

                        startMusic(7);//转接第二个人语音
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hasCallingVideo = true;
                                presenter.getUseridByRoom(inputNum, callUserId);
                                startTwoCallVideoTime();

                                callSecond = true;
                            }
                        }, 1500);
                    }
                } else if (type == 3) {
                    if (!hasCallSuccess) {
                        endCall();
                        faceOnResuse();
                        ToastUtil.showCustomToast("对方不在线或未接听");
                    }
                }
            }
        }
    }


    //显示密码输入框开门ui
    private void showRoomNumOpen() {
        hideAndStopFace();//输入时禁止识别

        startShowTime = System.currentTimeMillis();
        startShowLayoutTime();
        if (!inputLayoutShow) {
            roomNumLayout.setVisibility(View.VISIBLE);
//            showAnim(roomNumLayout);

            callVideoLayout.setVisibility(View.GONE);
            inputLayoutShow = true;
        }

    }

    //掩藏底部输入布局
    private void hideRoomInputLayout() {
        roomNumLayout.setVisibility(View.GONE);
        roomNumEt.setText("");
        inputLayoutShow = false;
    }

    //单独显示视频通话ui
    private void showCallVideoLayout() {
        roomNumLayout.setVisibility(View.GONE);
        callVideoLayout.setVisibility(View.VISIBLE);
    }

    //单独掩藏视频通话ui
    private void hideCallVideoLayout() {
        callVideoLayout.setVisibility(View.GONE);
        callStatusTv.setText("呼叫中，请稍候...");
        callNumTv.setText("");
    }

    //单独掩藏密码开门ui
//    private void hideRoomNumLayout() {
//        roomNumLayout.setVisibility(View.GONE);
//        dismissAnim(roomNumLayout);
//        mType = 0;
//    }


    /**
     * 保存记录到数据库
     *
     * @param type 1 IC卡开门，2 身份证开门，3 密码开门，  4 人脸开门
     * @param sn   ic卡，身份证开门时的卡号
     * @param sid  服务器上对应记录的id，上传记录图片时用
     */

    public void savaICOrIDCardRecord(final int type, final int sid, final String sn, final String imagePath) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                OpenRecord record = new OpenRecord();
                record.setCreateTime(new Date().getTime());
                record.setImage(imagePath);
                record.setSn(sn);
                record.setStatus(1);
                record.setUploadStatus(false);
                record.setSid(sid);
                record.setType(type);
                DbManager.getInstance().getDaoSession().getOpenRecordDao().insert(record);

                Intent in = new Intent(MainActivity.this, OpenRecordService.class);
                startService(in);
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
        ca.set(Calendar.HOUR_OF_DAY, 02);
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

                if (!deviceEnable()) {
                    showEnableToast();
                    return;
                }

                String str = parseCard(paramComBean);
                LogUtil.w("nfc 十六进制 = " + str);

                //对比数据库，开门
                Message msg = Message.obtain();
                msg.what = 3;
                msg.obj = str;
//                msg.obj = "ic20190501";
                doorHandler.sendMessage(msg);

            }
        };
    }

    private void toast(final byte[] cardData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String _str = ByteUtil.ByteArrToHex(cardData);
                String cardID = new BigInteger(_str, 16).toString();
                ToastUtil.showCustomToast(cardID);
            }
        });

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
        startMusic(1);
        MyToast.showToast("卡号未绑定", R.mipmap.icon_error, "#FF0000");
//        ToastUtil.showCustomToast("未注册的卡或无法识别的卡，请用已注册的ic卡或身份证开门");

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
        endCall();
    }

    long lastOpenTime = 0;

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

                        openDoor();
                        isFaceSuccess = true;//表示开门成功，此时获取一帧开门图片

                        uploadRecord(type, sn);
                        startMusic(2);
                        if (type != 4)
                            MyToast.showToast("开门成功", R.mipmap.icon_success, "#0ABA07");
//                        doorHandler.removeMessages(2);
//                        doorHandler.sendEmptyMessageDelayed(2, 5000);
                        break;
                    case 2:
                        // close door;

//                        Gpio.setPull('0', 4, 1);
//                        Gpio.setMulSel('O', 4, 1);//0 做为输入，1做为输出
//                        Gpio.writeGpio('O', 4, 0);
//                        isFacePause = false;
                        break;
                    case 3:
                        String str = (String) msg.obj;

                        parseData(str);
                        break;

                }
            }
        }
    }


    public void openDoor() {
        Gpio.setPull('0', 4, 1);
        Gpio.setMulSel('O', 4, 1);//0 做为输入，1做为输出
        Gpio.writeGpio('O', 4, 1);
    }


    /**
     * 播放MP3资源
     *
     * @param resId 资源ID
     */
    private void startMusic(int resId) {
        /**
         * 第一个参数为播放音频ID
         * 第二个 第三个为音量
         * 第四个为优先级
         * 第五个为是否循环播放
         * 第六个设置播放速度
         * 返回值 不为0即代表成功
         */

        checkVoice();
        int type = mSound.play(resId, 1, 1, 1, 0, 1);
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
        } else if (type == 5) {//远程开门
            presenter.uploadRemoteRecord(type, Integer.parseInt(sn));//sn对应远处开门的user_id
        }

    }

    @Override
    public void uploadCardSuccess(int type, int id, String sn) {
        getPicture(type, id, sn);
    }

    @Override
    public void entranceDetailSuccess(EntranceDetailsResBean bean) {
        if (bean != null && bean.getData() != null) {
            int status = bean.getData().getStatus();
            if (status == 1) {

            }
            SharePreferensUtil.putBoolean(KeyContacts.SP_KEY_DEVICE_ENABLE, true, KeyContacts.SP_NAME_JPUSH);

            int volume = bean.getData().getVolume();
            int volume_night = bean.getData().getVolume_night();
            SharePreferensUtil.putInt(KeyContacts.SP_KEY_VOLUME, volume, KeyContacts.SP_NAME_JPUSH);
            SharePreferensUtil.putInt(KeyContacts.SP_KEY_VOLUME_NIGHT, volume_night, KeyContacts.SP_NAME_JPUSH);


            String name = bean.getData().getName();
            UserInfoInstance.getInstance().setDoor(name);

//            entranceDetailTv.setText(garden_name + "/" + region_name + "/" + building_name + "/" + name);
            String display_name = bean.getData().getDisplay_name();
            entranceDetailTv.setText(display_name);
            SharePreferensUtil.putString(KeyContacts.SP_KEY_DISPLAY_NAME, display_name, KeyContacts.SP_NAME_JPUSH);

        }
    }

    @Override
    public void getUseridByRoomSuccess(boolean b, int userId, String roomNum) {

        if (b) {
            if (hasCallingVideo) {
                remoteUserId = userId + "";
                callUserId = userId;
                inputNum = roomNum;
                callVideo(userId + "", roomNum);
//                ToastUtil.showCustomToast("拨打用户userId = " + userId);
            }
        } else {
            endCall();
            faceOnResuse();
        }
    }

    @Override
    public void loadBannerSuccess() {
        initView();
    }

    @Override
    public void checkVersionSuccess(VersionResBean bean) {
        if (bean.getData() != null) {
            if (bean.getData().isUpgrade()) {
                String path = bean.getData().getPath();
                Intent intent = new Intent(this, AppDownloadService.class);
                intent.putExtra(KeyContacts.KEY_URL, path);
                intent.putExtra(KeyContacts.KEY_TITLE, "下载中..");
                startService(intent);
            }
        }
    }

    //清除设备信息，设备解绑了
    private void clearDeviceData() {
        ToastUtil.showCustomToast("设备已解绑");
        UserInfoInstance.getInstance().reset();
        CameraUtil.clearAllFace(faceSet);

        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtil.deleteFilesByDirectory(new File(FileUtil.getAppCachePath(MainActivity.this)));
                clearDB();

                goTo(SplashActivity.class);
                finish();
            }
        }).start();


    }

    //检查设备是否可用，被禁用时返回false
    private boolean deviceEnable() {
        boolean enable = SharePreferensUtil.getBoolean(KeyContacts.SP_KEY_DEVICE_ENABLE, true, KeyContacts.SP_NAME_JPUSH);
        return enable;
    }

    private void showEnableToast() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showCustomToast("设备已被禁用，请联系管理员");
            }
        });
    }

    //禁用设备
    private void disableDevice() {
        showEnableToast();
        SharePreferensUtil.putBoolean(KeyContacts.SP_KEY_DEVICE_ENABLE, false, KeyContacts.SP_NAME_JPUSH);
    }


    private void clearDB() {
        DbManager.getInstance().getDaoSession().getFaceImageDao().deleteAll();
        DbManager.getInstance().getDaoSession().getOpenCodeDao().deleteAll();
        DbManager.getInstance().getDaoSession().getICCardDao().deleteAll();
        DbManager.getInstance().getDaoSession().getIDCardDao().deleteAll();
        DbManager.getInstance().getDaoSession().getOpenRecordDao().deleteAll();
    }


    public Bitmap getCameraBitmap() {
        //bitmapBytes格式成YuvImage格式，YuvImage格式是横的，宽高反的
        YuvImage yuvimage = new YuvImage(bitmapBytes, ImageFormat.NV21, preWidth,
                preHeight, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, preWidth,
                preHeight), 100, baos);
        Bitmap bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);

        return bitmap;
    }

    private void getPicture(final int type, final int id, final String sn) {
        String picturePath = FileUtil.getAppRecordPicturePath(MainActivity.this);
        File file = new File(picturePath, new Date().getTime() + ".jpg");
//        Utils.saveBitmap(file.getPath(), BitmapUtil.getViewBitmap(homebgIv));

        if (bitmapBytes != null) {
            Bitmap bitmap = getCameraBitmap();
//
            bitmap = BitmapUtil.rotateBitmap(90, bitmap);
            Utils.saveBitmap(file.getPath(), bitmap);
        }


        CompressImageUtil compressImageUtil = new CompressImageUtil(this, null);
        compressImageUtil.compress(file.getPath(), new CompressImageUtil.CompressListener() {
            @Override
            public void onCompressSuccess(String imgPath) {
                savaICOrIDCardRecord(type, id, sn, imgPath);
            }

            @Override
            public void onCompressFailed(String imgPath, String msg) {
                savaICOrIDCardRecord(type, id, sn, imgPath);
            }
        });
    }

    private long faceErrorStartTime;//人脸识别上一次提示的时间
//    private boolean isFacePause;//人脸识别成功后禁止再提示，等关锁后才能再触发提示

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void faceOpenSuccess(FaceSuccessEventBean bean) {

        if (!deviceEnable()) {
            showEnableToast();
            return;
        }

        if (bean.isRegist) {
            mCameraView.setBackgroundResource(R.drawable.green_line_bg);
            openStatusTv.setText("您好！\n认证成功");
            openStatusTv.setTextColor(Color.parseColor("#ffffff"));

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastOpenTime >= 2000) {
                lastOpenTime = currentTime;
                Message msg = Message.obtain();
                msg.what = 1;
                msg.arg1 = 4;
                msg.obj = String.valueOf(bean.user_id);
                doorHandler.sendMessage(msg);
            }
        } else {
            mCameraView.setBackgroundResource(R.drawable.red_line_bg);
            openStatusTv.setText("抱歉！\n认证失败");
            openStatusTv.setTextColor(Color.parseColor("#ff0000"));

            long now = System.currentTimeMillis();
            if (now - faceErrorStartTime > 3000) {
//                ToastUtil.showCustomToast(bean.faceResult);
                startMusic(4);
                faceErrorStartTime = now;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void downVideoSuccess(DownVideoSuccessEventBus bean) {
        initView();
    }


    // -----------------------------------------------------------
    // For Face start..

    private RelativeLayout faceParentRl;
    private TextView dateTv, openStatusTv;
    private SurfaceView sfv_draw_view; //人脸框绘画层
    public FaceSet faceSet = null; //sdk逻辑层
    public CameraView mIRCameraView; //红外摄像头
    public CameraView mCameraView; //RGB摄像头
    private byte[] irData; //ir视频流
    private int screenW = 320;//屏幕分辨率w
    private final Object lock = new Object();
    private Size ratio;//摄像头预览分辨率
    private float scale_bit = 1;
    private List<YMFace> ymFaces;

    public DemoConfig mConfig;
    // For Face end..
    private int preWidth = 640;//根据相机流生成图片的宽
    private int preHeight = 480;//生成图片的高

    private boolean faceTrackInit = false;//人脸识别是否初始化

    private void onFaceResume() {

        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
//                            checkCameraEnable();

                            userMap = UserDataUtil.updateDataSource(true);
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
//                            showShortToast(getApplicationContext(), "code:" + result.code + "  " + result.msg);
                            mConfig.specialCameraLeftRightReverse = true;
                            //保存配置
                            SharedPrefUtils.putObject(getApplicationContext(), "DEMO_CONFIG", mConfig);
                            android.util.Log.d("Debug", "mConfig = " + mConfig.toString());

                            if (isDoubleEyes) openIRCamera();

                            faceTrackInit = true;
                        } else {
                            // showLongToast(getApplicationContext(), "请同意软件的权限，才能继续使用");
                        }
                    }
                });
    }

    private void onFacePause() {
        if (faceTrackInit) {
            if (mCameraView != null) {
                mCameraView.stop();
            }
            if (mIRCameraView != null) {
                if (mIRCameraView.isCameraOpened()) {
                    mIRCameraView.stop();
                }
            }
            // faceSet.stopTrack();
            faceTrackInit = false;
        }
    }

    private void startCamera() {
        //开启相机
        if (mCameraView != null && !mCameraView.isCameraOpened())
            mCameraView.start();
        if (isDoubleEyes)
            openIRCamera();

        faceTrackInit = true;
        hideCallVideoLayout();
        hideRoomInputLayout();
    }


    // 初始化人脸所需的UI;
    private void initFaceCamera() {
        initConfig();

        //识别预览框的背景层
        faceParentRl = findViewById(R.id.face_parentlayout);
        dateTv = findViewById(R.id.date_tv);
        openStatusTv = findViewById(R.id.open_staus_tv);
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        String today = DateFormateUtil.getCurrentDate();
        dateTv.setText(today + " " + StringUtil.getWeekDay(week));


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

            //将byte数组设置给 onPreviewFrame 回调中，不用频繁创建销毁数组，在startPreview前调用
//            mPreviewBuffer = new byte[mConfig.previewSizeWidth * mConfig.previewSizeHeight];
//            mCameraView.addCallbackBuffer(mPreviewBuffer);
//            mCameraView.setPreviewCallbackWithBuffer();

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
                                    showFaceLayout();

                                } else if (ymFaces == null && isFaceViewShow) {
                                    hideFaceLayout();
                                }
                                // 绘画人脸框
                                drawView(ymFaces, mConfig, sfv_draw_view, scale_bit, mCameraView.getFacing(), "");

                            }
                        });
                        //将byte数组设置给 onPreviewFrame 回调中，不用频繁创建销毁数组，在startPreview前调用
//                        mCameraView.addCallbackBuffer(data);
                    }
                }
            });


        }
    }

    private void showFaceLayout() {
        isFaceViewShow = true;
//        mCameraView.bringToFront();
        sfv_draw_view.setVisibility(View.VISIBLE);
        faceParentRl.bringToFront();
        entranceDetailTv.bringToFront();
        // if (mIRCameraView != null) mIRCameraView.bringToFront();
    }

    private void hideFaceLayout() {
        if (!isFaceViewShow) return;
        isFaceViewShow = false;
        sfv_draw_view.setVisibility(View.INVISIBLE);
        homebgIv.bringToFront();
        banner.bringToFront();
//        toolsBar.bringToFront();
        callVideoLayout.bringToFront();
        roomNumLayout.bringToFront();
        entranceDetailTv.bringToFront();

        mCameraView.setBackground(null);
        openStatusTv.setText("");
    }


    private void hideAndStopFace() {
        onFacePause();
        hideFaceLayout();
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
//    private byte[] mBytes;
//    private byte[] mBytesIr;
//    private int mWidth;
//    private int mHeight;
    private boolean openFaceTrack = false;//追踪
    private boolean openFaceReco = true;//识别
//    private boolean openFaceLiveness = false; //红外活体
//    private boolean openFaceRgbLiveness = true;  //可见光活体
//    private boolean openFaceBinoculareLiveness = false; //双目活体（可见光+红外）
    private Map<Integer, User> userMap;

    private boolean isFaceViewShow = false;
    private boolean isDoubleEyes = true;

    private byte[] bitmapBytes;//保存人脸识别成功后的当前图片
    boolean isFaceSuccess ;

    protected List<YMFace> onCameraPreviewFrame(final byte[] bytes, final byte[] irBytes, final int iw, final int ih, final boolean isMulti) {
        if (bytes == null) return null;
        //得到识别是的图片
        if(isFaceSuccess) {
            bitmapBytes = bytes;
            isFaceSuccess = false;
        }

//        mBytes = bytes;
//        mBytesIr = irBytes;
//        mWidth = iw;
//        mHeight = ih;
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
        sfv_draw_view.getLayoutParams().height = viewH;
        sfv_draw_view.getLayoutParams().width = viewW;
        sfv_draw_view.requestLayout();
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
        return sdkOrientation;
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


    private void faceOnResuse() {
        callUserId = 0;
        callSecond = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startCamera();
            }
        }, 500);
    }

}

