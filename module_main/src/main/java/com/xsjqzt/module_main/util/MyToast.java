package com.xsjqzt.module_main.util;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jbb.library_common.BaseApplication;
import com.jbb.library_common.utils.CommUtil;
import com.xsjqzt.module_main.R;

import java.io.IOException;

public class MyToast {

    public static void showToast(String str, int id,String color) {
        Toast customToast = new Toast(BaseApplication.getContext());
        View view = LayoutInflater.from(BaseApplication.getContext()).inflate(R.layout.opendoor_status_view, null);
        TextView message = view.findViewById(R.id.status_tv);
        ImageView imageView = view.findViewById(R.id.status_iv);
        LinearLayout parentLl = view.findViewById(R.id.parent_ll);
        message.setText(str);
        message.setTextColor(Color.parseColor(color));
        imageView.setImageResource(id);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,CommUtil.dp2px(200));
        parentLl.setLayoutParams(params);
        customToast.setView(view);
        customToast.setGravity(Gravity.BOTTOM|Gravity.FILL_HORIZONTAL,0,0);
        customToast.setDuration(Toast.LENGTH_LONG);
        customToast.show();
    }

    //重启
    public static void reboot() {
        try {
            Process exec = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot -p"});
            int i = exec.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
