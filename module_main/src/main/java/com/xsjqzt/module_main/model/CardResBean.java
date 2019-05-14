package com.xsjqzt.module_main.model;

import com.jbb.library_common.retrofit.other.BaseBean;

import java.util.List;

public class CardResBean extends BaseBean {
    private List<CardBean> data;

    public List<CardBean> getData() {
        return data;
    }

    public void setData(List<CardBean> data) {
        this.data = data;
    }


}
