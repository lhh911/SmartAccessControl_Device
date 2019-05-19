package com.xsjqzt.module_main.greendao;

import com.jbb.library_common.BaseApplication;

public class DbManager {

    // 是否加密
    public static final boolean ENCRYPTED = true;

    public static final String DB_NAME = "smartaccesscontrol.db";//数据库名
    private static DbManager mDbManager;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
//    private DaoMaster.DevOpenHelper mDevOpenHelper;
    private DBHelper dbHelper;


    public static DbManager getInstance(){
        if (null == mDbManager) {
            synchronized (DbManager.class) {
                if (null == mDbManager) {
                    mDbManager = new DbManager();
                }
            }
        }
        return mDbManager;

    }

    private DbManager(){
        init();
    }

    private void init(){
//        mDevOpenHelper = new DaoMaster.DevOpenHelper(BaseApplication.getContext(),DB_NAME);

        dbHelper = new DBHelper(BaseApplication.getContext());
        mDaoMaster = new DaoMaster(dbHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();

    }

    public DaoSession getDaoSession(){
        if(mDaoSession == null){
            init();
        }
        return mDaoSession;
    }


    /**
     * 关闭数据库
     */
    public void closeDataBase() {
        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }
        if (mDaoSession != null) {
            mDaoSession.clear();
            mDaoSession = null;
        }
    }
}
