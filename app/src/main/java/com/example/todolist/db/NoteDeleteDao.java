package com.example.todolist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteDeleteDao {
    private final NoteDBHelper helper;

    public NoteDeleteDao(Context context) {
        helper = new NoteDBHelper(context);
    }

    // 移入回收站：标记is_deleted=1，加入回收站
    public void deleteToTrash(long noteId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction(); // 开启事务，保证操作原子性
        try {
            // 1. 把笔记加入回收站表
            ContentValues cv = new ContentValues();
            cv.put(NoteDBHelper.COL_TRASH_NOTE_ID, noteId);
            cv.put(NoteDBHelper.COL_TRASH_DELETE_TIME, getCurrentTime());
            db.insert(NoteDBHelper.TABLE_TRASH, null, cv);

            // 2. 标记为已删除（核心修改：不再删除note表记录）
            ContentValues updateValues = new ContentValues();
            updateValues.put(NoteDBHelper.COL_IS_DELETED, 1);
            db.update(NoteDBHelper.TABLE_NOTE, updateValues,
                    NoteDBHelper.COL_ID + "=?", new String[]{String.valueOf(noteId)});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // 恢复笔记：从回收站移除，取消删除标记
    public void restoreNote(long noteId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            // 1. 从回收站表删除
            db.delete(NoteDBHelper.TABLE_TRASH,
                    NoteDBHelper.COL_TRASH_NOTE_ID + "=?",
                    new String[]{String.valueOf(noteId)});

            // 2. 取消删除标记
            ContentValues updateValues = new ContentValues();
            updateValues.put(NoteDBHelper.COL_IS_DELETED, 0);
            db.update(NoteDBHelper.TABLE_NOTE, updateValues,
                    NoteDBHelper.COL_ID + "=?", new String[]{String.valueOf(noteId)});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // 彻底删除：同时删两张表
    public void deleteForever(long noteId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(NoteDBHelper.TABLE_NOTE,
                    NoteDBHelper.COL_ID + "=?",
                    new String[]{String.valueOf(noteId)});
            db.delete(NoteDBHelper.TABLE_TRASH,
                    NoteDBHelper.COL_TRASH_NOTE_ID + "=?",
                    new String[]{String.valueOf(noteId)});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}