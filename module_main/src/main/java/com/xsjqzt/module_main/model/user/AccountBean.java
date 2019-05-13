package com.xsjqzt.module_main.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


//当前用户的账号，可能又多个账号
public class AccountBean implements Parcelable {
    /**
     * id : 1
     * name : 劳启明
     * mobile : 13724108079
     * garden_id : 2
     * garden_name : 轩汇豪庭
     * garden_image : /uploads/20170630/59ca5ae30346f12f4e36cc2be2c6774b.png
     * region_id : 2
     * region_name : 一期
     * building_id : 3
     * building_name : 3幢1座
     * room_id : 4
     * room_name : 2901
     * room_size : 98.00
     * parking_space : [{"id":258,"name":"P2902"},{"id":309,"name":"P2901-2"},{"id":1007,"name":"P2904"}]
     */

    private int id;
    private String name;
    private String mobile;
    private int garden_id;
    private String garden_name;
    private String garden_image;
    private int region_id;
    private String region_name;
    private int building_id;
    private String building_name;
    private int room_id;
    private String room_name;
    private String room_size;
    private List<ParkingSpaceBean> parking_space;

    public AccountBean() {
    }


    protected AccountBean(Parcel in) {
        id = in.readInt();
        name = in.readString();
        mobile = in.readString();
        garden_id = in.readInt();
        garden_name = in.readString();
        garden_image = in.readString();
        region_id = in.readInt();
        region_name = in.readString();
        building_id = in.readInt();
        building_name = in.readString();
        room_id = in.readInt();
        room_name = in.readString();
        room_size = in.readString();
        parking_space = in.createTypedArrayList(ParkingSpaceBean.CREATOR);
    }

    public static final Creator<AccountBean> CREATOR = new Creator<AccountBean>() {
        @Override
        public AccountBean createFromParcel(Parcel in) {
            return new AccountBean(in);
        }

        @Override
        public AccountBean[] newArray(int size) {
            return new AccountBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(mobile);
        parcel.writeInt(garden_id);
        parcel.writeString(garden_name);
        parcel.writeString(garden_image);
        parcel.writeInt(region_id);
        parcel.writeString(region_name);
        parcel.writeInt(building_id);
        parcel.writeString(building_name);
        parcel.writeInt(room_id);
        parcel.writeString(room_name);
        parcel.writeString(room_size);
        parcel.writeTypedList(parking_space);
    }



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

    public String getGarden_image() {
        return garden_image;
    }

    public void setGarden_image(String garden_image) {
        this.garden_image = garden_image;
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

    public String getRoom_size() {
        return room_size;
    }

    public void setRoom_size(String room_size) {
        this.room_size = room_size;
    }

    public List<ParkingSpaceBean> getParking_space() {
        return parking_space;
    }

    public void setParking_space(List<ParkingSpaceBean> parking_space) {
        this.parking_space = parking_space;
    }




    public static class ParkingSpaceBean implements Parcelable{
        /**
         * id : 258
         * name : P2902
         */

        private int id;
        private String name;

        public ParkingSpaceBean() {
        }

        protected ParkingSpaceBean(Parcel in) {
            id = in.readInt();
            name = in.readString();
        }

        public static final Creator<ParkingSpaceBean> CREATOR = new Creator<ParkingSpaceBean>() {
            @Override
            public ParkingSpaceBean createFromParcel(Parcel in) {
                return new ParkingSpaceBean(in);
            }

            @Override
            public ParkingSpaceBean[] newArray(int size) {
                return new ParkingSpaceBean[size];
            }
        };

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(id);
            parcel.writeString(name);
        }
    }
}
