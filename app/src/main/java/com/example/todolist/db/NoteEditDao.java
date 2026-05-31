package com.example.todolist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.example.todolist.entity.NoteEntity;

public class NoteEditDao {
    private final NoteDBHelper helper;

    public NoteEditDao(Context context) {
        helper = new NoteDBHelper(context);
    }

    public void update(NoteEntity note) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NoteDBHelper.COL_TITLE, note.getTitle());
        values.put(NoteDBHelper.COL_CONTENT, note.getContent());
        values.put(NoteDBHelper.COL_CREATE_TIME, note.getCreateTime());
        values.put(NoteDBHelper.COL_IS_TOP, note.getIsTop());
        values.put(NoteDBHelper.COL_IS_COLLECT, note.getIsCollect());
        values.put(NoteDBHelper.COL_OBJECT_ID, note.getObjectId());
        values.put(NoteDBHelper.COL_IS_SYNC, note.isSync() ? 1 : 0);

        db.update(NoteDBHelper.TABLE_NOTE, values, NoteDBHelper.COL_ID + "=?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
}