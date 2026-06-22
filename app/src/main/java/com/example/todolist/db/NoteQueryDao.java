package com.example.todolist.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.todolist.entity.NoteEntity;
import com.example.todolist.utils.SPUtil;
import java.util.ArrayList;
import java.util.List;

public class NoteQueryDao {
    private NoteDBHelper helper;

    public NoteQueryDao(Context context) {
        helper = new NoteDBHelper(context);
    }

    // 查询所有未删除的笔记（过滤is_deleted=0 + 当前登录用户）
    public List<NoteEntity> queryAllNotes() {
        List<NoteEntity> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String where = NoteDBHelper.COL_IS_DELETED + "=? AND " + NoteDBHelper.COL_USER_ID + "=?";
        String[] args = new String[]{"0", SPUtil.getCurrentUserId()};
        Cursor cursor = db.query(NoteDBHelper.TABLE_NOTE, null,
                where, args,
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
            // 读取用户ID
            note.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_USER_ID)));
            list.add(note);
        }
        cursor.close();
        db.close();
        return list;
    }

    // 根据ID查询笔记（仅限当前登录用户自己的笔记）
    public NoteEntity queryNoteById(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String where = NoteDBHelper.COL_ID + "=? AND " + NoteDBHelper.COL_USER_ID + "=?";
        String[] args = new String[]{String.valueOf(id), SPUtil.getCurrentUserId()};
        Cursor cursor = db.query(NoteDBHelper.TABLE_NOTE, null,
                where, args,
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
            note.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_USER_ID)));
        }
        cursor.close();
        db.close();
        return note;
    }

    // 搜索笔记（仅搜索当前用户、未删除的笔记）
    public List<NoteEntity> searchNotes(String key) {
        List<NoteEntity> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String whereClause = NoteDBHelper.COL_IS_DELETED + "=? AND (" +
                NoteDBHelper.COL_TITLE + " LIKE ? OR " + NoteDBHelper.COL_CONTENT + " LIKE ?) "
                + "AND " + NoteDBHelper.COL_USER_ID + "=?";
        String[] whereArgs = new String[]{"0", "%" + key + "%", "%" + key + "%", SPUtil.getCurrentUserId()};

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
            note.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(NoteDBHelper.COL_USER_ID)));
            list.add(note);
        }
        cursor.close();
        db.close();
        return list;
    }
}