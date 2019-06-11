package com.xsjqzt.module_main.model;

import com.jbb.library_common.retrofit.other.BaseBean;

public class RefreshTokenResBean extends BaseBean {
    /**
     * data : {"token":""}
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
         * token :
         */

        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
