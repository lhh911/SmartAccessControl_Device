package com.jbb.library_common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by ${lhh} on 2018/5/26.
 */

public class PermissionUtil {

    public static final int permissionRequestCode = 703;

    /**
     *  申请权限
     * @param activity
     * @param permissions
     */
    public static void checkPermission(Activity activity,String[] permissions){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, permissionRequestCode);
                }
            }
        }

    }

    /**
     *  适配7.0以上设备文件访问权限
     * @param mContext
     * @param file 文件路径
     * @return
     */
    public static Uri getFileProviderPath(Context mContext,File file){
        Uri contentUri = Uri.fromFile(file);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileProvider", file);
        }
        return contentUri;
    }

}
