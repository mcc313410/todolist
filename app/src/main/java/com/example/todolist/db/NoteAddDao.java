package com.example.todolist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.example.todolist.entity.NoteEntity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteAddDao {
    private SQLiteDatabase db;

    public NoteAddDao(Context context) {
        NoteDBHelper helper = new NoteDBHelper(context);
        db = helper.getWritableDatabase();
    }

    public void addNote(NoteEntity note) {
        ContentValues values = new ContentValues();
        values.put(NoteDBHelper.COL_TITLE, note.getTitle());
        values.put(NoteDBHelper.COL_CONTENT, note.getContent());
        values.put(NoteDBHelper.COL_TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));
        values.put(NoteDBHelper.COL_TOP, 0);
        values.put(NoteDBHelper.COL_COLLECT, 0);
        db.insert(NoteDBHelper.TABLE_NOTE, null, values);
    }
}