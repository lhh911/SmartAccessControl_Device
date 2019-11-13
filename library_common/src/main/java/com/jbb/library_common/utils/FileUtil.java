package com.jbb.library_common.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;


import com.jbb.library_common.BaseApplication;
import com.jbb.library_common.comfig.AppConfig;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * 类: FileUtil <p>
 * 描述: 文件操作相关类 <p>
 */
@SuppressWarnings("deprecation")
public class FileUtil {


    /**
     * 方法: isExternalStorageCanUse <p>
     */
    public static boolean isExternalStorageCanUse() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long availCount = sf.getAvailableBlocks();
            long blocSize = sf.getBlockSize();
            //外部存储空间10M的话可行
            if (availCount * blocSize > 1024 * 1024 * 10) {
                return true;
            }
        }
        return false;
    }

    /**
     * 方法: isRootStorageCanUse <p>
     * 描述: 判断手机存储是否可用 <p>
     */

    public static boolean isRootStorageCanUse() {

        File root = Environment.getRootDirectory();
        StatFs sf = new StatFs(root.getPath());
        long availCount = sf.getAvailableBlocks();
        long blocSize = sf.getBlockSize();
        //内部部存储空间10M的话也可行
        if (availCount * blocSize > 1024 * 1024 * 10) {
            return true;
        }
        return false;
    }




    /**
     * 方法: getFileName <p>
     * 描述: 从URL中获取filename <p>
     */
    public static String getFileName(String url) {
        String filename = "";
        // 从路径中获取
        if (TextUtils.isEmpty(filename)) {
            filename = url.substring(url.lastIndexOf("/") + 1);
        }
        return filename;
    }





    /**
     * 删除某个文件夹下的文件
     *
     * @param dir
     */
    public static void deleteFilesByDirectory(File dir) {
        if (dir != null) {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    deleteFilesByDirectory(new File(dir, children[i]));
                }
            } else {
                dir.delete();
            }
        }
    }


    /**
     * 得到cache size大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static String getAppCacheSize(File file) {
        return getFormatSize(getFolderSize(file));
    }

    /**
     * 获取文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            return 0;
        }
        return size;
    }

    /**
     * 格式化size数据
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        if (size == 0) {
            return "0KB";
        }
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }






    /**
     *  app缓存文件目录
     *
     * @param context
     * @return
     */
    public static String getAppCachePath(Context context) {
        if (FileUtil.isExternalStorageCanUse()) {
            File tempPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +AppConfig.SDCARD_DIR_PATH);
            if (!tempPath.exists()) {
                tempPath.mkdirs();
            }

            return tempPath.getPath();
        } else if (FileUtil.isRootStorageCanUse()) {
            return context.getFilesDir().getAbsolutePath();
        } else {
            //磁盘空间不足
            return "";
        }
    }

    /**
     * app 图片路径文件目录
     *
     * @param context
     * @return
     */
    public static String getAppPicturePath(Context context) {
        if (FileUtil.isExternalStorageCanUse()) {
            File tempPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + AppConfig.SDCARD_DIR_PATH +File.separator + AppConfig.SDCARD_DIR_PICTURE);
            if (!tempPath.exists()) {
                tempPath.mkdirs();
            }

            return tempPath.getPath();
        } else if (FileUtil.isRootStorageCanUse()) {
            return context.getFilesDir().getAbsolutePath();
        } else {
            //磁盘空间不足
            return "";
        }
    }




    /**
     * app 开门记录的存储路径,按当前日期建文件夹
     *
     */
    public static String getAppRecordPicturePath(Context context) {
        if (FileUtil.isExternalStorageCanUse()) {
            File tempPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + AppConfig.SDCARD_DIR_PATH + File.separator + AppConfig.SDCARD_DIR_PICTURE + File.separator + AppConfig.SDCARD_DIR_RECORDPICTURE );
            if (!tempPath.exists()) {
                tempPath.mkdirs();
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String fileName = format.format(Calendar.getInstance().getTimeInMillis());

            tempPath = new File(tempPath ,fileName);
            if(!tempPath.exists())
                tempPath.mkdir();

            return tempPath.getPath();
        } else if (FileUtil.isRootStorageCanUse()) {
            return context.getCacheDir().getAbsolutePath();
        } else {
            //磁盘空间不足
            return "";
        }
    }


    /**
     * app 开门记录的存储路径,按当前日期建文件夹
     *
     */
    public static String getAppFacePicturePath(Context context) {
        if (FileUtil.isExternalStorageCanUse()) {
            File tempPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + AppConfig.SDCARD_DIR_PATH + File.separator + AppConfig.SDCARD_DIR_PICTURE + File.separator + AppConfig.SDCARD_DIR_FACEPICTURE);
            if (!tempPath.exists()) {
                tempPath.mkdirs();
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String fileName = format.format(Calendar.getInstance().getTimeInMillis());

            tempPath = new File(tempPath ,fileName);
            if(!tempPath.exists())
                tempPath.mkdir();

            return tempPath.getPath();
        } else if (FileUtil.isRootStorageCanUse()) {
            return context.getCacheDir().getAbsolutePath();
        } else {
            //磁盘空间不足
            return "";
        }
    }


    /**
     * app 开门记录的存储路径,按当前日期建文件夹
     *
     */
    public static String getAppLogPath(Context context) {
        if (FileUtil.isExternalStorageCanUse()) {
            File tempPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + AppConfig.SDCARD_DIR_PATH + File.separator + AppConfig.SDCARD_DIR_LOG );
            if (!tempPath.exists()) {
                tempPath.mkdirs();
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String fileName = format.format(Calendar.getInstance().getTimeInMillis());

            tempPath = new File(tempPath ,fileName);
            if(!tempPath.exists())
                tempPath.mkdir();

            return tempPath.getPath();
        } else if (FileUtil.isRootStorageCanUse()) {
            return context.getCacheDir().getAbsolutePath();
        } else {
            //磁盘空间不足
            return "";
        }
    }


    //创建开门记录时抓拍的图片文件路径
    private static File createRecordFile(Context context){
        String path = FileUtil.getAppRecordPicturePath(context);
        File file = new File(path,(new Date().getTime() + ".jpg"));
        try {
            if (!file.exists())
                file.createNewFile();
        }catch(Exception e){
            e.printStackTrace();
        }
        return file;
    }



    /**
     * app 视频路径文件目录
     *
     * @param context
     * @return
     */
    public static String getAppVideoPath(Context context) {
        if (FileUtil.isExternalStorageCanUse()) {
            File tempPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +AppConfig.SDCARD_DIR_PATH +File.separator + AppConfig.SDCARD_DIR_VIDEO);
            if (!tempPath.exists()) {
                tempPath.mkdirs();
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String fileName = format.format(Calendar.getInstance().getTimeInMillis());

            tempPath = new File(tempPath ,fileName);
            if(!tempPath.exists())
                tempPath.mkdir();

            return tempPath.getPath();
        } else if (FileUtil.isRootStorageCanUse()) {
            return context.getCacheDir().getAbsolutePath();
        } else {
            //磁盘空间不足
            return "";
        }
    }



}
