package com.xsjqzt.module_main.model;

import com.jbb.library_common.retrofit.other.BaseBean;

/**
 * description ： TODO:类的作用
 * author : lhh
 * date : 2019/9/9 10:49
 */
public class VersionResBean extends BaseBean {
    /**
     * data : {"local_version":"2.0.7","remote_version":"2.0.6","force":1,"upgrade":true,"path":"https://xxxxx.xxxx.com/1111.zip"}
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
         * local_version : 2.0.7
         * remote_version : 2.0.6
         * force : 1
         * upgrade : true
         * path : https://xxxxx.xxxx.com/1111.zip
         */

        private String local_version;
        private String remote_version;
        private int force;
        private boolean upgrade;
        private String path;

        public String getLocal_version() {
            return local_version;
        }

        public void setLocal_version(String local_version) {
            this.local_version = local_version;
        }

        public String getRemote_version() {
            return remote_version;
        }

        public void setRemote_version(String remote_version) {
            this.remote_version = remote_version;
        }

        public int getForce() {
            return force;
        }

        public void setForce(int force) {
            this.force = force;
        }

        public boolean isUpgrade() {
            return upgrade;
        }

        public void setUpgrade(boolean upgrade) {
            this.upgrade = upgrade;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
