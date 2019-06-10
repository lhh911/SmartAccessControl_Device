package com.jbb.library_common.comfig;


//import com.jf.lib_common.utils.LogUtil;

public class AppConfig {
    public static final String SDCARD_DIR_PATH = "SmartAccessControl_Device";
    public static final String SDCARD_DIR_PICTURE = "picture";
    public static final String SDCARD_DIR_LOG = "log";
    public static final String SDCARD_DIR_RECORDPICTURE = "recordPicture";
    public static final String SDCARD_DIR_FACEPICTURE = "facePicture";
    public static final String SDCARD_DIR_VIDEO = "video";

    public static final int LOG_OFF = -100;
    public static final int LOG_ON = 6;

    /***
     * 日志开关
     ***/
    public static final int LOG_SWITCH_FLAG = LOG_ON;
     //线上地址
    public static final int HOST_INDEX_ONLINE = 0;
     //测试地址
    public static final int HOST_INDEX_PREPARE = 1;
    //修改此字段值切换 接口测试，生产环境
    public static final int HOST_ADDRESS_CONFIG_INDEX =  HOST_INDEX_ONLINE;


    public static String BUGLY_APPID = "0e2bc926f1";
}
