package com.xsjqzt.module_main.ui;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jbb.library_common.basemvp.BaseMvpActivity;
import com.jbb.library_common.utils.CommUtil;
import com.jbb.library_common.utils.ScreenShotUtil;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.presenter.RegistICCardPresenter;
import com.xsjqzt.module_main.view.RegistICCardIView;
import com.xsjqzt.module_main.widget.ImgTextView;

public class RegistICCardActivity extends BaseMvpActivity<RegistICCardIView,RegistICCardPresenter> implements RegistICCardIView {
    private TextView registTipTv;
    private ImageView qrCodeIv;
    private ImgTextView imgTextView;
    private TextView numTv;
    private LinearLayout qrCodeLayout;

    private String qrCodeNum = "abc888888889999";
    private int mType = 0; // 0 : IC卡注册 ，1 身份证注册

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
        registTipTv = findViewById(R.id.regist_tip_tv);
        imgTextView = findViewById(R.id.tip_layout);
        numTv = findViewById(R.id.serial_number_tv);
        qrCodeLayout = findViewById(R.id.qr_code_layout);
        qrCodeIv = findViewById(R.id.qr_code_iv);

        mType = getIntent().getIntExtra("mType", 0);
        if(mType == 1){
            registTipTv.setText("注册身份证");
            imgTextView.setText("请刷身份证");
        }
        numTv.setText(qrCodeNum);
        createQrCode();
    }

    private void createQrCode(){
        qrCodeLayout.setVisibility(View.VISIBLE);
        Bitmap qrCode = ScreenShotUtil.createQRCode(qrCodeNum, CommUtil.dp2px(100), CommUtil.dp2px(100), "#ffffff");
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
}
