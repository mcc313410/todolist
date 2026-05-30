package com.example.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class NoteDeleteDao {
    private NoteDBHelper dbHelper;

    public NoteDeleteDao(Context context) {
        dbHelper = new NoteDBHelper(context);
    }

    // 根据本地ID删除笔记
    public void deleteNote(long localId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(NoteDBHelper.TABLE_NOTE,
                NoteDBHelper.COL_ID + "=?",
                new String[]{String.valueOf(localId)});
    }
}