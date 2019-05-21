package com.xsjqzt.module_main.model;

import com.jbb.library_common.retrofit.other.BaseBean;

import java.util.List;

public class IDCardResBean extends BaseBean {


    private List<CardResBean> data;

    public List<CardResBean> getData() {
        return data;
    }

    public void setData(List<CardResBean> data) {
        this.data = data;
    }
}
