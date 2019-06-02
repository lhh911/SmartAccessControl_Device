package com.xsjqzt.module_main.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
//开门临时密码
@Entity
public class OpenCode {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private int sid;//服务器表中id

    @NotNull
    @Unique
    private String code;//临时密码

    private int update_time;

    private int expiry_time;//

    private int user_id;//

    @Generated(hash = 14662844)
    public OpenCode(Long id, int sid, @NotNull String code, int update_time,
            int expiry_time, int user_id) {
        this.id = id;
        this.sid = sid;
        this.code = code;
        this.update_time = update_time;
        this.expiry_time = expiry_time;
        this.user_id = user_id;
    }

    @Generated(hash = 1062227905)
    public OpenCode() {
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

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getUpdate_time() {
        return this.update_time;
    }

    public void setUpdate_time(int update_time) {
        this.update_time = update_time;
    }

    public int getExpiry_time() {
        return this.expiry_time;
    }

    public void setExpiry_time(int expiry_time) {
        this.expiry_time = expiry_time;
    }

    public int getUser_id() {
        return this.user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }


  }
