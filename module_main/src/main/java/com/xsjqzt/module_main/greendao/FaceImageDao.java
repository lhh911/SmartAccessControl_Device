package com.xsjqzt.module_main.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.xsjqzt.module_main.greendao.entity.FaceImage;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "FACE_IMAGE".
*/
public class FaceImageDao extends AbstractDao<FaceImage, Long> {

    public static final String TABLENAME = "FACE_IMAGE";

    /**
     * Properties of entity FaceImage.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Sid = new Property(1, int.class, "sid", false, "SID");
        public final static Property Image = new Property(2, String.class, "image", false, "IMAGE");
        public final static Property Ymimage = new Property(3, String.class, "ymimage", false, "YMIMAGE");
        public final static Property Status = new Property(4, int.class, "status", false, "STATUS");
        public final static Property Hasregist = new Property(5, boolean.class, "hasregist", false, "HASREGIST");
        public final static Property Mobile = new Property(6, String.class, "mobile", false, "MOBILE");
    }


    public FaceImageDao(DaoConfig config) {
        super(config);
    }
    
    public FaceImageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"FACE_IMAGE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"SID\" INTEGER NOT NULL UNIQUE ," + // 1: sid
                "\"IMAGE\" TEXT NOT NULL ," + // 2: image
                "\"YMIMAGE\" TEXT," + // 3: ymimage
                "\"STATUS\" INTEGER NOT NULL ," + // 4: status
                "\"HASREGIST\" INTEGER NOT NULL ," + // 5: hasregist
                "\"MOBILE\" TEXT);"); // 6: mobile
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"FACE_IMAGE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, FaceImage entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getSid());
        stmt.bindString(3, entity.getImage());
 
        String ymimage = entity.getYmimage();
        if (ymimage != null) {
            stmt.bindString(4, ymimage);
        }
        stmt.bindLong(5, entity.getStatus());
        stmt.bindLong(6, entity.getHasregist() ? 1L: 0L);
 
        String mobile = entity.getMobile();
        if (mobile != null) {
            stmt.bindString(7, mobile);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, FaceImage entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getSid());
        stmt.bindString(3, entity.getImage());
 
        String ymimage = entity.getYmimage();
        if (ymimage != null) {
            stmt.bindString(4, ymimage);
        }
        stmt.bindLong(5, entity.getStatus());
        stmt.bindLong(6, entity.getHasregist() ? 1L: 0L);
 
        String mobile = entity.getMobile();
        if (mobile != null) {
            stmt.bindString(7, mobile);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public FaceImage readEntity(Cursor cursor, int offset) {
        FaceImage entity = new FaceImage( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // sid
            cursor.getString(offset + 2), // image
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // ymimage
            cursor.getInt(offset + 4), // status
            cursor.getShort(offset + 5) != 0, // hasregist
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // mobile
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, FaceImage entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSid(cursor.getInt(offset + 1));
        entity.setImage(cursor.getString(offset + 2));
        entity.setYmimage(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setStatus(cursor.getInt(offset + 4));
        entity.setHasregist(cursor.getShort(offset + 5) != 0);
        entity.setMobile(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(FaceImage entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(FaceImage entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(FaceImage entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
