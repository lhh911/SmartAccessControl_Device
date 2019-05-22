package com.xsjqzt.module_main.greendao.entity;

import com.alibaba.fastjson.annotation.JSONField;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class IDCard {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private int sid;//服务器表中id

    @NotNull
    @Unique
    private String sn;//身份证号码


    private String user_name;//


    private int user_id;//


    @Generated(hash = 1837019743)
    public IDCard(Long id, int sid, @NotNull String sn, String user_name,
            int user_id) {
        this.id = id;
        this.sid = sid;
        this.sn = sn;
        this.user_name = user_name;
        this.user_id = user_id;
    }


    @Generated(hash = 1276747893)
    public IDCard() {
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


    public String getSn() {
        return this.sn;
    }


    public void setSn(String sn) {
        this.sn = sn;
    }


    public String getUser_name() {
        return this.user_name;
    }


    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


    public int getUser_id() {
        return this.user_id;
    }


    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }



  }
