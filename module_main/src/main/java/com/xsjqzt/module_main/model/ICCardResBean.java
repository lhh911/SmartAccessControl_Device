package com.xsjqzt.module_main.model;

import com.jbb.library_common.retrofit.other.BaseBean;
import com.xsjqzt.module_main.greendao.entity.ICCard;

import java.util.List;

public class ICCardResBean extends BaseBean {
    private List<CardResBean> data;

    public List<CardResBean> getData() {
        return data;
    }

    public void setData(List<CardResBean> data) {
        this.data = data;
    }


}
