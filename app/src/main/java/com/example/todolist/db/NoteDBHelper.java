package com.example.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "note.db";
    private static final int DB_VERSION = 3;

    // 笔记表
    public static final String TABLE_NOTE = "note";
    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_CONTENT = "content";
    public static final String COL_CREATE_TIME = "create_time";
    public static final String COL_IS_TOP = "is_top";
    public static final String COL_IS_COLLECT = "is_collect";
    public static final String COL_OBJECT_ID = "object_id";
    public static final String COL_IS_SYNC = "is_sync";
    public static final String COL_IS_DELETED = "is_deleted";

    // 回收站表
    public static final String TABLE_TRASH = "trash";
    public static final String COL_TRASH_ID = "_id";
    public static final String COL_TRASH_NOTE_ID = "note_id";
    public static final String COL_TRASH_DELETE_TIME = "delete_time";

    private static final String CREATE_TABLE_NOTE =
            "CREATE TABLE " + TABLE_NOTE + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_TITLE + " TEXT, " +
                    COL_CONTENT + " TEXT, " +
                    COL_CREATE_TIME + " TEXT, " +
                    COL_IS_TOP + " INTEGER DEFAULT 0, " +
                    COL_IS_COLLECT + " INTEGER DEFAULT 0, " +
                    COL_OBJECT_ID + " TEXT, " +
                    COL_IS_SYNC + " INTEGER DEFAULT 0, " +
                    COL_IS_DELETED + " INTEGER DEFAULT 0)"; // 新增删除标记

    // 创建回收站表 SQL
    private static final String CREATE_TABLE_TRASH =
            "CREATE TABLE " + TABLE_TRASH + " (" +
                    COL_TRASH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_TRASH_NOTE_ID + " INTEGER, " +
                    COL_TRASH_DELETE_TIME + " TEXT)";

    public NoteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTE);
        db.execSQL(CREATE_TABLE_TRASH);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_NOTE + " ADD COLUMN " + COL_IS_DELETED + " INTEGER DEFAULT 0");
        }
    }
}