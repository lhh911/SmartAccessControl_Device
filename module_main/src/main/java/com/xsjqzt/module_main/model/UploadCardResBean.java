package com.xsjqzt.module_main.model;

import com.jbb.library_common.retrofit.other.BaseBean;

public class UploadCardResBean extends BaseBean {

    /**
     * data : {"id":37}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 37
         */

        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
