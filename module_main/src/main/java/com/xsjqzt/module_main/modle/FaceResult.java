package com.xsjqzt.module_main.modle;

/**
 * 0 : 成功
 * -3	: 包名不匹配
 * -6	: app_id不匹配
 * -5	: 未读取到激活信息
 * -4	: 激活失败或者网络不正常
 * -1	: 已过期，当前日期比截止日期要大
 * -2	: 已过期，当前日期比打包日期要小
 * -11:异常
 * -12:句柄初始化失败
 * 1001 比对文件格式不正确
 * 1002 校验失败
 * 2101 激活已满
 * 2102 密钥不匹配
 * 1128 网络超时
 * 1106 DNS解析出错
 * 1107 服务器异常
 * 1100
 */

public class FaceResult extends ResultCode {
    public int personId;
    public float[] rect;
}


class ResultCode {
    public String msg;
    public int code;
}