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

    private int sid;//服务器此条记录id，传图片是用

    private boolean uploadStatus;// false 未上传，true 上传

    private long createTime;//记录生成时间

    private String type ; //照片来源 ，"": 默认拍照， "IPC": IPC抓拍拍照

    @Generated(hash = 1586289459)
    public OpenRecord(Long id, @NotNull String sn, int status, String image,
            int sid, boolean uploadStatus, long createTime, String type) {
        this.id = id;
        this.sn = sn;
        this.status = status;
        this.image = image;
        this.sid = sid;
        this.uploadStatus = uploadStatus;
        this.createTime = createTime;
        this.type = type;
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

    public int getSid() {
        return this.sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
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

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }






  }
