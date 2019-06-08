package com.xsjqzt.module_main.view;

import com.jbb.library_common.basemvp.BaseMvpView;
import com.xsjqzt.module_main.model.ICCardResBean;
import com.xsjqzt.module_main.model.EntranceDetailsResBean;
import com.xsjqzt.module_main.model.RoomNumByUserIdResBean;

public interface MainView extends BaseMvpView {
    void loadKeySuccess(String key);

    void getTokenSuccess();

    void uploadCardSuccess(int type, int id ,String sn);

    void entranceDetailSuccess(EntranceDetailsResBean bean);

    void getUseridByRoomSuccess(boolean b, String userId,String roomNum);

//    void loadIDCardsSuccess(ICCardResBean bean);

//    void loadICCardsSuccess(ICCardResBean bean);
}
