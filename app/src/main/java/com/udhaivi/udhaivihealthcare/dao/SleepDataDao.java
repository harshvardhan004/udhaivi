package com.udhaivi.udhaivihealthcare.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.udhaivi.udhaivihealthcare.model.SleepData;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SLEEP_DATA".
*/
public class SleepDataDao extends AbstractDao<SleepData, String> {

    public static final String TABLENAME = "SLEEP_DATA";

    /**
     * Properties of entity SleepData.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Time = new Property(0, String.class, "time", true, "TIME");
        public final static Property Address = new Property(1, String.class, "address", false, "ADDRESS");
        public final static Property DateString = new Property(2, String.class, "dateString", false, "DATE_STRING");
    }


    public SleepDataDao(DaoConfig config) {
        super(config);
    }
    
    public SleepDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SLEEP_DATA\" (" + //
                "\"TIME\" TEXT PRIMARY KEY NOT NULL ," + // 0: time
                "\"ADDRESS\" TEXT," + // 1: address
                "\"DATE_STRING\" TEXT);"); // 2: dateString
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SLEEP_DATA\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, SleepData entity) {
        stmt.clearBindings();
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(1, time);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(2, address);
        }
 
        String dateString = entity.getDateString();
        if (dateString != null) {
            stmt.bindString(3, dateString);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, SleepData entity) {
        stmt.clearBindings();
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(1, time);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(2, address);
        }
 
        String dateString = entity.getDateString();
        if (dateString != null) {
            stmt.bindString(3, dateString);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public SleepData readEntity(Cursor cursor, int offset) {
        SleepData entity = new SleepData( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // time
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // address
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // dateString
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, SleepData entity, int offset) {
        entity.setTime(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setAddress(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDateString(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final String updateKeyAfterInsert(SleepData entity, long rowId) {
        return entity.getTime();
    }
    
    @Override
    public String getKey(SleepData entity) {
        if(entity != null) {
            return entity.getTime();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(SleepData entity) {
        return entity.getTime() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
