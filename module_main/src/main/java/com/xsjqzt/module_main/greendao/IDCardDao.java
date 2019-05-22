package com.xsjqzt.module_main.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.xsjqzt.module_main.greendao.entity.IDCard;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "IDCARD".
*/
public class IDCardDao extends AbstractDao<IDCard, Long> {

    public static final String TABLENAME = "IDCARD";

    /**
     * Properties of entity IDCard.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Sid = new Property(1, int.class, "sid", false, "SID");
        public final static Property Sn = new Property(2, String.class, "sn", false, "SN");
        public final static Property User_name = new Property(3, String.class, "user_name", false, "USER_NAME");
        public final static Property User_id = new Property(4, int.class, "user_id", false, "USER_ID");
    }


    public IDCardDao(DaoConfig config) {
        super(config);
    }
    
    public IDCardDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"IDCARD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"SID\" INTEGER NOT NULL ," + // 1: sid
                "\"SN\" TEXT NOT NULL UNIQUE ," + // 2: sn
                "\"USER_NAME\" TEXT," + // 3: user_name
                "\"USER_ID\" INTEGER NOT NULL );"); // 4: user_id
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"IDCARD\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, IDCard entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getSid());
        stmt.bindString(3, entity.getSn());
 
        String user_name = entity.getUser_name();
        if (user_name != null) {
            stmt.bindString(4, user_name);
        }
        stmt.bindLong(5, entity.getUser_id());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, IDCard entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getSid());
        stmt.bindString(3, entity.getSn());
 
        String user_name = entity.getUser_name();
        if (user_name != null) {
            stmt.bindString(4, user_name);
        }
        stmt.bindLong(5, entity.getUser_id());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public IDCard readEntity(Cursor cursor, int offset) {
        IDCard entity = new IDCard( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // sid
            cursor.getString(offset + 2), // sn
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // user_name
            cursor.getInt(offset + 4) // user_id
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, IDCard entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSid(cursor.getInt(offset + 1));
        entity.setSn(cursor.getString(offset + 2));
        entity.setUser_name(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setUser_id(cursor.getInt(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(IDCard entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(IDCard entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(IDCard entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
