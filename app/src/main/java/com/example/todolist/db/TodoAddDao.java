package com.example.todolist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.todolist.entity.TodoEntity;

public class TodoAddDao {
    private static TodoAddDao instance;
    private final TodoDBHelper dbHelper;

    // 单例 对齐原有NoteDao写法
    public static synchronized TodoAddDao getInstance(Context context) {
        if (instance == null) {
            instance = new TodoAddDao(context.getApplicationContext());
        }
        return instance;
    }

    private TodoAddDao(Context context) {
        dbHelper = TodoDBHelper.getInstance(context);
    }

    /**
     * 插入新待办任务
     */
    public long addTodo(TodoEntity todo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TodoDBHelper.COLUMN_TITLE, todo.getTitle());
        values.put(TodoDBHelper.COLUMN_CONTENT, todo.getContent());
        values.put(TodoDBHelper.COLUMN_CREATE_TIME, todo.getCreateTime());
        values.put(TodoDBHelper.COLUMN_DEADLINE_TIME, todo.getDeadlineTime());
        values.put(TodoDBHelper.COLUMN_IS_COMPLETED, todo.isCompleted() ? 1 : 0);
        values.put(TodoDBHelper.COLUMN_PRIORITY, todo.getPriority());
        values.put(TodoDBHelper.COLUMN_COLOR, todo.getColorRes());
        values.put(TodoDBHelper.COLUMN_TAG, todo.getTag());
        values.put(TodoDBHelper.COLUMN_IS_TOP, todo.isTop() ? 1 : 0);
        values.put(TodoDBHelper.COLUMN_IS_ARCHIVED, todo.isArchived() ? 1 : 0);
        values.put(TodoDBHelper.COLUMN_REPEAT_TYPE, todo.getRepeatType());
        // 新增：存入当前待办所属用户ID
        values.put(TodoDBHelper.COLUMN_USER_ID, todo.getUserId());

        long id = db.insert(TodoDBHelper.TABLE_TODO, null, values);
        db.close();
        return id;
    }
}