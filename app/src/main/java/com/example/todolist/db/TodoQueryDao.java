package com.example.todolist.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.todolist.entity.TodoEntity;
import java.util.ArrayList;
import java.util.List;

public class TodoQueryDao {
    private static TodoQueryDao instance;
    private final TodoDBHelper dbHelper;

    public static synchronized TodoQueryDao getInstance(Context context) {
        if (instance == null) {
            instance = new TodoQueryDao(context.getApplicationContext());
        }
        return instance;
    }

    private TodoQueryDao(Context context) {
        dbHelper = TodoDBHelper.getInstance(context);
    }

    /**
     * 根据ID查询单条任务
     */
    public TodoEntity queryTodoById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TodoDBHelper.TABLE_TODO,
                null,
                TodoDBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );
        TodoEntity todo = null;
        if (cursor.moveToFirst()) {
            todo = cursorToEntity(cursor);
        }
        cursor.close();
        db.close();
        return todo;
    }

    /**
     * 查询【未归档】所有任务（主列表）
     * 排序：置顶优先 -> 创建时间倒序
     */
    public List<TodoEntity> queryAllUnArchiveTodo() {
        List<TodoEntity> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TodoDBHelper.TABLE_TODO,
                null,
                TodoDBHelper.COLUMN_IS_ARCHIVED + " = ?",
                new String[]{"0"},
                null, null,
                TodoDBHelper.COLUMN_IS_TOP + " DESC, " + TodoDBHelper.COLUMN_CREATE_TIME + " DESC"
        );
        while (cursor.moveToNext()) {
            list.add(cursorToEntity(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 查询【已归档】所有任务（归档列表）
     */
    public List<TodoEntity> queryAllArchiveTodo() {
        List<TodoEntity> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TodoDBHelper.TABLE_TODO,
                null,
                TodoDBHelper.COLUMN_IS_ARCHIVED + " = ?",
                new String[]{"1"},
                null, null,
                TodoDBHelper.COLUMN_CREATE_TIME + " DESC"
        );
        while (cursor.moveToNext()) {
            list.add(cursorToEntity(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 搜索未归档任务：标题/内容 模糊匹配
     * @param keyword 搜索关键词
     */
    public List<TodoEntity> searchUnArchiveTodo(String keyword) {
        List<TodoEntity> list = new ArrayList<>();
        if (keyword == null) {
            return queryAllUnArchiveTodo();
        }
        String key = keyword.trim();
        // 关键词为空，返回全部数据
        if (key.isEmpty()) {
            return queryAllUnArchiveTodo();
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 查询条件：未归档 并且 标题或内容包含关键词
        String selection = TodoDBHelper.COLUMN_IS_ARCHIVED + " = ? "
                + " AND ("
                + TodoDBHelper.COLUMN_TITLE + " LIKE ? "
                + " OR "
                + TodoDBHelper.COLUMN_CONTENT + " LIKE ?"
                + ")";
        String[] args = {"0", "%" + key + "%", "%" + key + "%"};

        Cursor cursor = db.query(
                TodoDBHelper.TABLE_TODO,
                null,
                selection,
                args,
                null, null,
                TodoDBHelper.COLUMN_IS_TOP + " DESC, " + TodoDBHelper.COLUMN_CREATE_TIME + " DESC"
        );

        while (cursor.moveToNext()) {
            list.add(cursorToEntity(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * Cursor 转 TodoEntity（完全对齐 DB 字段常量，不会报字段异常）
     */
    private TodoEntity cursorToEntity(Cursor cursor) {
        TodoEntity todo = new TodoEntity();
        todo.setId(cursor.getInt(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_ID)));
        todo.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_TITLE)));
        todo.setContent(cursor.getString(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_CONTENT)));
        todo.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_CREATE_TIME)));
        todo.setDeadlineTime(cursor.getLong(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_DEADLINE_TIME)));
        todo.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_IS_COMPLETED)) == 1);
        todo.setPriority(cursor.getInt(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_PRIORITY)));
        todo.setColorRes(cursor.getInt(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_COLOR)));
        todo.setTag(cursor.getString(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_TAG)));
        todo.setTop(cursor.getInt(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_IS_TOP)) == 1);
        todo.setArchived(cursor.getInt(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_IS_ARCHIVED)) == 1);
        todo.setRepeatType(cursor.getInt(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_REPEAT_TYPE)));
        return todo;
    }

    /**
     * 获取全部未归档任务总数
     */
    public int getAllUnArchiveTodoCount() {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TodoDBHelper.TABLE_TODO + " WHERE " + TodoDBHelper.COLUMN_IS_ARCHIVED + " = 0", null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /**
     * 获取已完成、未归档任务数量
     */
    public int getCompletedUnArchiveTodoCount() {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TodoDBHelper.TABLE_TODO + " WHERE " + TodoDBHelper.COLUMN_IS_ARCHIVED + " = 0 AND " + TodoDBHelper.COLUMN_IS_COMPLETED + " = 1", null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /**
     * 获取今日截止、未完成、未归档任务数量（今日待办）
     * @param todayStartMillis 今日0点时间戳
     * @param tomorrowStartMillis 明日0点时间戳
     */
    public int getTodayUnCompleteTodoCount(long todayStartMillis, long tomorrowStartMillis) {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + TodoDBHelper.TABLE_TODO
                + " WHERE " + TodoDBHelper.COLUMN_IS_ARCHIVED + " = 0 "
                + " AND " + TodoDBHelper.COLUMN_IS_COMPLETED + " = 0 "
                + " AND " + TodoDBHelper.COLUMN_DEADLINE_TIME + " >= ? "
                + " AND " + TodoDBHelper.COLUMN_DEADLINE_TIME + " < ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(todayStartMillis), String.valueOf(tomorrowStartMillis)});
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /**
     * 获取截止时间早于今天、未完成、未归档任务数量（逾期任务）
     * @param todayStartMillis 今日0点时间戳
     */
    public int getOverdueUnCompleteTodoCount(long todayStartMillis) {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + TodoDBHelper.TABLE_TODO
                + " WHERE " + TodoDBHelper.COLUMN_IS_ARCHIVED + " = 0 "
                + " AND " + TodoDBHelper.COLUMN_IS_COMPLETED + " = 0 "
                + " AND " + TodoDBHelper.COLUMN_DEADLINE_TIME + " < ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(todayStartMillis)});
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
    /**
     * 查询今日所有任务（包含归档、全部状态）
     * @param todayZero 今日0点时间戳
     * @param tomorrowZero 明日0点时间戳
     */
    public int getAllTodayTodoCount(long todayZero, long tomorrowZero) {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + TodoDBHelper.TABLE_TODO
                + " WHERE " + TodoDBHelper.COLUMN_DEADLINE_TIME + " >= ? "
                + " AND " + TodoDBHelper.COLUMN_DEADLINE_TIME + " < ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(todayZero), String.valueOf(tomorrowZero)});
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /**
     * 查询今日已完成任务（包含归档）
     * @param todayZero 今日0点时间戳
     * @param tomorrowZero 明日0点时间戳
     */
    public int getCompletedTodayTodoCount(long todayZero, long tomorrowZero) {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + TodoDBHelper.TABLE_TODO
                + " WHERE " + TodoDBHelper.COLUMN_DEADLINE_TIME + " >= ? "
                + " AND " + TodoDBHelper.COLUMN_DEADLINE_TIME + " < ? "
                + " AND " + TodoDBHelper.COLUMN_IS_COMPLETED + " = 1";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(todayZero), String.valueOf(tomorrowZero)});
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
    /**
     * 查询全部逾期任务（包含归档，未完成，截止时间早于今日0点）
     */
    public List<TodoEntity> getAllOverdueTodo(long todayZeroMillis) {
        List<TodoEntity> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + TodoDBHelper.TABLE_TODO
                + " WHERE " + TodoDBHelper.COLUMN_IS_COMPLETED + " = 0 "
                + " AND " + TodoDBHelper.COLUMN_DEADLINE_TIME + " < ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(todayZeroMillis)});
        while (cursor.moveToNext()) {
            list.add(cursorToEntity(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }
}