package com.xsjqzt.module_main.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.presenter.RegistICCardPresenter;
import com.xsjqzt.module_main.view.RegistICCardIView;
import com.xsjqzt.module_main.widget.ImgTextView;
import com.yzq.zxinglibrary.encode.CodeCreator;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigInteger;

import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;
import tp.xmaihh.serialport.utils.ByteUtil;

public class RegistICCardActivity extends BaseMvpActivity<RegistICCardIView, RegistICCardPresenter> implements RegistICCardIView {
    private TextView registTipTv;
    private ImageView qrCodeIv;
    private ImgTextView imgTextView;
    private TextView numTv;
    private LinearLayout qrCodeLayout;

//    private Button homeBtn;

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
//        homeBtn = findViewById(R.id.home_btn);
        registTipTv = findViewById(R.id.regist_tip_tv);
        imgTextView = findViewById(R.id.tip_layout);
        numTv = findViewById(R.id.serial_number_tv);
        qrCodeLayout = findViewById(R.id.qr_code_layout);
        qrCodeIv = findViewById(R.id.qr_code_iv);

        mType = getIntent().getIntExtra("mType", 0);
        if (mType == 1) {
            registTipTv.setText("注册身份证");
            imgTextView.setText("请刷身份证");
        } else {
            registTipTv.setText("注册IC卡");
            imgTextView.setText("请刷IC卡");
        }
//        numTv.setText(qrCodeNum);
//        createQrCode();
//        homeBtn.setOnClickListener(this);

        startMeasuing();
    }

    private void createQrCode() {
        int width = CommUtil.dp2px(140);
        qrCodeLayout.setVisibility(View.VISIBLE);
        Bitmap qrCode = CodeCreator.createQRCode(qrCodeNum, width, width, null);

        qrCodeIv.setImageBitmap(qrCode);
    }

    private void setError() {
        imgTextView.setText("非法卡号");
        imgTextView.setImageResouse(R.mipmap.icon_error, CommUtil.dp2px(23));
    }


    private void setSuccess() {
        imgTextView.setText("请使用手机app扫描二维码");
        imgTextView.setImageResouse(R.mipmap.icon_gth, CommUtil.dp2px(13));
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
                doorHandler.sendMessage(msg);
            }
        };
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

        public MyHandler(Activity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (weakReference.get() != null) {
                switch (msg.what) {
                    case 3:
                        String str = (String) msg.obj;
                        if (TextUtils.isEmpty(str)) {
                            ToastUtil.showCustomToast("未注册或无法识别的卡，请用ic卡或身份证开门");
                            setError();
                        } else {
                            qrCodeNum = str;
                            numTv.setText(qrCodeNum);
                            createQrCode();
                            setSuccess();
                        }
                        break;
                }
            }
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_STAR){// * 号
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
