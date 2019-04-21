package com.jbb.library_common.utils;

import android.widget.Toast;

import com.jbb.library_common.BaseApplication;


public class ToastUtil {

//    private static Toast customToast;


    /**
     * 描述:显示自定义toast  无需context
     * 作者:nn
     * 时间:2016/5/5 17:12
     * 版本:3.2.3
     */
    public static void showCustomToast(String msg) {
//        if (customToast == null) {
////            customToast = new Toast(MyApplication.getContext());
////            LayoutInflater inflate = (LayoutInflater)
////                    MyApplication.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////            View v = inflate.inflate(com.android.internal.R.layout.transient_notification, null);
////            TextView tv = (TextView)v.findViewById(com.android.internal.R.id.message);
////            tv.setText(text);
////        } else {
////            ((TextView) customToast.getView().findViewById(R.id.toast_message_tv)).setText(msg);
////        }
//        customToast.show();
        Toast.makeText(BaseApplication.getContext(),msg,Toast.LENGTH_SHORT).show();
    }

//    public static void showBottomCustomToast(String msg) {
//        if (customToast == null) {
//            customToast = new Toast(NnApplication.getContext());
//            View view = LayoutInflater.from(NnApplication.getContext()).inflate(R.layout.toast_view, null);
//            TextView message = (TextView) view.findViewById(R.id.toast_message_tv);
//            message.setText(msg);
//            customToast.setView(view);
//            customToast.setDuration(Toast.LENGTH_SHORT);
//        } else {
//            ((TextView) customToast.getView().findViewById(R.id.toast_message_tv)).setText(msg);
//        }
//        customToast.show();
//    }

//    public static void showLongToast(String msg) {
//        if (customToast == null) {
//            customToast = new Toast(NnApplication.getContext());
//            View view = LayoutInflater.from(NnApplication.getContext()).inflate(R.layout.toast_view, null);
//            TextView tvMsg = (TextView) view.findViewById(R.id.toast_message_tv);
//            tvMsg.setText(msg);
//            customToast.setGravity(Gravity.CENTER, 0, 0);
//            customToast.setView(view);
//            customToast.setDuration(Toast.LENGTH_LONG);
//        } else {
//            ((TextView) customToast.getView().findViewById(R.id.toast_message_tv)).setText(msg);
//        }
//        customToast.show();
//    }

}