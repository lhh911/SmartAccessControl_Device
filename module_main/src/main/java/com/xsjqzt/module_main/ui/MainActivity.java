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
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Gpio;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.xiao.nicevideoplayer.SimpleVideoPlayer;
import com.xiao.nicevideoplayer.SimpleVideoPlayerManager;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.activity.FaceDemoActivity;
import com.xsjqzt.module_main.greendao.DbManager;
import com.xsjqzt.module_main.greendao.FaceImageDao;
import com.xsjqzt.module_main.greendao.ICCardDao;
import com.xsjqzt.module_main.greendao.IDCardDao;
import com.xsjqzt.module_main.greendao.entity.FaceImage;
import com.xsjqzt.module_main.greendao.entity.ICCard;
import com.xsjqzt.module_main.greendao.entity.IDCard;
import com.xsjqzt.module_main.greendao.entity.OpenRecord;
import com.xsjqzt.module_main.model.EntranceDetailsResBean;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.presenter.MainPresenter;
import com.xsjqzt.module_main.receive.AlarmReceiver;
import com.xsjqzt.module_main.view.MainView;
import com.xsjqzt.module_main.widget.ImgTextView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Setting;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;

@Route(path = "/module_main/main")
public class MainActivity extends BaseMvpActivity<MainView, MainPresenter> implements MainView {

    private Banner banner;
    private SimpleVideoPlayer videoPlayer;

    //视频呼叫layout
    private LinearLayout callVideoLayout;
    private TextView callNumTv, callStatusTv, callTipTv;

    //房号密码号输入layout
    private LinearLayout roomNumLayout;//房号输入布局
    private EditText roomNumEt;
    private LinearLayout inputNumLayout;
    private ImgTextView successLayout, errorLayout;
    private int showSucOrError = 1; // 1 开门成功 ，2 开门失败


    private int showType = 1;// 1 图片广告，2 视频广告
    private MyBroadcastReceiver mReceiver;

    private String sn1;//序列号1
    private String sn2;//序列号2
    private PendingIntent pi;
    private AlarmManager am;

    //串口
    private SerialHelper serialHelper;
    private String sPort = "/dev/ttyS3";
    private int iBaudRate = 115200;

    private int mType ;// 0 默认什么都没显示， 1 密码开锁布局显示，2 视频电话显示
    private MyHandler doorHandler;

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

        //房号输入
        roomNumLayout = findViewById(R.id.input_num_layout);
        roomNumEt = findViewById(R.id.room_psw_et);
        TextView bottomTv = findViewById(R.id.bottom_tv);
        inputNumLayout = findViewById(R.id.input_layout);
        successLayout = findViewById(R.id.success_layout);
        errorLayout = findViewById(R.id.error_layout);

        Drawable drawable = getResources().getDrawable(R.mipmap.icon_gth);
        drawable.setBounds(0, 0, CommUtil.dp2px(13), CommUtil.dp2px(13));
        bottomTv.setCompoundDrawables(drawable, null, null, null);


        banner = findViewById(R.id.banner);
        videoPlayer = findViewById(R.id.videoplayer);
        requestPermiss();

        registReceiver();

//        presenter.entranceDetail();

        setAlarm();
        startMeasuing();

