package com.jbb.library_common.retrofit.other;

import android.os.Looper;

import com.jbb.library_common.comfig.AppConfig;
import com.jbb.library_common.utils.log.LogUtil;
import com.jbb.library_common.utils.ToastUtil;

public abstract class NetListeren<T> {
    public void onStart() {

    }

    public void onEnd() {
    }

    public void onError(Exception e) {
        if (AppConfig.LOG_SWITCH_FLAG == LogUtil.LOG_ON) {
            e.printStackTrace();
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            if (e instanceof NetException) {
                NetException exception = (NetException) e;
                ToastUtil.showCustomToast(exception.message());
            } else {//其他异常都要统计
                ToastUtil.showCustomToast(HttpRespStatus.MSG_UNKNOWN_ERROR);
            }
        }

        onEnd();
    }

    public abstract void onSuccess(T t);
}
