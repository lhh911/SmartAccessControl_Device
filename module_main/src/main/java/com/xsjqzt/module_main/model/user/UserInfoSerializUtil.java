/**
 * 工程: Coyote<p>
 * 标题: UserInfoSerializUtil.java<p>
 * 包:   com.niuniucaip.lotto.mainact.personnal.loginmodule.model<p>
 * 描述: TODO<p>
 * 作者: nn<p>
 * 时间: 2014年12月3日 下午6:42:32<p>
 * 版权: Copyright 2014 Shenzhen NiuNiucaip Tech Co.,Ltd.<p>
 * All rights reserved.<p>
 */

package com.xsjqzt.module_main.model.user;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.SharePreferensUtil;


public class UserInfoSerializUtil {


    /**
     * 方法: initUserInstance <p>
     * 初始化到单列中
     */
    public static void initUserInstance() {
        String userJsonData = SharePreferensUtil.getString(KeyContacts.SP_KEY_USERINFO, KeyContacts.SP_NAME_USERINFO);
        if (!TextUtils.isEmpty(userJsonData)) {
            try {
                SerializableUserInfo sUserInfo = JSON.parseObject(userJsonData, SerializableUserInfo.class);
                UserInfoInstance.getInstance().iniInstanse(sUserInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 方法: serializUserInstance <p>
     *  保存到文件
     */
    public static void serializUserInstance() {
        if (UserInfoInstance.getInstance().hasLogin()) {
            SerializableUserInfo info = UserInfoInstance.getInstance().getSerialzInfo();
            String userJson = JSON.toJSONString(info);
            SharePreferensUtil.putString(KeyContacts.SP_KEY_USERINFO, userJson, KeyContacts.SP_NAME_USERINFO);

        } else {
            SharePreferensUtil.deleteString(KeyContacts.SP_KEY_USERINFO, KeyContacts.SP_NAME_USERINFO);
        }
    }




}
