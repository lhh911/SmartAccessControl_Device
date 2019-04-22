package com.jbb.library_common.basemvp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jbb.library_common.R;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.StatusUtil;
import com.jbb.library_common.widght.LoadDialog;


/**
 * Created by ${lhh} on 2017/11/27.
 */

public abstract class BaseMvpActivity <V extends BaseMvpView,T extends BaseMvpPresenter<V>> extends FragmentActivity implements View.OnClickListener {
    public T presenter;

    private LoadDialog loadDialog;

    protected TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        ActivityManager.getInstance().pushActivity(this);

        setStatusColor();
        setSystemInvadeBlack();

        presenter = initPresenter();
        presenter.attach((V)this);

        mTitle = (TextView) findViewById(R.id.title);
        if(mTitle != null)
            mTitle.setText(getATitle());

        ImageView backIv = (ImageView) findViewById(R.id.back_iv);
        if(backIv != null)
            backIv.setOnClickListener(this);

        init();


        //内存被回收后，重走app
        switch (ActivityManager.getInstance().getAppStatus()) {
            case KeyContacts.STATUS_FORCE_KILLED:
                restartApp();
                break;
            case KeyContacts.STATUS_NORMAL:
                break;
            default:
                break;
        }


    }

    protected void restartApp() {
        ARouter.getInstance().build("/module_main/main")
                .withInt(KeyContacts.START_LAUNCH_ACTION,KeyContacts.STATUS_FORCE_KILLED).navigation();//

//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra(KeyContacts.START_LAUNCH_ACTION,KeyContacts.STATUS_FORCE_KILLED);
//        startActivity(intent);
    }



    @Override
    protected void onDestroy() {
        presenter.dettach();
        dismiss();
        ActivityManager.getInstance().popActivity(this);
        super.onDestroy();
    }



    public void show(String text) {
        if (loadDialog == null)
            loadDialog = new LoadDialog(this, text);

        if(!TextUtils.isEmpty(text))
            loadDialog.setText(text);
        loadDialog.show();
    }

    public void dismiss() {
        if(loadDialog != null )
            loadDialog.dismiss();
    }




    //不同页面需要改状态栏颜色时重写此方法
    protected void setStatusColor() {
        StatusUtil.setUseStatusBarColor(this, Color.TRANSPARENT);
    }

    protected void setSystemInvadeBlack() {
        // 第二个参数是是否沉浸,第三个参数是状态栏字体是否为黑色
        StatusUtil.setSystemStatus(this, false, false);
    }


    public void goTo(Class<?> to){
        Intent it = new Intent(this,to);
        startActivity(it);
        entenAnim();
    }
    public void goTo(Class<?> to,Bundle bundle){
        Intent it = new Intent(this,to);
        it.putExtras(bundle);
        startActivity(it);
        entenAnim();
    }

    public void goToForResult(Class<?> to,int requestCode){
        Intent it = new Intent(this,to);
        startActivityForResult(it,requestCode);
        entenAnim();
    }

    public void goToForResult(Class<?> to,Bundle bundle,int requestCode){
        Intent it = new Intent(this,to);
        it.putExtras(bundle);
        startActivityForResult(it,requestCode);
        entenAnim();
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.back_iv){
                finish();
                exitAnim();

        }
    }

    @Override
    public void onBackPressed() {
        finish();
        exitAnim();
    }

    public void entenAnim(){
        overridePendingTransition(R.anim.menu_bottombar_in,R.anim.no_anim);
    }

    public void exitAnim(){
        overridePendingTransition(R.anim.no_anim,R.anim.menu_bottombar_out);
    }

    // 实例化presenter
    public abstract T initPresenter();

    public abstract int getLayoutId();
    public abstract void init();
    public abstract String getATitle();
}