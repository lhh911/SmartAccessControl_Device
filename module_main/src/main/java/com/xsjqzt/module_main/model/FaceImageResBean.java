package com.xsjqzt.module_main.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.jbb.library_common.retrofit.other.BaseBean;

import java.util.ArrayList;

public class FaceImageResBean extends BaseBean implements Parcelable {
    private ArrayList<DataBean> data;

    public FaceImageResBean() {
    }

    protected FaceImageResBean(Parcel in) {
        data = in.createTypedArrayList(DataBean.CREATOR);
    }

    public static final Creator<FaceImageResBean> CREATOR = new Creator<FaceImageResBean>() {
        @Override
        public FaceImageResBean createFromParcel(Parcel in) {
            return new FaceImageResBean(in);
        }

        @Override
        public FaceImageResBean[] newArray(int size) {
            return new FaceImageResBean[size];
        }
    };

    public ArrayList<DataBean> getData() {
        return data;
    }

    public void setData(ArrayList<DataBean> data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(data);
    }

    public static class DataBean implements Parcelable {
        /**
         * id : 1
         * image : /static/user_faces/20190522/9dc25af172ef60efe41294d626e443c5.jpg
         * status : 1
         * mobile : 13724108079
         */

        private int user_id;
        private String image;
        private int status;
        private int update_time;
        private boolean is_delete;
        @JSONField(name = "code")
        private String codeX;

        public DataBean() {
        }


        protected DataBean(Parcel in) {
            user_id = in.readInt();
            image = in.readString();
            status = in.readInt();
            update_time = in.readInt();
            is_delete = in.readByte() != 0;
            codeX = in.readString();
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel in) {
                return new DataBean(in);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(user_id);
            dest.writeString(image);
            dest.writeInt(status);
            dest.writeInt(update_time);
            dest.writeByte((byte) (is_delete ? 1 : 0));
            dest.writeString(codeX);
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(int update_time) {
            this.update_time = update_time;
        }

        public boolean isIs_delete() {
            return is_delete;
        }

        public void setIs_delete(boolean is_delete) {
            this.is_delete = is_delete;
        }

        public String getCodeX() {
            return codeX;
        }

        public void setCodeX(String codeX) {
            this.codeX = codeX;
        }
    }
}
