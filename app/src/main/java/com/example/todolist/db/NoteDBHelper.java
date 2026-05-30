package com.example.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDBHelper extends SQLiteOpenHelper {
    // 数据库名、版本
    private static final String DB_NAME = "note_db";
    private static final int DB_VERSION = 2; // 版本升级为2（新增字段）

    // 表名
    public static final String TABLE_NOTE = "note_table";

    // 原有字段
    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_CONTENT = "content";
    public static final String COL_TIME = "create_time";
    public static final String COL_TOP = "is_top";
    public static final String COL_COLLECT = "is_collect";

    // 新增字段：云端ID、同步标记
    public static final String COL_OBJECT_ID = "object_id";
    public static final String COL_IS_SYNC = "is_sync";

    public NoteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建表语句：包含所有新旧字段
        String createSql = "CREATE TABLE " + TABLE_NOTE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT, "
                + COL_CONTENT + " TEXT, "
                + COL_TIME + " TEXT, "
                + COL_TOP + " INTEGER, "
                + COL_COLLECT + " INTEGER, "
                + COL_OBJECT_ID + " TEXT, "
                + COL_IS_SYNC + " INTEGER)";
        db.execSQL(createSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 版本1升级到版本2，追加两个字段
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL("ALTER TABLE " + TABLE_NOTE + " ADD COLUMN " + COL_OBJECT_ID + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_NOTE + " ADD COLUMN " + COL_IS_SYNC + " INTEGER DEFAULT 0");
        }
    }
}