package com.xsjqzt.module_main.ui;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.jbb.library_common.basemvp.BaseMvpActivity;
import com.jbb.library_common.utils.CommUtil;
import com.jbb.library_common.utils.ScreenShotUtil;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.presenter.RegistICCardPresenter;
import com.xsjqzt.module_main.view.RegistICCardIView;

public class RegistICCardActivity extends BaseMvpActivity<RegistICCardIView,RegistICCardPresenter> implements RegistICCardIView {

    private ImageView qrCodeIv;
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
        qrCodeIv = findViewById(R.id.qr_code_iv);
        Bitmap qrCode = ScreenShotUtil.createQRCode("abc8888888899999999999999999", CommUtil.dp2px(100), CommUtil.dp2px(100), "#ffffff");
        qrCodeIv.setImageBitmap(qrCode);
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
