package com.jbb.library_common;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.bumptech.glide.request.RequestOptions;


public class BaseApplication extends Application {


    //圆角
    public static RequestOptions glideOptionsCrop;

    public static Context mContext;
    public static String mobileName;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mobileName = Build.MANUFACTURER.toLowerCase();

    }


}
