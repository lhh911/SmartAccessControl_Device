package com.xsjqzt.module_main.model;

import com.jbb.library_common.retrofit.other.BaseBean;

public class UserInfoResBean extends BaseBean {

    public UserInfoResBean() {
    }
    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    /**
     * id : 1138
     * name : 劳启
     * mobile : 13724108079
     * sex : 1
     * birthday : 2019-01-01
     * avatar :
     * garden_id : 4
     * garden_name : 安信科技办公室
     * region_id : 3
     * region_name : 1期
     * building_id : 10
     * building_name : A栋
     * room_id : 274
     * room_name : A001
     * room_size : 120.0
     * group_id : 0
     */
    public static class DataBean {
        private int id;
        private String name;
        private String mobile;
        private int sex;
        private String birthday;
        private String avatar;
        private int garden_id;
        private String garden_name;
        private int region_id;
        private String region_name;
        private int building_id;
        private String building_name;
        private int room_id;
        private String room_name;
        private double room_size;
        private int group_id;



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

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
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

        public int getRoom_id() {
            return room_id;
        }

        public void setRoom_id(int room_id) {
            this.room_id = room_id;
        }

        public String getRoom_name() {
            return room_name;
        }

        public void setRoom_name(String room_name) {
            this.room_name = room_name;
        }

        public double getRoom_size() {
            return room_size;
        }

        public void setRoom_size(double room_size) {
            this.room_size = room_size;
        }

        public int getGroup_id() {
            return group_id;
        }

        public void setGroup_id(int group_id) {
            this.group_id = group_id;
        }
    }
}
