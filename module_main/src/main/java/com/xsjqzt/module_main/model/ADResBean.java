package com.xsjqzt.module_main.model;

import com.google.gson.annotations.SerializedName;
import com.jbb.library_common.retrofit.other.BaseBean;

import java.util.List;

public class ADResBean extends BaseBean {
    /**
     * data : {"id":1,"name":"大门广告位","type":1,"update_time":1560051435,"list":[{"id":1,"title":"广告一","path":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1560061554316&di=e7184922fa48c014a5608e411b229563&imgtype=0&src=http%3A%2F%2Fimg.redocn.com%2Fsheji%2F20150131%2Fyunnanzhongyanhaoshanhaoshuihaoyanxingxianghaibao_3902591.jpg","url":"http://www.baidu.com"},{"id":2,"title":"广告二","path":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1560061645644&di=acc18f095a747ec5f45d26b3623ba871&imgtype=0&src=http%3A%2F%2Fpic2.cxtuku.com%2F00%2F10%2F25%2Fb911fd1d4696.jpg","url":""}]}
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
         * id : 1
         * name : 大门广告位
         * type : 1
         * update_time : 1560051435
         * list : [{"id":1,"title":"广告一","path":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1560061554316&di=e7184922fa48c014a5608e411b229563&imgtype=0&src=http%3A%2F%2Fimg.redocn.com%2Fsheji%2F20150131%2Fyunnanzhongyanhaoshanhaoshuihaoyanxingxianghaibao_3902591.jpg","url":"http://www.baidu.com"},{"id":2,"title":"广告二","path":"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1560061645644&di=acc18f095a747ec5f45d26b3623ba871&imgtype=0&src=http%3A%2F%2Fpic2.cxtuku.com%2F00%2F10%2F25%2Fb911fd1d4696.jpg","url":""}]
         */

        private int id;
        private String name;
        private int type;
        private int update_time;
        private List<ListBean> list;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(int update_time) {
            this.update_time = update_time;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * id : 1
             * title : 广告一
             * path : https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1560061554316&di=e7184922fa48c014a5608e411b229563&imgtype=0&src=http%3A%2F%2Fimg.redocn.com%2Fsheji%2F20150131%2Fyunnanzhongyanhaoshanhaoshuihaoyanxingxianghaibao_3902591.jpg
             * url : http://www.baidu.com
             */

            private int id;
            private String title;
            private String path;
            @SerializedName("url")
            private String urlX;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getUrlX() {
                return urlX;
            }

            public void setUrlX(String urlX) {
                this.urlX = urlX;
            }
        }
    }
}
