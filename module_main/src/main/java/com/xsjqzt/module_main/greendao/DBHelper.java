package com.xsjqzt.module_main.greendao;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

public class DBHelper extends DaoMaster.DevOpenHelper {
    public DBHelper(Context context) {
        super(context, DbManager.DB_NAME, null);
    }
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //  需要进行数据迁移更新的实体类 ，新增的不用加
//        DBMigrationHelper.getInstance().migrate(db, ConsumeInfoDao.class, QuotaInfoDao.class);
    }
}

