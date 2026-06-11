package com.example.todolist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.example.todolist.db.TodoDBHelper;
import com.example.todolist.entity.TodoEntity;

public class TodoEditDao {
    private static TodoEditDao instance;
    private final TodoDBHelper dbHelper;

    public static synchronized TodoEditDao getInstance(Context context) {
        if (instance == null) {
            instance = new TodoEditDao(context.getApplicationContext());
        }
        return instance;
    }

    private TodoEditDao(Context context) {
        dbHelper = TodoDBHelper.getInstance(context);
    }

    /**
     * 更新整条任务数据
     */
    public int updateTodo(TodoEntity todo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TodoDBHelper.COLUMN_TITLE, todo.getTitle());
        values.put(TodoDBHelper.COLUMN_CONTENT, todo.getContent());
        values.put(TodoDBHelper.COLUMN_DEADLINE_TIME, todo.getDeadlineTime());
        values.put(TodoDBHelper.COLUMN_IS_COMPLETED, todo.isCompleted() ? 1 : 0);
        values.put(TodoDBHelper.COLUMN_PRIORITY, todo.getPriority());
        values.put(TodoDBHelper.COLUMN_COLOR, todo.getColorRes());
        values.put(TodoDBHelper.COLUMN_TAG, todo.getTag());
        values.put(TodoDBHelper.COLUMN_IS_TOP, todo.isTop() ? 1 : 0);
        values.put(TodoDBHelper.COLUMN_IS_ARCHIVED, todo.isArchived() ? 1 : 0);
        values.put(TodoDBHelper.COLUMN_REPEAT_TYPE, todo.getRepeatType());

        int rows = db.update(
                TodoDBHelper.TABLE_TODO,
                values,
                TodoDBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(todo.getId())}
        );
        db.close();
        return rows;
    }

    /**
     * 单独更新置顶状态
     */
    public int updateTopStatus(int id, boolean isTop) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TodoDBHelper.COLUMN_IS_TOP, isTop ? 1 : 0);

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
     * 单独更新归档状态
     */
    public int updateArchiveStatus(int id, boolean isArchive) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TodoDBHelper.COLUMN_IS_ARCHIVED, isArchive ? 1 : 0);

        int rows = db.update(
                TodoDBHelper.TABLE_TODO,
                values,
                TodoDBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }
}
