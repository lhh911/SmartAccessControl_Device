package com.jbb.library_common.retrofit.other;

/***
 * 所有实体返回类继承它
 */
public class BaseBean {


    private int resultCode;
    private String resultCodeMessage;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultCodeMessage() {
        return resultCodeMessage;
    }

    public void setResultCodeMessage(String resultCodeMessage) {
        this.resultCodeMessage = resultCodeMessage;
    }
}
