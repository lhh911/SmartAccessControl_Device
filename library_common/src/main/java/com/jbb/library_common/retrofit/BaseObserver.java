package com.jbb.library_common.retrofit;


import com.jbb.library_common.retrofit.other.BaseBean;

import io.reactivex.Observer;


public abstract class BaseObserver<T extends BaseBean> implements Observer<T> {

    @Override
    public void onError(Throwable e) {
        String message = e.getMessage();
        onFailed(new Exception(message));
    }



    @Override
    public void onNext(T t) {
        if(t.getResultCode() == 0){
            onSuccess(t);
        }else {
            onFailed(new Exception(t.getResultCodeMessage()));
        }
        onEnd();
    }

    public abstract void onSuccess(T result);
    public abstract void onFailed(Exception e);
    public abstract void onEnd();
}
