package com.xsjqzt.module_main.view;

import com.jbb.library_common.basemvp.BaseMvpView;
import com.xsjqzt.module_main.model.CardResBean;
import com.xsjqzt.module_main.model.EntranceDetailsResBean;

public interface MainView extends BaseMvpView {
    void loadKeySuccess(String key);

    void getTokenSuccess();

    void entranceDetailSuccess(EntranceDetailsResBean bean);

    void loadIDCardsSuccess(CardResBean bean);

    void loadICCardsSuccess(CardResBean bean);
}
