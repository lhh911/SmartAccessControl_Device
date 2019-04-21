package com.jbb.library_common.comfig;


//import com.jf.lib_common.utils.LogUtil;

public class AppConfig {
    public static final String SDCARD_DIR_PATH = "xiaojintiao";
    public static final String SDCARD_DIR_PICTURE = "picture";

    public static final int LOG_OFF = -100;
    public static final int LOG_ON = 6;

    /***
     * 日志开关
     ***/
    public static final int LOG_SWITCH_FLAG = LOG_OFF;
     //线上地址
    public static final int HOST_INDEX_ONLINE = 0;
     //测试地址
    public static final int HOST_INDEX_PREPARE = 1;
    //修改此字段值切换 接口测试，生产环境
    public static final int HOST_ADDRESS_CONFIG_INDEX =  HOST_INDEX_ONLINE;

    //闪验appId
    public static final String appId = "suLEabUL";
    //闪验appKey
    public static final String appKey = "ITTU4wy7";

    //微信
    public static final String WXAPP_ID = "wx80e48500f510ae43";
    public static final String TENCENT_APPID = "1106520585";

    //新颜
    public static final String XY_APIUSER = "8150725329";
    public static final String XY_APIKEY = "6613d600d19941a094753830bd6fc0af";

    //有盾人脸识别
    public static final String PUB_KEY = "b1fc9e48-495b-4479-bbcb-7911a4c21676";
    public static final String SECRETKEY = "9186f5fc-de70-48a3-843c-d12a94cf6c48";
//    public static final String YD_NOTICE_URL = "http://apitest.jiebangbang.cn/report/udReport";//测式环境
    public static final String YD_NOTICE_URL = "http://xjtapi.fenqiduo.cn/xjtapi/report/udReport/notify";//正式环境

}
