package com.jbb.library_common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.jbb.library_common.BaseApplication;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 类: SharePreferensUtil <p>
 * 描述: 本地xml数据保存方法 <p>
 * 作者: nn <p>
 * 时间: 2014年7月28日 上午11:45:03 <p>
 */
public class SharePreferensUtil {

    /**
     * 方法: saveData <p>
     * 描述: 保存批量或者单个数据  <p>
     * 参数: @param mContext
     * 参数: @param map
     * 参数: @param preferencesName <p>
     * 返回: void <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年7月30日 上午10:52:15
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void saveData(Map<String, String> map, String preferencesName) {
        SharedPreferences spf = BaseApplication.getContext().getSharedPreferences(preferencesName,
                Context.MODE_PRIVATE);
        Editor editor = spf.edit();

        Set set = map.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = (Entry<String, String>) it.next();
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.commit();
    }


    /**
     * 方法: putString <p>
     * 描述: 保存单个值  <p>
     * 参数: @param mContext
     * 参数: @param key
     * 参数: @param value
     * 参数: @param preferencesName
     * 参数: @return <p>
     * 返回: boolean <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年7月30日 上午10:52:05
     */
    public static boolean putString(String key, String value, String preferencesName) {
        if (TextUtils.isEmpty(value)) {
            value = "";
        }
        SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return sp.edit().putString(key, value).commit();
    }

    /**
     * 方法: getString <p>
     * 描述: 根据key得到某个值 <p>
     * 参数: @param mContext
     * 参数: @param key
     * 参数: @param preferencesName
     * 参数: @return <p>
     * 返回: String <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年7月30日 上午10:51:56
     */
    public static String getString(String key, String preferencesName) {
        SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    /**
     * 方法: putBoolean <p>
     * 描述: 保存boolean值 <p>
     * 参数: @param mContext
     * 参数: @param key
     * 参数: @param value
     * 参数: @param preferencesName
     * 参数: @return <p>
     * 返回: boolean <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年7月30日 上午11:05:30
     */
    public static boolean putBoolean(String key, boolean value, String preferencesName) {
        SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 方法: getBoolean <p>
     * 描述: 得到boolean值 <p>
     * 参数: @param mContext
     * 参数: @param key
     * 参数: @param value
     * 参数: @param preferencesName
     * 参数: @return <p>
     * 返回: boolean <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年7月30日 上午11:06:50
     */
    public static boolean getBoolean(String key, boolean defaultValue, String preferencesName) {
        SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }


    public static boolean putInt(String key, int value, String preferencesName) {
        SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return sp.edit().putInt(key, value).commit();
    }

    public static int getInt(String key, int defaultValue, String preferencesName) {
        SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }

    public static boolean putLong(String key, long value, String preferencesName) {
        SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return sp.edit().putLong(key, value).commit();
    }

    public static long getLong(String key, long defaultValue, String preferencesName) {
        SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return sp.getLong(key, defaultValue);
    }

    /**
     * 方法: deleteString <p>
     * 描述: 根据key删除保存的值 <p>
     * 参数: @param mContext
     * 参数: @param key
     * 参数: @param preferencesName <p>
     * 返回: void <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年7月30日 上午10:51:46
     */
    public static void deleteString(String key, String preferencesName) {
        SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }


    /**
     * 方法: delSharedPreferences <p>
     * 描述: 清空SharePreferens文件 <p>
     * 参数: @param mContext
     * 参数: @param preferencesName <p>
     * 返回: void <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年7月30日 上午10:51:33
     */
    public static void delSharedPreferences(String preferencesName) {
        SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

    /**
     * 方法: contains <p>
     * 描述: 判断是否包含这个key，如果包含，返回true，否则返回false<p>
     * 参数: @param mContext
     * 参数: @param key
     * 参数: @param preferencesName
     * 参数: @return<p>
     * 返回: boolean<p>
     * 异常 <p>
     * 作者: nn<p>
     * 时间: 2015年8月4日 上午11:14:26<p>
     */
    public static boolean contains(String key, String preferencesName) {
        SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return sp.contains(key);
    }


    public static boolean putBoolean(Context context,String key, boolean value, String preferencesName) {
        SharedPreferences sp = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return sp.edit().putBoolean(key, value).commit();
    }

}
