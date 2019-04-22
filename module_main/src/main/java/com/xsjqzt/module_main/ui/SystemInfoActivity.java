package com.xsjqzt.module_main.ui;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jbb.library_common.basemvp.BaseActivity;
import com.jbb.library_common.utils.CommUtil;
import com.jbb.library_common.utils.ScreenShotUtil;
import com.xsjqzt.module_main.R;

public class SystemInfoActivity extends BaseActivity {

    private TextView deviceNameTv,ipAddressTv,qrNumTv;
    private ImageView voiceIv,qrCodeIv;
    private String qrCodeNum = "adcei100011" ;

    @Override
    public void init() {
        deviceNameTv = findViewById(R.id.device_name_tv);
        ipAddressTv = findViewById(R.id.ip_address_tv);
        qrNumTv = findViewById(R.id.serial_number_tv);
        voiceIv = findViewById(R.id.voice_iv);
        qrCodeIv = findViewById(R.id.qr_code_iv);

        createQrCode();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_system_info;
    }

    @Override
    public String getATitle() {
        return null;
    }

    private void createQrCode(){
        qrNumTv.setText(qrCodeNum);
        Bitmap qrCode = ScreenShotUtil.createQRCode(qrCodeNum, CommUtil.dp2px(100), CommUtil.dp2px(100), "#ffffff");
        qrCodeIv.setImageBitmap(qrCode);
    }

}
