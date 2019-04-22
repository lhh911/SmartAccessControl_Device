package com.xsjqzt.module_main.ui;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jbb.library_common.basemvp.BaseMvpActivity;
import com.jbb.library_common.utils.CommUtil;
import com.jbb.library_common.utils.StatusUtil;
import com.jbb.library_common.utils.ToastUtil;
import com.jbb.library_common.utils.Utils;
import com.xsjqzt.module_main.R;
import com.xsjqzt.module_main.presenter.RoomNumPswUnlockPresenter;
import com.xsjqzt.module_main.view.RoomNumPswUnlockIView;
import com.xsjqzt.module_main.widget.ImgTextView;

import java.util.Timer;
import java.util.TimerTask;

/***
 * 房号密码开锁
 */
public class RoomNumPswUnlockActivity extends BaseMvpActivity<RoomNumPswUnlockIView, RoomNumPswUnlockPresenter> implements RoomNumPswUnlockIView {

    private EditText editText;
    private LinearLayout inputLayout;
    private ImgTextView successLayout, errorLayout;
    private int showSucOrError = 1; // 1 提交成功 ，2 提交错误


    @Override
    public RoomNumPswUnlockPresenter initPresenter() {
        return new RoomNumPswUnlockPresenter();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_room_num_psw_unlock;
    }

    @Override
    public void init() {
        editText = findViewById(R.id.room_psw_et);
        TextView bottomTv = findViewById(R.id.bottom_tv);

        Drawable drawable = getResources().getDrawable(R.mipmap.icon_gth);
        drawable.setBounds(0, 0, CommUtil.dp2px(13), CommUtil.dp2px(13));
        bottomTv.setCompoundDrawables(drawable, null, null, null);

        inputLayout = findViewById(R.id.input_layout);
        successLayout = findViewById(R.id.success_layout);
        errorLayout = findViewById(R.id.error_layout);


        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    Utils.hideKeyboard(editText);
                    commit();
                }
                return true;
            }
        });
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

    public void commit() {
        String input = editText.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            ToastUtil.showCustomToast("请输入账号或密码");
            return;
        }

        showSucOrError = 2;

        setShowSucOrError();
    }

    private void setShowSucOrError() {
        if (showSucOrError == 1) {
            inputLayout.setVisibility(View.GONE);
            successLayout.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        } else {
            inputLayout.setVisibility(View.GONE);
            successLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
        }

        starTime();
    }

    private void starTime() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (showSucOrError == 1) {
                            finish();
                        } else {
                            inputLayout.setVisibility(View.VISIBLE);
                            successLayout.setVisibility(View.GONE);
                            errorLayout.setVisibility(View.GONE);
                        }
                    }
                });
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 3000);
    }


}
