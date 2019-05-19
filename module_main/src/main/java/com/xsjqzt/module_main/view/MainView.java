package com.xsjqzt.module_main.view;

import com.jbb.library_common.basemvp.BaseMvpView;
import com.xsjqzt.module_main.model.ICCardResBean;
import com.xsjqzt.module_main.model.EntranceDetailsResBean;

public interface MainView extends BaseMvpView {
    void loadKeySuccess(String key);

    void getTokenSuccess();

    void entranceDetailSuccess(EntranceDetailsResBean bean);

//    void loadIDCardsSuccess(ICCardResBean bean);

//    void loadICCardsSuccess(ICCardResBean bean);
}
