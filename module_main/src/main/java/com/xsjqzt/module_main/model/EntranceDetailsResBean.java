package com.xsjqzt.module_main.model;

import com.jbb.library_common.retrofit.other.BaseBean;

public class EntranceDetailsResBean extends BaseBean {
    /**
     * data : {"id":1,"name":"1号门","sn":"1234567890","status":0,"volume":10,"distance_type":1,"garden_id":4,"garden_name":"安信科技办公室","region_id":3,"region_name":"1期","building_id":10,"building_name":"A栋"}
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
         * name : 1号门
         * sn : 1234567890
         * status : 0
         * volume : 10
         * distance_type : 1
         * garden_id : 4
         * garden_name : 安信科技办公室
         * region_id : 3
         * region_name : 1期
         * building_id : 10
         * building_name : A栋
         */

        private int id;
        private String name;
        private String sn;
        private int status;
        private int volume;
        private int distance_type;
        private int garden_id;
        private String garden_name;
        private int region_id;
        private String region_name;
        private int building_id;
        private String building_name;

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

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getVolume() {
            return volume;
        }

        public void setVolume(int volume) {
            this.volume = volume;
        }

        public int getDistance_type() {
            return distance_type;
        }

        public void setDistance_type(int distance_type) {
            this.distance_type = distance_type;
        }

        public int getGarden_id() {
            return garden_id;
        }

        public void setGarden_id(int garden_id) {
            this.garden_id = garden_id;
        }

        public String getGarden_name() {
            return garden_name;
        }

        public void setGarden_name(String garden_name) {
            this.garden_name = garden_name;
        }

        public int getRegion_id() {
            return region_id;
        }

        public void setRegion_id(int region_id) {
            this.region_id = region_id;
        }

        public String getRegion_name() {
            return region_name;
        }

        public void setRegion_name(String region_name) {
            this.region_name = region_name;
        }

        public int getBuilding_id() {
            return building_id;
        }

        public void setBuilding_id(int building_id) {
            this.building_id = building_id;
        }

        public String getBuilding_name() {
            return building_name;
        }

        public void setBuilding_name(String building_name) {
            this.building_name = building_name;
        }
    }
}
