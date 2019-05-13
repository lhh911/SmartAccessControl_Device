/**  
 * 工程: Coyote<p>
 * 标题: SerializableUserInfo.java<p>
 * 包:   com.niuniucaip.lotto.mainact.personnal.loginmodule.model<p>
 * 描述: TODO<p>
 * 作者: nn<p>
 * 时间: 2014年11月22日 下午3:07:07<p>
 * 版权: Copyright 2014 Shenzhen NiuNiucaip Tech Co.,Ltd.<p>
 * All rights reserved.<p>
 *
 */

package com.xsjqzt.module_main.model.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 类: SerializableUserInfo<p>
 *
 */
public class SerializableUserInfo implements Parcelable {
    private String key;
    private String token;
    private String refresh_token;



    public SerializableUserInfo() {
    }

    protected SerializableUserInfo(Parcel in) {
        key = in.readString();
        token = in.readString();
        refresh_token = in.readString();

    }

    public static final Creator<SerializableUserInfo> CREATOR = new Creator<SerializableUserInfo>() {
        @Override
        public SerializableUserInfo createFromParcel(Parcel in) {
            return new SerializableUserInfo(in);
        }

        @Override
        public SerializableUserInfo[] newArray(int size) {
            return new SerializableUserInfo[size];
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

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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


}
