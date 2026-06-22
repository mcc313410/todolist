package com.example.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class TodoDeleteDao {
    private static TodoDeleteDao instance;
    private final TodoDBHelper dbHelper;

    public static synchronized TodoDeleteDao getInstance(Context context) {
        if (instance == null) {
            instance = new TodoDeleteDao(context.getApplicationContext());
        }
        return instance;
    }

    private TodoDeleteDao(Context context) {
        dbHelper = TodoDBHelper.getInstance(context);
    }

    /**
     * 根据ID删除单条任务
     */
    public int deleteTodoById(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                TodoDBHelper.TABLE_TODO,
                TodoDBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }

    /**
     * 清空所有归档任务（拓展功能：一键清空）
     */
    public int deleteAllArchiveTodo() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                TodoDBHelper.TABLE_TODO,
                TodoDBHelper.COLUMN_IS_ARCHIVED + " = ?",
                new String[]{"1"}
        );
        db.close();
        return rows;
    }
}