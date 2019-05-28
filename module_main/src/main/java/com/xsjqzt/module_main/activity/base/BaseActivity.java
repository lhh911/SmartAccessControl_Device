package com.xsjqzt.module_main.activity.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.xsjqzt.module_main.Config.DemoConfig;

import com.xsjqzt.module_main.util.SharedPrefUtils;


/**
 * 所有Activity基类
 */
public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    public DemoConfig mConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
        hideBottomUIMenu();
        initConfig();
    }


    @Override
    protected void onResume() {
        hideBottomUIMenu();
        super.onResume();
       // CPUUtil.setScalingMaxFreq("1274000");
    }

    protected abstract void initData();

    protected abstract void initView();

    protected abstract void initEvent();

    /**
     * 显示ShortToast
     *
     * @param context
     * @param content
     */
    public void showShortToast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示LongToast
     *
     * @param context
     * @param content
     */
    protected void showLongToast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }

    /**
     * 显示进度对话框不可手动取消
     */
    protected void showProgressDialog(Context context, String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMessage(message);
        }
        mProgressDialog.show();
    }

    /**
     * 显示进度对话框可手动取消
     */
    protected void showCancelableProgressDialog(Context context, String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.setMessage(message);
        }
        mProgressDialog.show();
    }

    /**
     * 取消显示进度对话框
     */
    protected void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void initConfig() {
        mConfig = SharedPrefUtils.getObject(ExApplication.getContext(), "DEMO_CONFIG", DemoConfig.class);
        if (mConfig == null) {
            mConfig = new DemoConfig();
            SharedPrefUtils.putObject(ExApplication.getContext(), "DEMO_CONFIG", mConfig);
        }
    }

    protected void hideBottomUIMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);//API19
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
    }


}
