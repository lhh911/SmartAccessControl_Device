package com.xsjqzt.module_main.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ICCard {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private int sid;//服务器表中id

    @NotNull
    private String sn;//ic卡号码

    @NotNull
    private String name;//ic名

    @Generated(hash = 79061019)
    public ICCard(Long id, int sid, @NotNull String sn, @NotNull String name) {
        this.id = id;
        this.sid = sid;
        this.sn = sn;
        this.name = name;
    }

    @Generated(hash = 1554848264)
    public ICCard() {
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

  }
