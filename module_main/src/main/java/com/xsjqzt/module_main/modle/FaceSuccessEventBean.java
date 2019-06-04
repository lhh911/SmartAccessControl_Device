package com.xsjqzt.module_main.modle;

public class FaceSuccessEventBean {
    public int user_id;
    public String faceResult;

    public FaceSuccessEventBean(int user_id,String faceResult) {
        this.user_id = user_id;
        this.faceResult = faceResult;
    }
}
