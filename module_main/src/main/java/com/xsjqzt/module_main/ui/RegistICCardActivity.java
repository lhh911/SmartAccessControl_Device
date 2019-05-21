package com.xsjqzt.module_main.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Gpio;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jbb.library_common.basemvp.BaseMvpActivity;
import com.jbb.library_common.utils.CommUtil;
import com.jbb.library_common.utils.ScreenShotUtil;
import com.jbb.library_common.utils.ToastUtil;
import com.jbb.library_common.utils.log.LogUtil;
import com.xiao.nicevideoplayer.SimpleVideoPlayerManager;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.greendao.DbManager;
import com.xsjqzt.module_main.greendao.ICCardDao;
import com.xsjqzt.module_main.greendao.entity.ICCard;
import com.xsjqzt.module_main.greendao.entity.IDCard;
import com.xsjqzt.module_main.presenter.RegistICCardPresenter;
import com.xsjqzt.module_main.view.RegistICCardIView;
import com.xsjqzt.module_main.widget.ImgTextView;

import java.io.IOException;
import java.lang.ref.WeakReference;

import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;

public class RegistICCardActivity extends BaseMvpActivity<RegistICCardIView,RegistICCardPresenter> implements RegistICCardIView {
    private TextView registTipTv;
    private ImageView qrCodeIv;
    private ImgTextView imgTextView;
    private TextView numTv;
    private LinearLayout qrCodeLayout;

    private Button homeBtn;

    private String qrCodeNum = "abc888888889999";
    private int mType = 0; // 0 : IC卡注册 ，1 身份证注册

    private MyHandler doorHandler;
    //串口
    private SerialHelper serialHelper;
    private String sPort = "/dev/ttyS3";
    private int iBaudRate = 115200;

    @Override
    public RegistICCardPresenter initPresenter() {
        return new RegistICCardPresenter();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_regist_iccard;
    }

    @Override
    public void init() {
        homeBtn = findViewById(R.id.home_btn);
        registTipTv = findViewById(R.id.regist_tip_tv);
        imgTextView = findViewById(R.id.tip_layout);
        numTv = findViewById(R.id.serial_number_tv);
        qrCodeLayout = findViewById(R.id.qr_code_layout);
        qrCodeIv = findViewById(R.id.qr_code_iv);

        mType = getIntent().getIntExtra("mType", 0);
        if(mType == 1){
            registTipTv.setText("注册身份证");
            imgTextView.setText("请刷身份证");
        }else{
            registTipTv.setText("注册IC卡");
            imgTextView.setText("请刷IC卡");
        }
//        numTv.setText(qrCodeNum);
//        createQrCode();
        homeBtn.setOnClickListener(this);

        startMeasuing();
    }

    private void createQrCode(){
        int width = CommUtil.dp2px(120);
        qrCodeLayout.setVisibility(View.VISIBLE);
        Bitmap qrCode = ScreenShotUtil.createQRCode(qrCodeNum, width, width, "#ffffff");
        qrCodeIv.setImageBitmap(qrCode);
    }

    private void setError(){
        imgTextView.setText("非法卡号");
        imgTextView.setImageResouse(R.mipmap.icon_error,CommUtil.dp2px(23));
    }


    private void setSuccess(){
        imgTextView.setText("请使用手机app扫描二维码");
        imgTextView.setImageResouse(R.mipmap.icon_gth,CommUtil.dp2px(13));
    }



    @Override
    public String getATitle() {
        return null;
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.home_btn){
            finish();
        }
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
//        LogUtil.w("nfc数据 = " + str);
        qrCodeNum  = str.substring(2,20);
//        qrCodeNum  = str;
        numTv.setText(qrCodeNum);

        if (str.length() > 10) {//ic卡
            createQrCode();
            setSuccess();
        } else if (str.length() > 18) {
            createQrCode();
            setSuccess();
        } else {
            ToastUtil.showCustomToast("无法识别的卡，请用ic卡或身份证开门");
            setError();
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

        open();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
                    case 3:
                        String str = (String) msg.obj;
                        parseData(str);
                        break;
                }
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
