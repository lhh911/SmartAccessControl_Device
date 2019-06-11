package com.xsjqzt.module_main.view;

import com.jbb.library_common.basemvp.BaseMvpView;
import com.xsjqzt.module_main.model.EntranceInfoResBean;

public interface SystemInfoIView extends BaseMvpView {
    void loadDeviceSuccess(EntranceInfoResBean bean);
}
