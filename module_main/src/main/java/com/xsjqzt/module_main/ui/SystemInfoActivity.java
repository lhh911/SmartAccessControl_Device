package com.xsjqzt.module_main.ui;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jbb.library_common.basemvp.BaseActivity;
import com.jbb.library_common.basemvp.BaseMvpActivity;
import com.jbb.library_common.utils.CommUtil;
import com.jbb.library_common.utils.ScreenShotUtil;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.model.EntranceInfoResBean;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.presenter.SystemInfoPresenter;
import com.xsjqzt.module_main.view.SystemInfoIView;

public class SystemInfoActivity extends BaseMvpActivity<SystemInfoIView,SystemInfoPresenter> implements SystemInfoIView {

    private TextView deviceNameTv,ipAddressTv,qrNumTv;
    private ImageView voiceIv,qrCodeIv;
    private Button confirmBtn;

    @Override
    public void init() {
        confirmBtn = findViewById(R.id.confirm_bind_btn);
        deviceNameTv = findViewById(R.id.device_name_tv);
        ipAddressTv = findViewById(R.id.ip_address_tv);
        qrNumTv = findViewById(R.id.serial_number_tv);
        voiceIv = findViewById(R.id.voice_iv);
        qrCodeIv = findViewById(R.id.qr_code_iv);

        confirmBtn.setOnClickListener(this);

        createQrCode();

        if(UserInfoInstance.getInstance().hasLogin())
            presenter.loadDevice();

    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.confirm_bind_btn){
            finish();
        }
    }

    @Override
    public SystemInfoPresenter initPresenter() {
        return new SystemInfoPresenter();
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
        qrNumTv.setText(UserInfoInstance.getInstance().getMacAddress());
        Bitmap qrCode = ScreenShotUtil.createQRCode(UserInfoInstance.getInstance().getMacAddress(), CommUtil.dp2px(110), CommUtil.dp2px(110), "#ffffff");
        qrCodeIv.setImageBitmap(qrCode);
    }

    @Override
    public void showLoading() {
        show("");
    }

    @Override
    public void hideLoading() {
        dismiss();
    }

    @Override
    public void error(Exception e) {

    }

    @Override
    public void loadDeviceSuccess(EntranceInfoResBean bean) {
        if(bean != null && bean.getData() != null){
            String garden_name = bean.getData().getGarden_name();
            String region_name = bean.getData().getRegion_name();
            String building_name = bean.getData().getBuilding_name();
            String name = bean.getData().getName();

            deviceNameTv.setText(garden_name + region_name + building_name + name);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
