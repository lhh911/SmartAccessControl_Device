package com.xsjqzt.module_main.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.xsjqzt.module_main.greendao.entity.ICCard;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ICCARD".
*/
public class ICCardDao extends AbstractDao<ICCard, Long> {

    public static final String TABLENAME = "ICCARD";

    /**
     * Properties of entity ICCard.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Sid = new Property(1, int.class, "sid", false, "SID");
        public final static Property Sn = new Property(2, String.class, "sn", false, "SN");
        public final static Property Update_time = new Property(3, int.class, "update_time", false, "UPDATE_TIME");
        public final static Property User_name = new Property(4, String.class, "user_name", false, "USER_NAME");
        public final static Property User_id = new Property(5, int.class, "user_id", false, "USER_ID");
    }


    public ICCardDao(DaoConfig config) {
        super(config);
    }
    
    public ICCardDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ICCARD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"SID\" INTEGER NOT NULL ," + // 1: sid
                "\"SN\" TEXT NOT NULL UNIQUE ," + // 2: sn
                "\"UPDATE_TIME\" INTEGER NOT NULL ," + // 3: update_time
                "\"USER_NAME\" TEXT," + // 4: user_name
                "\"USER_ID\" INTEGER NOT NULL );"); // 5: user_id
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ICCARD\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ICCard entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getSid());
        stmt.bindString(3, entity.getSn());
        stmt.bindLong(4, entity.getUpdate_time());
 
        String user_name = entity.getUser_name();
        if (user_name != null) {
            stmt.bindString(5, user_name);
        }
        stmt.bindLong(6, entity.getUser_id());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ICCard entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getSid());
        stmt.bindString(3, entity.getSn());
        stmt.bindLong(4, entity.getUpdate_time());
 
        String user_name = entity.getUser_name();
        if (user_name != null) {
            stmt.bindString(5, user_name);
        }
        stmt.bindLong(6, entity.getUser_id());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public ICCard readEntity(Cursor cursor, int offset) {
        ICCard entity = new ICCard( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // sid
            cursor.getString(offset + 2), // sn
            cursor.getInt(offset + 3), // update_time
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // user_name
            cursor.getInt(offset + 5) // user_id
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ICCard entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSid(cursor.getInt(offset + 1));
        entity.setSn(cursor.getString(offset + 2));
        entity.setUpdate_time(cursor.getInt(offset + 3));
        entity.setUser_name(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setUser_id(cursor.getInt(offset + 5));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ICCard entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ICCard entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ICCard entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
