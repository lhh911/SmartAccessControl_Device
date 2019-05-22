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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

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

    /**
     * 获取MAC地址
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        String mac = "";

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mac = getMacDefault(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mac = getMacAddress();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mac = getMacFromHardware();
        }
        return mac;
    }

    /**
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET" />
     * @return
     */
    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Android 6.0（包括） - Android 7.0（不包括）
     * @return
     */
    private static String getMacAddress() {
        String WifiAddress = "";
        try {
            WifiAddress = new BufferedReader(new FileReader(new File("/sys/class/net/wlan0/address"))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WifiAddress;
    }

    /**
     * Android  6.0 之前（不包括6.0）
     * 必须的权限  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     * @param context
     * @return
     */
    private static String getMacDefault(Context context) {
        String mac = "";
        if (context == null) {
            return mac;
        }

        WifiManager wifi = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return mac;
        }
        WifiInfo info = null;
        try {
            info = wifi.getConnectionInfo();
        } catch (Exception e) {
        }
        if (info == null) {
            return null;
        }
        mac = info.getMacAddress();
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH);
        }
        return mac;
    }


}
