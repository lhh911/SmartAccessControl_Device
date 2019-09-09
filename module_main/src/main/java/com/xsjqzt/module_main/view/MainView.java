package com.xsjqzt.module_main.view;

import com.jbb.library_common.basemvp.BaseMvpView;
import com.xsjqzt.module_main.model.ICCardResBean;
import com.xsjqzt.module_main.model.EntranceDetailsResBean;
import com.xsjqzt.module_main.model.RoomNumByUserIdResBean;
import com.xsjqzt.module_main.model.VersionResBean;

public interface MainView extends BaseMvpView {
    void loadKeySuccess(String key);

    void getTokenSuccess();

    void uploadCardSuccess(int type, int id ,String sn);

    void entranceDetailSuccess(EntranceDetailsResBean bean);

    void getUseridByRoomSuccess(boolean b, int userId,String roomNum);

    void loadBannerSuccess();

    void checkVersionSuccess(VersionResBean bean);

//    void loadIDCardsSuccess(ICCardResBean bean);

//    void loadICCardsSuccess(ICCardResBean bean);
}
