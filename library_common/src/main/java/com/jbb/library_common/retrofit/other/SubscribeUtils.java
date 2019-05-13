package com.jbb.library_common.retrofit.other;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.jbb.library_common.BaseApplication;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.DeviceUtil;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class SubscribeUtils {

    public static <T> void subscribe(Observable<ResponseBody> observable, final Class<T> resultClass, final NetListeren<T> listeren) {
        if (!DeviceUtil.isNetWorkEnable()) {
            if (listeren != null) {
                listeren.onError(new NetException(HttpRespStatus.SC_NET_NO_CONNECTION_ERROR));
            }
            return;
        }
        if (listeren != null) {
            listeren.onStart();
        }
        observable.flatMap(new Function<ResponseBody, Observable<T>>() {

                    @Override
                    public Observable apply(ResponseBody response) throws Exception {
                        BaseBean baseBean = null;
                        String result = response.string();
                        baseBean = JSON.parseObject(result, BaseBean.class);
                        if(baseBean == null){
                            return Observable.error(new Throwable(HttpRespStatus.MSG_UNKNOWN_ERROR));
                        }

                        if(baseBean.getCode() == 0){
                            T bean = JSON.parseObject(result, resultClass);
                            return Observable.just(bean);
                        }else{
                            if(baseBean.getCode() == 2001 || baseBean.getCode() == 2002){//2001 token 过期， 2002 Refresh Token 过期
                                Intent it = new Intent(KeyContacts.ACTION_API_KEY_INVALID);
                                it.putExtra("code" ,baseBean.getCode());
                                BaseApplication.getContext().sendBroadcast(it);
                            }
                            return Observable.error(new NetException(baseBean.getCode(),baseBean.getMessage()));
                        }

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(T o) {
                        if(listeren != null) {
                            listeren.onSuccess(o);
                            listeren.onEnd();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(listeren != null)
                            listeren.onError((Exception) e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
