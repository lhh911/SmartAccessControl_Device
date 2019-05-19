package com.xsjqzt.module_main.model;

import com.jbb.library_common.retrofit.other.BaseBean;
import com.xsjqzt.module_main.greendao.entity.ICCard;
import com.xsjqzt.module_main.greendao.entity.IDCard;

import java.util.List;

public class IDCardResBean extends BaseBean {
    private List<IDCard> data;

    public List<IDCard> getData() {
        return data;
    }

    public void setData(List<IDCard> data) {
        this.data = data;
    }


}
