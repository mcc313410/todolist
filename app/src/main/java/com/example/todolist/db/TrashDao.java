package com.example.todolist.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.todolist.entity.TrashBean;

import java.util.ArrayList;
import java.util.List;

public class TrashDao {
    private final NoteDBHelper helper;
    private final Context context;

    public TrashDao(Context context) {
        this.context = context;
        helper = new NoteDBHelper(context);
    }

    // 查询所有回收站笔记（关联note表，仅查is_deleted=1的记录）
    public List<TrashBean> getAllTrash() {
        List<TrashBean> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "SELECT t._id, t.note_id, t.delete_time, n.title, n.content, n.create_time, n.is_top, n.is_collect " +
                "FROM " + NoteDBHelper.TABLE_TRASH + " t " +
                "LEFT JOIN " + NoteDBHelper.TABLE_NOTE + " n ON t.note_id = n._id " +
                "WHERE n." + NoteDBHelper.COL_IS_DELETED + " = 1";

        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            TrashBean bean = new TrashBean();
            bean.setTrashId(cursor.getLong(0));
            bean.setNoteId(cursor.getLong(1));
            bean.setDeleteTime(cursor.getString(2));
            bean.setTitle(cursor.getString(3));
            bean.setContent(cursor.getString(4));
            bean.setCreateTime(cursor.getString(5));
            bean.setIsTop(cursor.getInt(6));
            bean.setIsCollect(cursor.getInt(7));
            list.add(bean);
        }
        cursor.close();
        db.close();
        return list;
    }

    // 恢复笔记：调用NoteDeleteDao的方法
    public void restoreNote(long noteId) {
        // 直接用保存的context
        new NoteDeleteDao(context).restoreNote(noteId);
    }

    // 永久删除笔记
    public void deleteForever(long noteId) {
        new NoteDeleteDao(context).deleteForever(noteId);
    }

    // 一键清空回收站
    public void clearAllTrash() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(NoteDBHelper.TABLE_TRASH, null, null);
        db.close();
    }
}