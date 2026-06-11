package com.example.todolist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.example.todolist.db.TodoDBHelper;

public class TodoCompleteDao {
    private static TodoCompleteDao instance;
    private final TodoDBHelper dbHelper;

    public static synchronized TodoCompleteDao getInstance(Context context) {
        if (instance == null) {
            instance = new TodoCompleteDao(context.getApplicationContext());
        }
        return instance;
    }

    private TodoCompleteDao(Context context) {
        dbHelper = TodoDBHelper.getInstance(context);
    }

    /**
     * 切换任务完成状态（列表复选框点击调用）
     */
    public int switchCompleteStatus(int id, boolean isComplete) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TodoDBHelper.COLUMN_IS_COMPLETED, isComplete ? 1 : 0);

        int rows = db.update(
                TodoDBHelper.TABLE_TODO,
                values,
                TodoDBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }

    /**
     * 自动归档所有已完成任务
     */
    public int autoArchiveCompletedTodo() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TodoDBHelper.COLUMN_IS_ARCHIVED, 1);

        int rows = db.update(
                TodoDBHelper.TABLE_TODO,
                values,
                TodoDBHelper.COLUMN_IS_COMPLETED + " = ?",
                new String[]{"1"}
        );
        db.close();
        return rows;
    }
}