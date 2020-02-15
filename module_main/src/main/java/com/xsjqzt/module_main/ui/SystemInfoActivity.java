package com.xsjqzt.module_main.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.jbb.library_common.basemvp.BaseMvpActivity;
import com.jbb.library_common.comfig.InterfaceConfig;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.retrofit.RetrofitManager;
import com.jbb.library_common.utils.CommUtil;
import com.jbb.library_common.utils.DeviceUtil;
import com.jbb.library_common.utils.SharePreferensUtil;
import com.jbb.library_common.utils.Utils;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.model.EntranceInfoResBean;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.presenter.SystemInfoPresenter;
import com.xsjqzt.module_main.view.SystemInfoIView;
import com.yzq.zxinglibrary.encode.CodeCreator;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import cn.jpush.android.api.JPushInterface;

public class SystemInfoActivity extends BaseMvpActivity<SystemInfoIView, SystemInfoPresenter> implements SystemInfoIView {

    private TextView deviceNameTv,versionTv, ipAddressTv, qrNumTv;
    private ImageView voiceIv, qrCodeIv;
    private boolean isShiftClick;
//    private Button confirmBtn;

    private MyBroadcastReceiver mReceiver;
    private boolean isKeyEnterFirst;

    @Override
    public void init() {
        deviceNameTv = findViewById(R.id.device_name_tv);
        versionTv = findViewById(R.id.version_tv);
        ipAddressTv = findViewById(R.id.ip_address_tv);
        qrNumTv = findViewById(R.id.serial_number_tv);
        voiceIv = findViewById(R.id.voice_iv);
        qrCodeIv = findViewById(R.id.qr_code_iv);


        createQrCode();

        if (UserInfoInstance.getInstance().hasLogin())
            presenter.loadDevice();

        IntentFilter filter = new IntentFilter();
        filter.addAction(KeyContacts.ACTION_RECEICE_NOTITY);
        mReceiver = new MyBroadcastReceiver();
        registerReceiver(mReceiver, filter);
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
        Bitmap qrCode = CodeCreator.createQRCode(code, CommUtil.dp2px(130), CommUtil.dp2px(130), null);
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

            String display_name = bean.getData().getDisplay_name();

            deviceNameTv.setText(display_name);
            versionTv.setText(Utils.getVerName(this));
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


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER && isKeyEnterFirst) {
            isKeyEnterFirst = false;
            finish();
            return true;
        }
        isKeyEnterFirst = true;
        return super.dispatchKeyEvent(event);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
           if (intent.getAction() == KeyContacts.ACTION_RECEICE_NOTITY) {
               if(intent.getExtras() == null)return;
               try {
                   String extras = intent.getExtras().getString(JPushInterface.EXTRA_EXTRA);
                   JSONObject json = new JSONObject(extras);
                   int type = json.optInt("type");

                   if (type == 106) {//设备绑定成功
                       String url_root = json.optString("url_root", "");
                       SharePreferensUtil.putString(KeyContacts.URL_ROOT,url_root,KeyContacts.SP_NAME_JPUSH);
                       if(!TextUtils.isEmpty(url_root)  && !InterfaceConfig.BASEURL.equals(url_root)) {
                           InterfaceConfig.BASEURL = url_root;

                           RetrofitManager.getInstance().switchUrl(url_root);
                       }
                       finish();

                   }
               } catch (Exception e) {

               }
            }
        }
    }

}
