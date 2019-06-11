package com.jbb.library_common.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jbb.library_common.BaseApplication;

public class GlideUtils {

    public static void display(Context mContext, String url, ImageView view) {
        Glide.with(mContext).load(url).into(view);
    }
    public static void display(Context mContext, String url, ImageView view, RequestOptions glideOptions) {
        Glide.with(mContext).load(url).apply(glideOptions).into(view);
    }


    public static void displayCornersCrop(Context mContext, String url, ImageView view) {

        Glide.with(mContext).load(url).apply(BaseApplication.glideOptionsCrop).into(view);
    }
}
