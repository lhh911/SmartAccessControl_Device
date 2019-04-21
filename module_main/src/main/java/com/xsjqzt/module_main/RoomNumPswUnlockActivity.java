package com.xsjqzt.module_main;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.jbb.library_common.basemvp.BaseMvpActivity;
import com.jbb.library_common.utils.CommUtil;
import com.jbb.library_common.utils.Utils;
import com.xsjqzt.module_main.presenter.RoomNumPswUnlockPresenter;
import com.xsjqzt.module_main.view.RoomNumPswUnlockIView;

/***
 * 房号密码开锁
 */
public class RoomNumPswUnlockActivity extends BaseMvpActivity<RoomNumPswUnlockIView,RoomNumPswUnlockPresenter> implements RoomNumPswUnlockIView {

    private EditText editText;


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
        drawable.setBounds(0,0, CommUtil.dp2px(13),CommUtil.dp2px(13));
        bottomTv.setCompoundDrawables(drawable,null,null,null);



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
}
