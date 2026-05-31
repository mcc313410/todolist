package com.example.todolist.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.todolist.entity.NoteEntity;
import java.util.ArrayList;
import java.util.List;

public class NoteQueryDao {
    private NoteDBHelper helper;

    public NoteQueryDao(Context context) {
        helper = new NoteDBHelper(context);
    }

    public List<NoteEntity> queryAllNotes() {
        List<NoteEntity> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(NoteDBHelper.TABLE_NOTE, null, null, null, null, null,
                NoteDBHelper.COL_IS_TOP + " DESC, " + NoteDBHelper.COL_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            NoteEntity note = new NoteEntity();
            note.setId(cursor.getLong(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_ID)));
            note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_TITLE)));
            note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_CONTENT)));
            note.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_CREATE_TIME)));
            note.setIsCollect(cursor.getInt(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_IS_COLLECT)));
            note.setIsTop(cursor.getInt(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_IS_TOP)));
            note.setObjectId(cursor.getString(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_OBJECT_ID)));
            note.setSync(cursor.getInt(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_IS_SYNC)) == 1);
            list.add(note);
        }
        cursor.close();
        db.close();
        return list;
    }

    public NoteEntity queryNoteById(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(NoteDBHelper.TABLE_NOTE, null,
                NoteDBHelper.COL_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        NoteEntity note = null;
        if (cursor.moveToFirst()) {
            note = new NoteEntity();
            note.setId(cursor.getLong(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_ID)));
            note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_TITLE)));
            note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_CONTENT)));
            note.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_CREATE_TIME)));
            note.setIsCollect(cursor.getInt(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_IS_COLLECT)));
            note.setIsTop(cursor.getInt(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_IS_TOP)));
            note.setObjectId(cursor.getString(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_OBJECT_ID)));
            note.setSync(cursor.getInt(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_IS_SYNC)) == 1);
        }
        cursor.close();
        db.close();
        return note;
    }

    public List<NoteEntity> searchNotes(String key) {
        List<NoteEntity> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(NoteDBHelper.TABLE_NOTE, null,
                NoteDBHelper.COL_TITLE + " LIKE ? OR " + NoteDBHelper.COL_CONTENT + " LIKE ?",
                new String[]{"%" + key + "%", "%" + key + "%"}, null, null,
                NoteDBHelper.COL_IS_TOP + " DESC, " + NoteDBHelper.COL_CREATE_TIME + " DESC");
        while (cursor.moveToNext()) {
            NoteEntity note = new NoteEntity();
            note.setId(cursor.getLong(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_ID)));
            note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_TITLE)));
            note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_CONTENT)));
            note.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_CREATE_TIME)));
            note.setIsCollect(cursor.getInt(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_IS_COLLECT)));
            note.setIsTop(cursor.getInt(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_IS_TOP)));
            note.setObjectId(cursor.getString(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_OBJECT_ID)));
            note.setSync(cursor.getInt(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_IS_SYNC)) == 1);
            list.add(note);
        }
        cursor.close();
        db.close();
        return list;
    }
}