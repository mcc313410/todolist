package com.example.todolist.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.todolist.entity.NoteEntity;

import java.util.ArrayList;
import java.util.List;

public class NoteQueryDao {
    private NoteDBHelper dbHelper;

    public NoteQueryDao(Context context) {
        dbHelper = new NoteDBHelper(context);
    }

    // 根据本地ID查询单条笔记
    public NoteEntity queryNoteById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(NoteDBHelper.TABLE_NOTE,
                new String[]{
                        NoteDBHelper.COL_ID,
                        NoteDBHelper.COL_TITLE,
                        NoteDBHelper.COL_CONTENT,
                        NoteDBHelper.COL_TIME,
                        NoteDBHelper.COL_TOP,
                        NoteDBHelper.COL_COLLECT,
                        NoteDBHelper.COL_OBJECT_ID,
                        NoteDBHelper.COL_IS_SYNC
                },
                NoteDBHelper.COL_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            NoteEntity note = new NoteEntity();
            note.setId(cursor.getLong(0));
            note.setTitle(cursor.getString(1));
            note.setContent(cursor.getString(2));
            note.setCreateTime(cursor.getString(3));
            note.setIsTop(cursor.getInt(4));
            note.setIsCollect(cursor.getInt(5));
            note.setObjectId(cursor.getString(6));
            note.setSync(cursor.getInt(7) == 1);
            cursor.close();
            return note;
        }
        return null;
    }

    // 查询全部笔记（置顶优先排序）
    public List<NoteEntity> queryAllNotes() {
        List<NoteEntity> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(NoteDBHelper.TABLE_NOTE,
                new String[]{
                        NoteDBHelper.COL_ID,
                        NoteDBHelper.COL_TITLE,
                        NoteDBHelper.COL_CONTENT,
                        NoteDBHelper.COL_TIME,
                        NoteDBHelper.COL_TOP,
                        NoteDBHelper.COL_COLLECT,
                        NoteDBHelper.COL_OBJECT_ID,
                        NoteDBHelper.COL_IS_SYNC
                },
                null, null, null, null,
                NoteDBHelper.COL_TOP + " DESC, " + NoteDBHelper.COL_ID + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                NoteEntity note = new NoteEntity();
                note.setId(cursor.getLong(0));
                note.setTitle(cursor.getString(1));
                note.setContent(cursor.getString(2));
                note.setCreateTime(cursor.getString(3));
                note.setIsTop(cursor.getInt(4));
                note.setIsCollect(cursor.getInt(5));
                note.setObjectId(cursor.getString(6));
                note.setSync(cursor.getInt(7) == 1);
                list.add(note);
            }
            cursor.close();
        }
        return list;
    }
}