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
    private int user_id;//服务器表中id
    @NotNull
    private int personId;//阅面数据库id

    @NotNull
    private String code;//阅面注册唯一识别码

    private String image;//后台业主上传的人脸图片

    private int status;// 人脸图片状态，1 待检验，2 有效， 3无效

    private int update_time;//

    @Generated(hash = 274524461)
    public FaceImage(Long id, int user_id, int personId, @NotNull String code,
            String image, int status, int update_time) {
        this.id = id;
        this.user_id = user_id;
        this.personId = personId;
        this.code = code;
        this.image = image;
        this.status = status;
        this.update_time = update_time;
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

    public int getUser_id() {
        return this.user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUpdate_time() {
        return this.update_time;
    }

    public void setUpdate_time(int update_time) {
        this.update_time = update_time;
    }

    public int getPersonId() {
        return this.personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }





  }
