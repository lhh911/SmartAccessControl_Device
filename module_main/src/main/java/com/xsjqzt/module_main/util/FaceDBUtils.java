package com.xsjqzt.module_main.util;

import android.database.Cursor;

import com.xsjqzt.module_main.greendao.DbManager;
import com.xsjqzt.module_main.greendao.FaceImageDao;

import java.util.ArrayList;
import java.util.List;

public class FaceDBUtils {


    public static List<String> loadRegistFaceUserId(){
        String sql = "SELECT "+ FaceImageDao.Properties.User_id.columnName  + " FROM " + FaceImageDao.TABLENAME ;
        Cursor cursor = DbManager.getInstance().getDaoSession().getDatabase().rawQuery(sql, null);

        List<String> list = new ArrayList<>();
//        cursor.moveToFirst();
        while (cursor.moveToNext()){
            list.add(String.valueOf(cursor.getInt(0)));
        }
        cursor.close();
        return list;
    }
}
