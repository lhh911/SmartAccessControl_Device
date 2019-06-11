package com.xsjqzt.module_main.model;

import com.jbb.library_common.retrofit.other.BaseBean;

public class TokenResBean extends BaseBean {
    /**
     * data : {"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1NTY0NDQ5NTYsImV4cCI6MTU1NjUzMTM1NiwidXNlciI6eyJzbiI6IjEyMzQ1Njc4OTAifX0.P2TroNFWsFLYjKQqvnT7FY4gwTb0po7WxcM22n_mPTc","refresh_token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1NTY0NDQ5NTYsImV4cCI6MTU1OTAzNjk1NiwidXNlciI6eyJzbiI6IjEyMzQ1Njc4OTAifX0.cM_wFC45a1FGe6U9Si6GuqWz2-KAC3hAajeO2iqYH6s"}
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
         * token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1NTY0NDQ5NTYsImV4cCI6MTU1NjUzMTM1NiwidXNlciI6eyJzbiI6IjEyMzQ1Njc4OTAifX0.P2TroNFWsFLYjKQqvnT7FY4gwTb0po7WxcM22n_mPTc
         * refresh_token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1NTY0NDQ5NTYsImV4cCI6MTU1OTAzNjk1NiwidXNlciI6eyJzbiI6IjEyMzQ1Njc4OTAifX0.cM_wFC45a1FGe6U9Si6GuqWz2-KAC3hAajeO2iqYH6s
         */

        private String token;
        private String refresh_token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getRefresh_token() {
            return refresh_token;
        }

        public void setRefresh_token(String refresh_token) {
            this.refresh_token = refresh_token;
        }
    }
}
