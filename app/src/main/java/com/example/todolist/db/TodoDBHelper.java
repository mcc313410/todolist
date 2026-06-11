package com.example.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "todo_db.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_TODO = "todo_table";

    // 表字段
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_CREATE_TIME = "create_time";
    public static final String COLUMN_DEADLINE_TIME = "deadline_time";
    public static final String COLUMN_IS_COMPLETED = "is_completed";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_TAG = "tag";
    public static final String COLUMN_IS_TOP = "is_top";
    public static final String COLUMN_IS_ARCHIVED = "is_archived";
    public static final String COLUMN_REPEAT_TYPE = "repeat_type";

    private static TodoDBHelper instance;

    public static synchronized TodoDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new TodoDBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private TodoDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableSql = "CREATE TABLE " + TABLE_TODO + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_CREATE_TIME + " INTEGER, " +
                COLUMN_DEADLINE_TIME + " INTEGER, " +
                COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0, " +
                COLUMN_PRIORITY + " INTEGER DEFAULT 1, " +
                COLUMN_COLOR + " INTEGER, " +
                COLUMN_TAG + " TEXT, " +
                COLUMN_IS_TOP + " INTEGER DEFAULT 0, " +
                COLUMN_IS_ARCHIVED + " INTEGER DEFAULT 0, " +
                COLUMN_REPEAT_TYPE + " INTEGER DEFAULT 0)";
        db.execSQL(createTableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }
}
