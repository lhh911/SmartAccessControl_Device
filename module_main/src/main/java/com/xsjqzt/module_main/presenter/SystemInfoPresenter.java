package com.xsjqzt.module_main.presenter;

import com.jbb.library_common.basemvp.BaseMvpPresenter;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.retrofit.RetrofitManager;
import com.jbb.library_common.retrofit.other.NetListeren;
import com.jbb.library_common.retrofit.other.SubscribeUtils;
import com.xsjqzt.module_main.model.EntranceInfoResBean;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.service.ApiService;
import com.xsjqzt.module_main.view.SystemInfoIView;

public class SystemInfoPresenter extends BaseMvpPresenter<SystemInfoIView> {
    public void loadDevice() {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .entranceDetail(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken()), EntranceInfoResBean.class, new NetListeren<EntranceInfoResBean>() {
            @Override
            public void onSuccess(EntranceInfoResBean bean) {
                if (mView != null) {
                    mView.loadDeviceSuccess(bean);
                }

            }

            @Override
            public void onStart() {
                if (mView != null)
                    mView.showLoading();
            }

            @Override
            public void onEnd() {
                if (mView != null)
                    mView.hideLoading();
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
            }
        });
    }
}
