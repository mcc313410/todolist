package com.example.todolist.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class TrashDao {
    private final NoteDBHelper helper;

    public TrashDao(Context context) {
        helper = new NoteDBHelper(context);
    }

    public List<TrashBean> getAllTrash() {
        List<TrashBean> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "SELECT t._id, t.note_id, t.delete_time, n.title, n.content " +
                "FROM trash t LEFT JOIN note n ON t.note_id = n._id";

        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            TrashBean b = new TrashBean();
            b.setTrashId(c.getLong(0));
            b.setNoteId(c.getLong(1));
            b.setDeleteTime(c.getString(2));
            b.setTitle(c.getString(3));
            b.setContent(c.getString(4));
            list.add(b);
        }
        c.close();
        db.close();
        return list;
    }

    public void restoreNote(long noteId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("trash", "note_id=?", new String[]{String.valueOf(noteId)});
        db.close();
    }

    public void deleteForever(long noteId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("note", "_id=?", new String[]{String.valueOf(noteId)});
        db.delete("trash", "note_id=?", new String[]{String.valueOf(noteId)});
        db.close();
    }
}