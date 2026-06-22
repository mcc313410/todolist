package com.example.todolist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.example.todolist.entity.NoteEntity;
import com.example.todolist.utils.DateUtil;
import com.example.todolist.utils.SPUtil;

public class NoteAddDao {
    private final NoteDBHelper helper;
    public NoteAddDao(Context context) {
        helper = new NoteDBHelper(context);
    }

    public long insert(NoteEntity note) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NoteDBHelper.COL_TITLE, note.getTitle());
        cv.put(NoteDBHelper.COL_CONTENT, note.getContent());
        cv.put(NoteDBHelper.COL_CREATE_TIME, DateUtil.getCurrentTime());
        cv.put(NoteDBHelper.COL_IS_TOP, note.getIsTop());
        cv.put(NoteDBHelper.COL_IS_COLLECT, note.getIsCollect());
        cv.put(NoteDBHelper.COL_OBJECT_ID, note.getObjectId());
        cv.put(NoteDBHelper.COL_IS_SYNC, note.isSync() ? 1 : 0);
        // 绑定当前登录用户ID
        cv.put(NoteDBHelper.COL_USER_ID, SPUtil.getCurrentUserId());

        long id = db.insert(NoteDBHelper.TABLE_NOTE, null, cv);
        db.close();
        return id;
    }
}