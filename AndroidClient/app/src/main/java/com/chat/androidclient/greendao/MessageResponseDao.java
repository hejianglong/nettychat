package com.chat.androidclient.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.chat.androidclient.mvvm.model.Contact.ConversationTYPEConverter;
import com.chat.androidclient.mvvm.model.ConverSationTYPE;

import com.chat.androidclient.mvvm.procotol.response.MessageResponse;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MESSAGE_RESPONSE".
*/
public class MessageResponseDao extends AbstractDao<MessageResponse, Long> {

    public static final String TABLENAME = "MESSAGE_RESPONSE";

    /**
     * Properties of entity MessageResponse.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property FromUserId = new Property(1, Long.class, "fromUserId", false, "FROM_USER_ID");
        public final static Property ToUserId = new Property(2, Long.class, "toUserId", false, "TO_USER_ID");
        public final static Property Time = new Property(3, Long.class, "time", false, "TIME");
        public final static Property Message = new Property(4, String.class, "message", false, "MESSAGE");
        public final static Property Type = new Property(5, Integer.class, "type", false, "TYPE");
    }

    private final ConversationTYPEConverter typeConverter = new ConversationTYPEConverter();

    public MessageResponseDao(DaoConfig config) {
        super(config);
    }
    
    public MessageResponseDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MESSAGE_RESPONSE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"FROM_USER_ID\" INTEGER," + // 1: fromUserId
                "\"TO_USER_ID\" INTEGER," + // 2: toUserId
                "\"TIME\" INTEGER," + // 3: time
                "\"MESSAGE\" TEXT," + // 4: message
                "\"TYPE\" INTEGER);"); // 5: type
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MESSAGE_RESPONSE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MessageResponse entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long fromUserId = entity.getFromUserId();
        if (fromUserId != null) {
            stmt.bindLong(2, fromUserId);
        }
 
        Long toUserId = entity.getToUserId();
        if (toUserId != null) {
            stmt.bindLong(3, toUserId);
        }
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(4, time);
        }
 
        String message = entity.getMessage();
        if (message != null) {
            stmt.bindString(5, message);
        }
 
        ConverSationTYPE type = entity.getConversationType();
        if (type != null) {
            stmt.bindLong(6, typeConverter.convertToDatabaseValue(type));
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MessageResponse entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long fromUserId = entity.getFromUserId();
        if (fromUserId != null) {
            stmt.bindLong(2, fromUserId);
        }
 
        Long toUserId = entity.getToUserId();
        if (toUserId != null) {
            stmt.bindLong(3, toUserId);
        }
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(4, time);
        }
 
        String message = entity.getMessage();
        if (message != null) {
            stmt.bindString(5, message);
        }
 
        ConverSationTYPE type = entity.getConversationType();
        if (type != null) {
            stmt.bindLong(6, typeConverter.convertToDatabaseValue(type));
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public MessageResponse readEntity(Cursor cursor, int offset) {
        MessageResponse entity = new MessageResponse( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // fromUserId
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // toUserId
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // time
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // message
            cursor.isNull(offset + 5) ? null : typeConverter.convertToEntityProperty(cursor.getInt(offset + 5)) // type
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MessageResponse entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFromUserId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setToUserId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setTime(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setMessage(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setConversationType(cursor.isNull(offset + 5) ? null : typeConverter.convertToEntityProperty(cursor.getInt(offset + 5)));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(MessageResponse entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(MessageResponse entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MessageResponse entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
