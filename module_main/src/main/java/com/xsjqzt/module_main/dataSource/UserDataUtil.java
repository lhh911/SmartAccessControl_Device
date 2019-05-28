package com.xsjqzt.module_main.dataSource;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import com.xsjqzt.module_main.Config.DemoConfig;
import com.xsjqzt.module_main.activity.base.ExApplication;
import com.xsjqzt.module_main.modle.User;
import com.xsjqzt.module_main.util.BitmapUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 获取本地数据库中数据
 * create by qyg on 2018/10/15.
 */
public class UserDataUtil {

    private static Map<Integer, User> userMap = new HashMap<>();
    private static ArrayList<User> userList = new ArrayList<>();

    /**
     * 根据personId获取User对象
     *
     * @param id
     * @return
     */
    public static User getUserById(String id) {
        DataSource dataSource = new DataSource(ExApplication.getContext());
        return dataSource.getUserByPersonId(id);
    }

    /**
     * 获取数据库中注册用户数量
     *
     * @return
     */
    public static int getUserCount() {
        DataSource dataSource = new DataSource(ExApplication.getContext());
        ArrayList<User> allUser = dataSource.getAllUser();
        if (null == allUser || allUser.size() == 0) {
            return 0;
        } else {
            return allUser.size();
        }
    }

    /**
     * 清理数据表
     */
    public static void clearDb() {
        DataSource dataSource = new DataSource(ExApplication.getContext());
        List<User> userList = dataSource.getAllUser();
        for (int i = 0; i < userList.size(); i++) {
            String imgPath = DemoConfig.ImagePath + userList.get(i).getPersonId() + ".jpg";
            File imgFile = new File(imgPath);
            if (imgFile.exists()) {
                imgFile.delete();
            }
        }
        userMap.clear();
        dataSource.clearTable();
    }

    /**
     * 更新数据源
     *
     * @return
     */
    public static ArrayList<User> updateDataSource() {
        DataSource dataSource = new DataSource(ExApplication.getContext());
        userMap.clear();
        userList.clear();
        userList = dataSource.getAllUser();
        File file = new File(DemoConfig.ImagePath);
        file.mkdirs();
        for (int i = 0; i < userList.size(); i++) {
            String imgPath = DemoConfig.ImagePath + userList.get(i).getPersonId() + ".jpg";
            File imgFile = new File(imgPath);
            if (imgFile.exists()) {
                userList.get(i).setHead(imgPath);
            }
            userMap.put(Integer.valueOf(userList.get(i).getPersonId()), userList.get(i));
        }
        return userList;
    }

    /**
     * 重载更新数据源方法，返回usermap对象
     *
     * @param needUserMap
     * @return
     */
    public static Map<Integer, User> updateDataSource(boolean needUserMap) {

        long time = System.currentTimeMillis();
        DataSource dataSource = new DataSource(ExApplication.getContext());
        userMap.clear();
        userList.clear();
        userList = dataSource.getAllUser();
        File file = new File(DemoConfig.ImagePath);
        file.mkdirs();
        for (int i = 0; i < userList.size(); i++) {
            String imgPath = DemoConfig.ImagePath + userList.get(i).getPersonId() + ".jpg";
            File imgFile = new File(imgPath);
            if (imgFile.exists()) {
                userList.get(i).setHead(imgPath);
            }
            userMap.put(Integer.valueOf(userList.get(i).getPersonId()), userList.get(i));
        }
        return userMap;
    }

    /**
     * 根据personId获取注册名称
     *
     * @param personId
     * @return
     */
    public static String getNameFromPersonId(int personId) {
        if (personId > 0 && userMap.containsKey(personId)) {
            User user = userMap.get(personId);
            return user.getName();
        }
        return "";
    }

    /**
     * 根据personId获取注册名称
     *
     * @param personId
     * @return
     */
    public static boolean deleteByPersonId(String personId) {
        DataSource dataSource = new DataSource(ExApplication.getContext());
        dataSource.deleteById(personId);
        return true;
    }

    public static boolean addDataSource(Bitmap bitmap, User user, float[] rect) {
        try {
            Bitmap head = null;
            head = Bitmap.createBitmap(bitmap, (int) rect[0], (int) rect[1], (int) rect[2], (int) rect[3], null, true);
            DataSource dataSource = new DataSource(ExApplication.getContext());
            dataSource.insert(user);
            if (head == null) head = bitmap;
            BitmapUtil.saveBitmap(head, DemoConfig.ImagePath + user.getPersonId() + ".jpg");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 添加数据库
     *
     * @param bitmap
     * @param user
     * @param rect        人脸坐标
     * @param orientation
     */
    public static boolean addDataSource(Bitmap bitmap, User user, float[] rect, int orientation) {
        Bitmap head = null;
        try {
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                Matrix matrix = new Matrix();
                matrix.postRotate(270);
                head = Bitmap.createBitmap(bitmap, bitmap.getWidth() - (int) rect[1] - (int) rect[3], (int) rect[0], (int) rect[3], (int) rect[2], matrix, true);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                head = Bitmap.createBitmap(bitmap, (int) rect[0], (int) rect[1], (int) rect[2], (int) rect[3], null, true);
            }
        } catch (Exception e) {
            head = null;
            Log.e("RsTog", "error:" + e);
        }
        if (head == null) {
            head = bitmap;
        }
        DataSource dataSource = new DataSource(ExApplication.getContext());
        dataSource.insert(user);
        BitmapUtil.saveBitmap(head, DemoConfig.ImagePath + user.getPersonId() + ".jpg");
        return true;
    }
}
