package com.xsjqzt.module_main.dataSource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.xsjqzt.module_main.modle.User;

import java.util.ArrayList;

/**
 * 本地数据库使用帮助类
 * Created by mac on 16/7/11.
 */
public class DataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.USER_ID,
            MySQLiteHelper.PERSON_ID,
            MySQLiteHelper.SERVER_PERSON_ID,
            MySQLiteHelper.USER_NAME,
            MySQLiteHelper.USER_AGE,
            MySQLiteHelper.USER_GENDER,
            MySQLiteHelper.USER_SCORE,
            MySQLiteHelper.FACE_FEATURE,
    };

    public DataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(User user) {
        open();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.PERSON_ID, user.getPersonId());
        values.put(MySQLiteHelper.SERVER_PERSON_ID, user.getServerPersonId());
        values.put(MySQLiteHelper.USER_NAME, user.getName());
        values.put(MySQLiteHelper.USER_AGE, user.getAge());
        values.put(MySQLiteHelper.USER_GENDER, user.getGender());
        values.put(MySQLiteHelper.USER_SCORE, user.getScore());
        values.put(MySQLiteHelper.FACE_FEATURE, user.getFaceFeature() == null ? "" : user.getFaceFeature());
        long i = database.insert(MySQLiteHelper.TABLE_USER, null, values);
        close();
        return i;
    }

    public ArrayList<User> getAllUser() {
        open();
        ArrayList<User> result = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_USER,
                allColumns, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User user = new User();
            user.setUserId(cursor.getString(0));
            user.setPersonId(cursor.getString(1));
            user.setServerPersonId(cursor.getString(2));
            user.setName(cursor.getString(3));
            user.setAge(cursor.getString(4));
            user.setGender(cursor.getString(5));
            user.setScore(cursor.getString(6));
             user.setFaceFeature(cursor.getString(7));
            result.add(user);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return result;
    }

    public User getUserByPersonId(String person_id) {
        open();
        User user = new User();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_USER,
                null,
                "person_id = ?",
                new String[]{person_id},
                null, null, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            user.setUserId(cursor.getString(0));
            user.setPersonId(cursor.getString(1));
            user.setServerPersonId(cursor.getString(2));
            user.setName(cursor.getString(3));
            user.setAge(cursor.getString(4));
            user.setGender(cursor.getString(5));
            user.setScore(cursor.getString(6));
        } else {
            user = null;
        }
        close();
        return user;
    }


    public User getUserByServerPersonId(String serverPersonId) {

        open();
        User user = new User();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_USER,
                null,
                "person_id = ?",
                new String[]{serverPersonId},
                null, null, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            user.setUserId(cursor.getString(0));
            user.setPersonId(cursor.getString(1));
            user.setServerPersonId(cursor.getString(2));
            user.setName(cursor.getString(3));
            user.setAge(cursor.getString(4));
            user.setGender(cursor.getString(5));
            user.setScore(cursor.getString(6));
        } else {
            user = null;
        }
        close();
        return user;

    }

    public int deleteById(String personId) {
        int i = 0;
        open();
        i = database.delete(MySQLiteHelper.TABLE_USER, MySQLiteHelper.PERSON_ID + "=?", new String[]{personId});
        close();
        return i;
    }

    public void clearTable() {
        //执行SQL语句
        open();
        database.delete(MySQLiteHelper.TABLE_USER, MySQLiteHelper.PERSON_ID + ">?", new String[]{"-1"});
        close();
//        database.execSQL("delete from stu_table where _id  >= 0");
    }

    public boolean deleteAllUser() {

        boolean flag = false;
        try {
            open();
            database.execSQL("delete from " + MySQLiteHelper.TABLE_USER);
            database.execSQL("update sqlite_sequence set seq = 0 where name =  '" + MySQLiteHelper.TABLE_USER + "'");
            flag = true;

        } catch (SQLException e) {
            flag = false;
        }

        close();
        return flag;
    }
}
