package com.xsjqzt.module_main.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class OpenRecord {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private String sn;

    private int status;//（0 失败[不开门]，1 成功[开门]）

    private String image;//板载摄像头照片

    private String image2;//IPC抓拍照片

    private boolean uploadStatus;// false 未上传，true 上传

    private long createTime;//记录生成时间

    private int ICOrID ; // 1 IC记录， 2 id卡记录

    @Generated(hash = 529022790)
    public OpenRecord(Long id, @NotNull String sn, int status, String image,
            String image2, boolean uploadStatus, long createTime, int ICOrID) {
        this.id = id;
        this.sn = sn;
        this.status = status;
        this.image = image;
        this.image2 = image2;
        this.uploadStatus = uploadStatus;
        this.createTime = createTime;
        this.ICOrID = ICOrID;
    }
    @Generated(hash = 1513091491)
    public OpenRecord() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getSn() {
        return this.sn;
    }
    public void setSn(String sn) {
        this.sn = sn;
    }
    public int getStatus() {
        return this.status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getImage() {
        return this.image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getImage2() {
        return this.image2;
    }
    public void setImage2(String image2) {
        this.image2 = image2;
    }
    public boolean getUploadStatus() {
        return this.uploadStatus;
    }
    public void setUploadStatus(boolean uploadStatus) {
        this.uploadStatus = uploadStatus;
    }
    public long getCreateTime() {
        return this.createTime;
    }
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    public int getICOrID() {
        return this.ICOrID;
    }
    public void setICOrID(int ICOrID) {
        this.ICOrID = ICOrID;
    }


  }
