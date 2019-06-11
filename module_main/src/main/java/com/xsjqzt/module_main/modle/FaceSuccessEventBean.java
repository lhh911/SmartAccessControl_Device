package com.xsjqzt.module_main.modle;

public class FaceSuccessEventBean {
    public int user_id;
    public String faceResult;
    public boolean isRegist;

    public FaceSuccessEventBean(int user_id,String faceResult,boolean isRegist) {
        this.user_id = user_id;
        this.faceResult = faceResult;
        this.isRegist = isRegist;
    }
}