        test();
    }

    private void test() {
        List<ICCard> list = DbManager.getInstance().getDaoSession().getICCardDao().queryBuilder().list();
        StringBuffer sf = new StringBuffer();
        sf.append("数据库的IC卡").append("\n");
        for(ICCard card: list){
            sf.append(card.getUser_name()+ "，"+card.getSn()+ "，"+ card.getSid()).append("\n");
        }

        new AlertDialog.Builder(this).setMessage(sf.toString()).show();

        int type = 1;
        String sn = "ic20190501";
        if(type == 1){
            presenter.uploadICCardRecord(type,sn);
        }else{
            presenter.uploadIDCardRecord(type,sn);
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
        showRoomNumOpen();
    }

    public void btn2Click(View view) {
        goTo(RegistICCardActivity.class);
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
            if(mType == 1){
                roomNumLayout.setVisibility(View.GONE);
                dismissAnim(roomNumLayout);
                mType = 0;
            }else if(mType == 2){
                callVideoLayout.setVisibility(View.GONE);
                dismissAnim(callVideoLayout);
                mType = 0;
            }
            return true;
        }


        return super.onKeyDown(keyCode, event);
    }



    @Override
    public void loadKeySuccess(String key) {
        UserInfoInstance.getInstance().setKey(key);
        //获取token
        //获取token的key，生成规则：skey = md5(sn1+sn2+key)
        String skey = MD5Util.md5(sn1 + sn2 + key);
        presenter.getToken(sn1, skey);
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
        filter.addAction(Manifest.permission.ACCESS_NETWORK_STATE);
        filter.addAction(Manifest.permission.CHANGE_NETWORK_STATE);
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
            } else if (intent.getAction() == Manifest.permission.ACCESS_NETWORK_STATE || intent.getAction() == Manifest.permission.CHANGE_NETWORK_STATE) {
                //监听网络变化
                if (Utils.getNetWorkState(MainActivity.this)) {
                    LogUtil.w("NetWorkState = " + true);
                    login();
                }
            } else if (intent.getAction() == KeyContacts.ACTION_RECEICE_NOTITY) {
                handleNotity(intent.getExtras());
            }
        }
    }

    private void handleNotity(final Bundle bundle) {

//        Observable.fromArray(bundle)
//                .flatMap(new Function<Bundle, ObservableSource<String>>() {
//                    @Override
//                    public ObservableSource<String> apply(Bundle bundle) throws Exception {
//
//                        return Observable.just("");
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .subscribe();

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
                    } else if (type == 3) {//设置音量
                        setVoice(json.getInt("volume"));
                    }else if(type == 4){//下载人脸图片，并注册到阅面的人脸库，将注册状态发送给后台服务器
                        downFaceImage();
                    }
                }catch (Exception e){

                }
            }
        }).start();


    }

    private void downFaceImage() {
//        FaceImageDao faceImageDao = DbManager.getInstance().getDaoSession().getFaceImageDao();
//        FaceImage faceImage = faceImageDao.queryBuilder().limit(1).orderDesc(ICCardDao.Properties.Sid).unique();
//        int sid = 0;
//        if (faceImage != null) {
//            sid = faceImage.getSid();
//        }
        presenter.loadFaceImage(this,1);
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
        ICCard iccard = icCardDao.queryBuilder().limit(1).orderDesc(ICCardDao.Properties.Sid).unique();
        int sid = 0;
        if (iccard != null) {
            sid = iccard.getSid();
        }
        presenter.loadICCards(sid);
    }

    private void downIDCardData() {
        IDCardDao idCardDao = DbManager.getInstance().getDaoSession().getIDCardDao();
        IDCard idcard = idCardDao.queryBuilder().limit(1).orderDesc(IDCardDao.Properties.Sid).unique();
        int sid = 0;
        if (idcard != null) {
            sid = idcard.getSid();
        }
        presenter.loadIDCards(sid);

    }


    private void login() {
        if (UserInfoInstance.getInstance().hasLogin())//没登录需要登录下
            return;
        //获取加密key
        presenter.loadKey(sn1);
    }

    private void refreshToken() {
        presenter.refreshToken();
    }


    /**
     *  检查输入的是房号还是开门密码
     * @param inputNum  4位或6位位房号 ， 5位为开门密码
     */
    private void checkInput(String inputNum){
        if(inputNum.length() == 5){//密码开门
            roomNumLayout.setVisibility(View.VISIBLE);
            callVideoLayout.setVisibility(View.GONE);
            //请求接口验证密码

            showSucOrError = 2;
            setShowSucOrError();

        }else if(inputNum.length() == 4 || inputNum.length() == 6){
            roomNumLayout.setVisibility(View.GONE);
            callVideoLayout.setVisibility(View.VISIBLE);
            mType = 2;

            //根据房号获取userid，再拨视频通话
            callVideo(inputNum);
        }else{
            ToastUtil.showCustomToast("请输入正确的房间号或者临时密码");
        }
    }


    //密码开门显示结果
    private void setShowSucOrError() {
        if (showSucOrError == 1) {//开门成功
            inputNumLayout.setVisibility(View.GONE);
            successLayout.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        } else {
            inputNumLayout.setVisibility(View.GONE);
            successLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
        }

        starTime();
    }

    //密码开门时错误情况下3秒后退回输入状态
    private void starTime() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inputNumLayout.setVisibility(View.VISIBLE);
                        successLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.GONE);
                        if (showSucOrError == 1) {
                            hideRoomNumLayout();
                        } else {
                            mType = 1;
                        }
                    }
                });
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 3000);
    }


    //拨打视频通话
    private void callVideo(String userid) {
//        String roomNum = callNumTv.getText().toString().trim();
        //通过房号获取到环信的账号名（接口）

        try {//单参数
            EMClient.getInstance().callManager().makeVideoCall(userid,"");
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

    //显示密码开门ui
    private void showRoomNumOpen() {
        roomNumLayout.setVisibility(View.VISIBLE);
        roomNumEt.setText("");
        showAnim(roomNumLayout);
        mType = 1;
    }

    //掩藏视频通话ui
    private void hideCallVideoLayout(){
        callVideoLayout.setVisibility(View.GONE);
        dismissAnim(callVideoLayout);
        mType = 0;
    }
    //掩藏密码开门ui
    private void hideRoomNumLayout(){
        roomNumLayout.setVisibility(View.GONE);
        dismissAnim(roomNumLayout);
        mType = 0;
    }


    //保存记录到数据库
    public void savaICOrIDCardRecord(int type,int sid ,String sn) {
//        int type = 1;

        String picturePath = FileUtil.getAppRecordPicturePath(this);
        File file = new File(picturePath, new Date().getTime()+".jpg");
        Utils.saveBitmap(file.getPath(),BitmapUtil.getViewBitmap(banner));

        OpenRecord record = new OpenRecord();
        record.setCreateTime(new Date().getTime());
        record.setImage(file.getPath());
        record.setSn(sn);
        record.setStatus(1);
        record.setUploadStatus(false);
        record.setSid(sid);
//        record.setType("默认");
//        record.setICOrID(type);

        DbManager.getInstance().getDaoSession().getOpenRecordDao().insert(record);

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
        am.setRepeating(AlarmManager.RTC_WAKEUP, new Date().getTime(), interval, pi);

    }


    //初始化nfc串口
    public void startMeasuing() {
        LogUtil.w("SerialPort  startMeasuing");
        doorHandler = new MyHandler(this);

        serialHelper = new SerialHelper(sPort, iBaudRate) {
            @Override
            protected void onDataReceived(ComBean paramComBean) {
                String str = bytesToHex(paramComBean.bRec);

                //对比数据库，开门
                Message msg = Message.obtain();
                msg.what = 3;
                msg.obj = str;
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
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }


    private void parseData(String str) {
        ToastUtil.showCustomToast(str);
        LogUtil.w("nfc数据 = " + str);

        if (str.length() > 10) {//ic卡
            ICCard iccard = DbManager.getInstance().getDaoSession().getICCardDao().queryBuilder()
                    .where(ICCardDao.Properties.Sn.eq(str)).unique();
            if (iccard != null) {
                Message msg = Message.obtain();
                msg.what = 1;
                msg.arg1 = 1;
                msg.obj = str;
                doorHandler.sendMessage(msg);
            }
        } else if (str.length() > 18) {
            IDCard idcard = DbManager.getInstance().getDaoSession().getIDCardDao().queryBuilder()
                    .where(ICCardDao.Properties.Sn.eq(str)).unique();
            if (idcard != null) {
                Message msg = Message.obtain();
                msg.what = 1;
                msg.arg1 = 2;
                msg.obj = str;
                doorHandler.sendMessage(msg);
            }
        } else {
            ToastUtil.showCustomToast("无法识别的卡，请用ic卡或身份证开门");
        }
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        SimpleVideoPlayerManager.instance().suspendNiceVideoPlayer();
        if (banner != null)
            banner.stopAutoPlay();

        stopMeasuing();
    }

    public class MyHandler extends Handler {
        private WeakReference<Activity> weakReference;

        public MyHandler(Activity activity){
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if(weakReference.get() != null) {
                switch (msg.what) {
                    case 1:
                        // open door;
                        int type = msg.arg1;
                        String sn = (String) msg.obj;
                        Gpio.RelayOnOff(1);
                        uploadRecord(type,sn);
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

    //上传不带图片的记录
    private void uploadRecord(int type ,String sn){
        if(type == 1){
            presenter.uploadICCardRecord(type,sn);
        }else{
            presenter.uploadIDCardRecord(type,sn);
        }
    }

    @Override
    public void uploadCardSuccess(int type, int id,String sn) {
        savaICOrIDCardRecord(type,id ,sn);
    }
}

