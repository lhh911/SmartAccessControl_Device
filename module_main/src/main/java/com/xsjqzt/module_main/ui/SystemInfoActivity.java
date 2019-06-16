package com.xsjqzt.module_main.ui;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.yzq.zxinglibrary.encode.CodeCreator;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class SystemInfoActivity extends BaseMvpActivity<SystemInfoIView, SystemInfoPresenter> implements SystemInfoIView {

    private TextView deviceNameTv, ipAddressTv, qrNumTv;
    private ImageView voiceIv, qrCodeIv;
    private boolean isShiftClick;
//    private Button confirmBtn;

    @Override
    public void init() {
        deviceNameTv = findViewById(R.id.device_name_tv);
        ipAddressTv = findViewById(R.id.ip_address_tv);
        qrNumTv = findViewById(R.id.serial_number_tv);
        voiceIv = findViewById(R.id.voice_iv);
        qrCodeIv = findViewById(R.id.qr_code_iv);


        createQrCode();

        if (UserInfoInstance.getInstance().hasLogin())
            presenter.loadDevice();

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

    private void createQrCode() {
        String code = UserInfoInstance.getInstance().getSn1() + UserInfoInstance.getInstance().getSn2();
        qrNumTv.setText(code);
//        Bitmap qrCode = ScreenShotUtil.createQRCode(UserInfoInstance.getInstance().getMacAddress(), CommUtil.dp2px(110), CommUtil.dp2px(110), "#ffffff");
        Bitmap qrCode = CodeCreator.createQRCode(code, CommUtil.dp2px(140), CommUtil.dp2px(140), null);
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
        if (bean != null && bean.getData() != null) {
            String garden_name = bean.getData().getGarden_name();
            String region_name = bean.getData().getRegion_name();
            String building_name = bean.getData().getBuilding_name();
            String name = bean.getData().getName();

            deviceNameTv.setText(garden_name + region_name + building_name + name);

            ipAddressTv.setText(getLocalIp());
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT || keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT) {// * 号
            isShiftClick  = true;
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_3) {
            if(isShiftClick){// # 号
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    public String getLocalIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                // 得到每一个网络接口绑定的所有ip
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
                        ipaddress = ip.getHostAddress();
                        return ipaddress;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipaddress;

    }


}
