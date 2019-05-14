package com.jbb.library_common.comfig;

public class InterfaceConfig {

//    public static final String BASEURL = "http://apitest.jiebangbang.cn:28080/manager/" ;

    public static final String [] URLS = {"https://api.xsjqzt.com" ,"https://api.xsjqzt.com"};//

    public static final String BASEURL = URLS[AppConfig.HOST_ADDRESS_CONFIG_INDEX];




    public static final String validateAndCacheCardInfo = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json" ;//根据卡号获取银行卡信息，（阿里开放接口）
}
