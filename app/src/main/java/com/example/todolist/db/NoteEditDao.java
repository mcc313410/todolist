package com.example.todolist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.todolist.entity.NoteEntity;

public class NoteEditDao {
    private NoteDBHelper dbHelper;

    public NoteEditDao(Context context) {
        dbHelper = new NoteDBHelper(context);
    }

    // 更新笔记（包含云端ID、同步状态）
    public void updateNote(NoteEntity note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NoteDBHelper.COL_TITLE, note.getTitle());
        values.put(NoteDBHelper.COL_CONTENT, note.getContent());
        values.put(NoteDBHelper.COL_TIME, note.getCreateTime());
        values.put(NoteDBHelper.COL_TOP, note.getIsTop());
        values.put(NoteDBHelper.COL_COLLECT, note.getIsCollect());
        // 保存云端ID和同步标记
        values.put(NoteDBHelper.COL_OBJECT_ID, note.getObjectId());
        values.put(NoteDBHelper.COL_IS_SYNC, note.isSync() ? 1 : 0);

        db.update(NoteDBHelper.TABLE_NOTE, values,
                NoteDBHelper.COL_ID + "=?",
                new String[]{String.valueOf(note.getId())});
    }

    // 新增笔记方法（如果你的新增逻辑在这里，同步状态默认0未同步）
    public long insertNote(NoteEntity note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NoteDBHelper.COL_TITLE, note.getTitle());
        values.put(NoteDBHelper.COL_CONTENT, note.getContent());
        values.put(NoteDBHelper.COL_TIME, note.getCreateTime());
        values.put(NoteDBHelper.COL_TOP, note.getIsTop());
        values.put(NoteDBHelper.COL_COLLECT, note.getIsCollect());
        values.put(NoteDBHelper.COL_OBJECT_ID, note.getObjectId());
        values.put(NoteDBHelper.COL_IS_SYNC, 0); // 新建笔记默认未同步
        return db.insert(NoteDBHelper.TABLE_NOTE, null, values);
    }
}