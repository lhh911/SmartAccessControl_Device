package com.xsjqzt.module_main.presenter;

import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.retrofit.RetrofitManager;
import com.jbb.library_common.retrofit.other.BaseBean;
import com.jbb.library_common.retrofit.other.NetListeren;
import com.jbb.library_common.retrofit.other.SubscribeUtils;
import com.jbb.library_common.utils.SharePreferensUtil;
import com.jbb.library_common.utils.ToastUtil;
import com.xsjqzt.module_main.model.user.UserInfoInstance;
import com.xsjqzt.module_main.service.ApiService;

public class RegistrationIdPresenter {
    public void registrationId(String registrationId) {
        SubscribeUtils.subscribe(RetrofitManager.getInstance().getService(ApiService.class)
                .registrationId(KeyContacts.Bearer + UserInfoInstance.getInstance().getToken(), registrationId), BaseBean.class, new NetListeren<BaseBean>() {
            @Override
            public void onSuccess(BaseBean bean) {
                ToastUtil.showCustomToast("设备接入成功");
                SharePreferensUtil.putBoolean(KeyContacts.SP_KEY_REGISTRATIONID,true ,KeyContacts.SP_NAME_JPUSH);
            }
            @Override
            public void onError(Exception e) {
//                super.onError(e);
            }
        });
    }
}
