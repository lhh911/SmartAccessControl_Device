package com.xsjqzt.module_main.view;

import com.jbb.library_common.basemvp.BaseMvpView;

public interface TokenView extends BaseMvpView {
    void loadKeySuccess(String key);

    void getTokenSuccess();
}
