package com.xsjqzt.module_main.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class FaceImage {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    @Unique
    private int sid;//服务器表中id

    @NotNull
    private String image;//

    private String ymimage;//阅面注册成功后的识别图片


    private int status;// 人脸图片状态，1 待检验，2 有效， 3无效

    private boolean hasregist;// 是否已经注册成功阅面


    private String mobile;//


    @Generated(hash = 1497177014)
    public FaceImage(Long id, int sid, @NotNull String image, String ymimage,
            int status, boolean hasregist, String mobile) {
        this.id = id;
        this.sid = sid;
        this.image = image;
        this.ymimage = ymimage;
        this.status = status;
        this.hasregist = hasregist;
        this.mobile = mobile;
    }


    @Generated(hash = 1755518208)
    public FaceImage() {
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public int getSid() {
        return this.sid;
    }


    public void setSid(int sid) {
        this.sid = sid;
    }


    public String getImage() {
        return this.image;
    }


    public void setImage(String image) {
        this.image = image;
    }


    public String getYmimage() {
        return this.ymimage;
    }


    public void setYmimage(String ymimage) {
        this.ymimage = ymimage;
    }


    public int getStatus() {
        return this.status;
    }


    public void setStatus(int status) {
        this.status = status;
    }


    public boolean getHasregist() {
        return this.hasregist;
    }


    public void setHasregist(boolean hasregist) {
        this.hasregist = hasregist;
    }


    public String getMobile() {
        return this.mobile;
    }


    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


  }
