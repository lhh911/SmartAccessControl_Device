package com.xsjqzt.module_main.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.jbb.library_common.retrofit.other.BaseBean;

import java.util.ArrayList;
import java.util.List;

public class FaceImageResBean extends BaseBean implements Parcelable{
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

        private int id;
        private String image;
        private int status;
        private String mobile;

        public DataBean() {
        }

        protected DataBean(Parcel in) {
            id = in.readInt();
            image = in.readString();
            status = in.readInt();
            mobile = in.readString();
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

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(image);
            dest.writeInt(status);
            dest.writeString(mobile);
        }
    }
}
