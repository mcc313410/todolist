package com.example.todolist.db;

public class TrashBean {
    private long trashId;
    private long noteId;
    private String title;
    private String content;
    private String deleteTime;

    public long getTrashId() { return trashId; }
    public void setTrashId(long trashId) { this.trashId = trashId; }

    public long getNoteId() { return noteId; }
    public void setNoteId(long noteId) { this.noteId = noteId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getDeleteTime() { return deleteTime; }
    public void setDeleteTime(String deleteTime) { this.deleteTime = deleteTime; }
}