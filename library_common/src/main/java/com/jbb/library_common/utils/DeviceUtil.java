package com.jbb.library_common.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;

import com.jbb.library_common.BaseApplication;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 类: DeviceUtils <p>
 * 描述: 拿到设备上的一些信息 <p>
 * 作者: nn <p>
 * 时间: 2014年7月25日 上午11:18:41 <p>
 */
public class DeviceUtil {

    /**
     * 方法: isHasNetWork <p>
     * 描述: 判断网络是否可用 <p>
     * 参数: @return <p>
     * 返回: boolean <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年7月25日 上午11:19:05
     */
    public static boolean isNetWorkEnable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) BaseApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo network = cm.getActiveNetworkInfo();
                if (network != null && network.isConnected()) {

                    return true;
                }
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 方法: IsWiFi <p>
     * 描述: 判断当前网络是否是wifi <p>
     * 参数: @return <p>
     * 返回: boolean <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年7月25日 上午11:59:11
     */
    public static boolean IsWiFiState() {
        boolean is = false;
        try {
            ConnectivityManager connectMgr = (ConnectivityManager) BaseApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectMgr.getActiveNetworkInfo();
            if (info != null) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    is = true;
                }
            }
        } catch (Exception e) {
            is = false;
        }
        return is;
    }

    /**
     * 方法: isCanUseSd <p>
     * 描述: 判断SD卡是否可用 <p>
     * 参数: @return <p>
     * 返回: boolean <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年7月25日 上午11:59:31
     */
    public static boolean isCanUseSd() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 方法: isTablet <p>
     * 描述: 判断设备是否是平板 <p>
     * 参数: @return <p>
     * 返回: boolean <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年7月25日 上午11:59:53
     */
    public static boolean isTablet() {
        return (BaseApplication.getContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }



    /**
     * 方法: getIMSI <p>
     * 描述: 获取IMSI 卡的序列号 变卡就变 <p>
     * 参数: @return <p>
     * 返回: String <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年7月25日 下午12:00:31
     */
    public static String getIMSI() {
        String IMSI = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) BaseApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager.getSubscriberId() != null) {
                IMSI = telephonyManager.getSubscriberId();

            }
        } catch (Exception ex) {

        }
        return IMSI;
    }

    /**
     * 方法: getAndroidID <p>
     * 描述: 获取androidID <p>
     * 参数: @return <p>
     * 返回: String <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年7月25日 下午12:00:41
     */
    public static String getAndroidID() {
        String androidId = "";
        try {
            androidId = Secure.getString(BaseApplication.getContext().getContentResolver(), Secure.ANDROID_ID);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return androidId;
    }

    /**
     * 方法: getBlueTooth <p>
     * 描述: 获取蓝牙地址 <p>
     * 参数: @return <p>
     * 返回: String <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年7月25日 下午12:00:52
     */

    public static String getBlueTooth() {
        try {
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            if (btAdapter.getAddress() != null) {
                return btAdapter.getAddress().replaceAll(":", "-");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 方法: getMAC <p>
     * 描述: 获取MAC地址 <p>
     * 参数: @return <p>
     * 返回: String <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年7月25日 下午12:01:00
     */
    public static String getMAC() {
        try {
            WifiManager wifi = (WifiManager) BaseApplication.getContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            if (info != null) {
                String macAdress = info.getMacAddress();
                return macAdress;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 方法: getBRAND <p>
     * 描述: 获取设备品牌 <p>
     */
    public static String getBRAND() {
        return Build.BRAND;
    }


    /**
     * 方法: getBRAND <p>
     * 描述: 获取设备品牌 <p>
     */
    public static String getManufacture() {
        return Build.MANUFACTURER;
    }

    /**
     * 方法: getModel <p>
     * 描述: 获取设备型号 <p>
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 方法: getOSVesion <p>
     * 描述: 获取操作系统版本 <p>
     */
    public static String getOSVesion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 方法: getSDKVersion <p>
     * 描述: 获取android sdk版本信息<p>
     */
    public static int getSDKVersion() {
      return Build.VERSION.SDK_INT;

    }

    //获取设备唯一标识
    public static String getUniqueId(Context context) {
        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = androidID + Build.SERIAL;//adnroidId + 硬件序列号
//        return id;
        return MD5Util.md5(id);

    }


    /**
     * 方法: getNetType <p>
     * 描述: 获取网络类型 获取手机卡类型 <p>
     * 参数: @return <p>
     * 返回: String <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年7月25日 下午12:01:47
     */
    public static String getNetType() {
        String type = "";
        try {
            TelephonyManager iPhoneManager = (TelephonyManager) BaseApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            String iNumeric = iPhoneManager.getSimOperator();
            if (iNumeric.length() > 0) {
                if (iNumeric.equals("46000") || iNumeric.equals("46002")) {
                    type = "中国移动";
                } else if (iNumeric.equals("46001")) {
                    type = "中国联通";
                } else if (iNumeric.equals("46003")) {
                    type = "中国电信";
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return type;

    }

    /**
     * 方法: getResolution <p>
     * 描述: 获取分辨率 <p>
     * 参数: @return <p>
     * 返回: Integer[] <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年7月25日 下午12:01:59
     */
    public static Integer[] getResolution() {
        // 在service中也能得到高和宽
        Integer[] a = new Integer[2];
        int width;
        int height;
        WindowManager mWindowManager = (WindowManager) BaseApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        width = mWindowManager.getDefaultDisplay().getWidth();
        height = mWindowManager.getDefaultDisplay().getHeight();
        a[0] = width;
        a[1] = height;
        return a;
    }



    /**
     * 方法: getDeviceId <p>
     * 描述: 获取 device id<p>
     * 参数: @return<p>
     * 返回: String<p>
     * 异常 <p>
     * 作者: nn<p>
     * 时间: 2014-7-31 下午8:31:03<p>
     */
    public static String getOnlyDeviceId() {

        String id = null;

        TelephonyManager tm = (TelephonyManager) BaseApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            id = tm.getDeviceId();
        } else {
            try {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class, String.class);
                id = (String) get.invoke(c, "ro.serialno", "");
                if (TextUtils.isEmpty(id)) {
                    id = Secure.getString(BaseApplication.getContext().getContentResolver(), Secure.ANDROID_ID);
                }
                if (TextUtils.isEmpty(id)) {
                    id = getMacAddressWithNoHandler();
                }
            } catch (Exception ignored) {
                id = getMacAddressWithNoHandler();
            }
        }

        return id;

    }




    /**
     * 方法: getMacAddressWithNoHandler <p>
     * 描述: 返回没有处理过的mac地址<p>
     * 参数: @return<p>
     * 返回: String<p>
     * 异常 <p>
     * 作者: nn<p>
     * 时间: 2014年11月30日 下午2:26:42<p>
     */
    private static String getMacAddressWithNoHandler() {
        WifiManager wifi = (WifiManager) BaseApplication.getContext().getSystemService(Context.WIFI_SERVICE);
        String mac = null;

        if (wifi != null) {
            WifiInfo info = wifi.getConnectionInfo();
            mac = info.getMacAddress();
        }
        return mac;
    }






    /**
     * 描述:获取本地IP信息
     * 作者:nn
     * 时间:2016/1/24 14:16
     * 版本:3.1.6
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return "";
    }




}
