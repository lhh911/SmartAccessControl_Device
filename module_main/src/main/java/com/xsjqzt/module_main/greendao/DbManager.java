package com.xsjqzt.module_main.greendao;

import com.jbb.library_common.BaseApplication;

public class DbManager {

    // 是否加密
    public static final boolean ENCRYPTED = true;

    private static final String DB_NAME = "smartaccesscontrol.db";
    private static DbManager mDbManager;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private DaoMaster.DevOpenHelper mDevOpenHelper;


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
        mDevOpenHelper = new DaoMaster.DevOpenHelper(BaseApplication.getContext(),DB_NAME);

        mDaoMaster = new DaoMaster(mDevOpenHelper.getWritableDatabase());
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
        if (mDevOpenHelper != null) {
            mDevOpenHelper.close();
            mDevOpenHelper = null;
        }
        if (mDaoSession != null) {
            mDaoSession.clear();
            mDaoSession = null;
        }
    }
}
