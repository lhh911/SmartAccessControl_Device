package com.xsjqzt.module_main.model.user;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.xsjqzt.module_main.model.UserInfoResBean;

public class UserInfoInstance implements Parcelable {

    private static UserInfoInstance instance;

    private String key;//加密key
    private String token;
    private String refresh_token;


    private String macAddress;
    private String sn1;
    private String sn2;
    private String door;

    private UserInfoInstance(){}

    protected UserInfoInstance(Parcel in) {
        key = in.readString();
        token = in.readString();
        refresh_token = in.readString();

    }

    public static final Creator<UserInfoInstance> CREATOR = new Creator<UserInfoInstance>() {
        @Override
        public UserInfoInstance createFromParcel(Parcel in) {
            return new UserInfoInstance(in);
        }

        @Override
        public UserInfoInstance[] newArray(int size) {
            return new UserInfoInstance[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(token);
        parcel.writeString(refresh_token);

//        parcel.writeParcelable(accounts, i);
    }



    public static UserInfoInstance getInstance(){
        if(instance == null){
            synchronized (UserInfoInstance.class){
                if(instance == null){
                    instance = new UserInfoInstance();
                }
            }
        }
        return instance;
    }

    public boolean hasLogin() {
        return !TextUtils.isEmpty(token);
    }




    public void iniInstanse(SerializableUserInfo bean) {
        if (bean != null) {
            key = bean.getKey();
            token = bean.getToken();
            refresh_token = bean.getRefresh_token();

        }
    }


    public SerializableUserInfo getSerialzInfo() {
        SerializableUserInfo info = new SerializableUserInfo();
        info.setKey(key);
        info.setToken(token);
        info.setRefresh_token(refresh_token);

        return info;
    }




    public void reset(){
        key = "";
        token = "";
        refresh_token = "";

        UserInfoSerializUtil.serializUserInstance();
    }


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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getSn1() {
        return sn1;
    }

    public void setSn1(String sn1) {
        this.sn1 = sn1;
    }

    public String getSn2() {
        return sn2;
    }

    public void setSn2(String sn2) {
        this.sn2 = sn2;
    }

    public String getDoor() {
        return door;
    }

    public void setDoor(String door) {
        this.door = door;
    }
}
