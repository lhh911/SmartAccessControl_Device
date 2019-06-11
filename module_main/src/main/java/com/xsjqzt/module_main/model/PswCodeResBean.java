package com.xsjqzt.module_main.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.jbb.library_common.retrofit.other.BaseBean;

import java.util.List;

public class PswCodeResBean extends BaseBean {
    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 18
         * code : 62660
         * user_id : 1546
         * update_time : 1559186266
         * expiry_time : 1559193466
         * is_delete : true
         */

        private int id;
        @JSONField(name = "code")
        private String codeX;
        private int user_id;
        private int update_time;
        private int expiry_time;
        private boolean is_delete;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCodeX() {
            return codeX;
        }

        public void setCodeX(String codeX) {
            this.codeX = codeX;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(int update_time) {
            this.update_time = update_time;
        }

        public int getExpiry_time() {
            return expiry_time;
        }

        public void setExpiry_time(int expiry_time) {
            this.expiry_time = expiry_time;
        }

        public boolean isIs_delete() {
            return is_delete;
        }

        public void setIs_delete(boolean is_delete) {
            this.is_delete = is_delete;
        }
    }
}
