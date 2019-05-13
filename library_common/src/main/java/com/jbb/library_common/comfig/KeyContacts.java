/**
 * 工程: Coyote
 * 标题: KeyContacts.java
 * 包:   com.niuniucaip.lotto.ui.config
 * 描述: TODO
 * 作者: nn
 * 时间: 2014-7-22 下午4:56:26
 * 版权: Copyright 2014 Shenzhen NiuNiucaip Tech Co.,Ltd.
 * All rights reserved.
 */

package com.jbb.library_common.comfig;

import android.content.Intent;

/**
 * 类: KeyContacts
 * 描述: 用于存放常用字符串
 * 作者: nn
 * 时间: 2014-7-22 下午4:56:26
 */
public class KeyContacts {



    /**公共参数**/
    public static final String KEY_CUSTOM_PARAMS = "Custom-Params";


    public static final String KEY_TITLE = "title";
    public static final String KEY_URL = "url";

    public static final String COMM_NUM0 = "0";
    public static final String API_KEY = "API_KEY";


    public static final String SP_KEY_USERINFO = "key_userinfo";//用户信息sp 中key
    public static final String SP_NAME_USERINFO= "userinfo"; //SP userinfo文件名


    public static final String ACTION_LOGIN_SUCCESS = "action_login_success";//登录成功后通知
    public static final String ACTION_API_KEY_INVALID = "action_api_key_invalid";//apikey失效

    public static final String system  = "android";//手机系统

    public static final String Bearer  = "Bearer ";//手机系统


    public static final int STATUS_FORCE_KILLED = -1;//应用在后台被强杀了
    public static final int STATUS_NORMAL = 2; //APP正常态
    public static final String START_LAUNCH_ACTION = "start_launch_action";
}