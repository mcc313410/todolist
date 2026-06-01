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

    // 查询所有未删除的笔记（过滤is_deleted=0）
    public List<NoteEntity> queryAllNotes() {
        List<NoteEntity> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        // 新增where条件：仅查询未删除的笔记
        Cursor cursor = db.query(NoteDBHelper.TABLE_NOTE, null,
                NoteDBHelper.COL_IS_DELETED + "=?", new String[]{"0"},
                null, null,
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

    // 根据ID查询笔记（兼容已删除状态）
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

    // 搜索笔记（仅搜索未删除的）
    public List<NoteEntity> searchNotes(String key) {
        List<NoteEntity> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        // 新增where条件：过滤未删除 + 模糊搜索标题/内容
        String whereClause = NoteDBHelper.COL_IS_DELETED + "=? AND (" +
                NoteDBHelper.COL_TITLE + " LIKE ? OR " + NoteDBHelper.COL_CONTENT + " LIKE ?)";
        String[] whereArgs = new String[]{"0", "%" + key + "%", "%" + key + "%"};

        Cursor cursor = db.query(NoteDBHelper.TABLE_NOTE, null,
                whereClause, whereArgs, null, null,
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