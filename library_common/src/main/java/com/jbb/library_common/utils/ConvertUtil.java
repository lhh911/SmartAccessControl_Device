package com.jbb.library_common.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;


/**
 * 
 * 类: ConvertUtil <p>
 * 描述: 转换类  捕捉异常 <p>
 * 作者: nn <p>
 * 时间: 2014年9月23日 下午12:13:21 <p>
 */
public class ConvertUtil {

	/**
	 * 
	 * 方法: convertToFloat <p>
	 * 描述: 将String 转换成float  异常返回0.00 <p>
	 * 参数: @param src
	 * 参数: @return <p>
	 * 返回: float <p>
	 * 异常  <p>
	 * 作者: nn <p>
	 * 时间: 2014年9月23日 下午12:17:39
	 */
	public static float convertToFloat(String src) {
		float dest = 0f;
		try {
			dest = Float.parseFloat(src);
		} catch (Exception e) {
			dest = 0;
		}
		return dest;
	}

	/**
	 * 
	 * 方法: convertToInt <p>
	 * 描述: 将String 转换成int 异常返回0 <p>
	 * 参数: @param src
	 * 参数: @return <p>
	 * 返回: int <p>
	 * 异常  <p>
	 * 作者: nn <p>
	 * 时间: 2014年9月23日 下午12:19:06
	 */
	public static int convertToInt(String src) {
		int dest = 0;
		try {
			dest = Integer.parseInt(src);
		} catch (Exception e) {
			dest = 0;
		}
		return dest;
	}
	
	/** 
	 * 方法: convertToInt <p>
	 * 描述: TODO<p>
	 * 参数: @param playIdStr
	 * 参数: @param invaldatePlayid
	 * 参数: @return<p>
	 * 返回: int<p>
	 * 异常 <p>
	 * 作者: nn<p>
	 * 时间: 2014年11月18日 下午3:49:10<p>
	 */
	public static int convertToInt(String src, int defaultValue) {
		int dest = 0;
		try {
			dest = Integer.parseInt(src);
		} catch (Exception e) {
			dest = defaultValue;
		}
		return dest;
	}

    /**
     *
     * 方法: convertToDouble <p>
     * 描述: 将字符串转成double <p>
     * 参数: @param src
     * 参数: @return <p>
     * 返回: double <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年12月26日 上午11:18:41
     */
    public static double convertToDouble(String src) {
        double dest = 0;
        try {
            dest = Double.parseDouble(src);
        } catch (Exception e) {
            dest = 0;
        }
        return dest;
    }

	/**
	 * 
	 * 方法: convertToLong <p>
	 * 描述: 将字符串转成long <p>
	 * 参数: @param src
	 * 参数: @return <p>
	 * 返回: long <p>
	 * 异常  <p>
	 * 作者: nn <p>
	 * 时间: 2014年11月15日 上午11:18:41
	 */
	public static long convertToLong(String src) {
		long dest = 0;
		try {
			dest = Long.parseLong(src);
		} catch (Exception e) {
			dest = 0;
		}
		return dest;
	}
	
    /**
     *
     * 方法: toDoubleDigits <p>
     * 描述: 将String 转换成双精度的字符串eg 1.00,兑换时候使用  异常返回0.00 <p>
     * 参数: @param src
     * 参数: @return <p>
     * 返回: String <p>
     * 异常  <p>
     * 作者: nn <p>
     * 时间: 2014年10月23日 下午16:17:39
     */
    public static String toDoubleDigits(String src) {
        DecimalFormat df = new DecimalFormat("0.00");
        Double dest = 0.00;
        try {
            dest = Double.parseDouble(src);
        } catch (Exception e) {
            dest = 0.00;
        }
        return df.format(dest);
    }
	/**
	 * 
	 * 方法: convertToArray <p>
	 * 描述: 讲一个字符串根据空格转换成array <p>
	 * 参数: @param src
	 * 参数: @return <p>
	 * 返回: String[] <p>
	 * 异常  <p>
	 * 作者: nn <p>
	 * 时间: 2014年9月29日 下午4:11:27
	 */
	public static String[] convertToArray(String src) {
		if(TextUtils.isEmpty(src)){
			return null;
		}
		
		if(src.contains(" ")){
			return src.split(" ");
		}
		return null;
		
	}

	/**
	 * 
	 * 方法: specialRoundFloat <p>
	 * 描述: 四舍六入五单双算法 <p>
	 * 参数: @param srcFloat
	 * 参数: @return <p>
	 * 返回: float <p>
	 * 异常  <p>
	 * 作者: nn <p>
	 * 时间: 2015年1月19日 上午11:47:35
	 */
	public static float specialRoundFloat(float srcFloat) {
		float descFloat =  srcFloat;
		try {
		String str = new BigDecimal(descFloat).toString();
		if(str.contains(".")){
			int index = str.length()-str.indexOf(".")-1;
			if(index>=3){
				String title = str.substring(0,str.indexOf("."));
				String subStr = str.substring(str.indexOf(".")+1,str.indexOf(".")+4);
				String first  = subStr.substring(0,1);
				String second  = subStr.substring(1,2);
				String third  = subStr.substring(2,3);
				int thirdInt = convertToInt(third);
				int secondInt = convertToInt(second);
				if(thirdInt<5){
					descFloat =  convertToFloat(title+"."+first+second);
				}else if(thirdInt==5){
					if(secondInt%2==0){
						descFloat = convertToFloat(title+"."+first+second);
					}else{
						descFloat =  judgeToConvert(title, convertToInt(first),secondInt);
					}
					
				}else if(thirdInt>5){
					descFloat =   judgeToConvert(title, convertToInt(first),secondInt);
				}
			}
		}
		
		} catch (Exception e) {
			// TODO: handle exception
			return srcFloat;
		}
		return descFloat;
		
	}

	/**
	 *描述:之前的方法有问题  忽略了为9的情况 现在兼容
	 *作者:nn
	 *时间:2016/3/4 10:49
	 *版本:3.1.8
	 */
	public static float judgeToConvert(String title,int first,int second) {
		int front  =  convertToInt(title);
		if(second==9){
			second = 0;
			if(first==9){
				front += 1;
				first = 0;
			}else{
				first +=1;
			}
		}else{
			second +=1;
		}
		return convertToFloat(front+"."+first+second);

	}
	/**
	 * 
	 * 方法: specialRoundFloat <p>
	 * 描述: 四舍六入算法 <p>
	 * 参数: @param src
	 * 参数: @return <p>
	 * 返回: float <p>
	 * 异常  <p>
	 * 作者: nn <p>
	 * 时间: 2015年1月19日 上午11:55:53
	 */
	public static float specialRoundFloat(String src) {
		
		float srcFloat = convertToFloat(src);
		return specialRoundFloat(srcFloat);
	}
}


