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

    // 移入回收站
    public void deleteToTrash(long noteId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(NoteDBHelper.COL_TRASH_NOTE_ID, noteId);
        cv.put(NoteDBHelper.COL_TRASH_DELETE_TIME, getCurrentTime());

        db.insert(NoteDBHelper.TABLE_TRASH, null, cv);
        db.delete(NoteDBHelper.TABLE_NOTE, NoteDBHelper.COL_ID + "=?", new String[]{String.valueOf(noteId)});

        db.close();
    }

    // 彻底删除（从回收站和笔记表中删除）
    public void deleteForever(long noteId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        // 从笔记表中删除
        db.delete(NoteDBHelper.TABLE_NOTE, NoteDBHelper.COL_ID + "=?", new String[]{String.valueOf(noteId)});
        // 从回收站表中删除
        db.delete(NoteDBHelper.TABLE_TRASH, NoteDBHelper.COL_TRASH_NOTE_ID + "=?", new String[]{String.valueOf(noteId)});
        db.close();
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}