package com.example.todolist.entity;

public class NoteEntity {
    private long id;
    private String title;
    private String content;
    private String createTime;
    private int isCollect;
    private int isTop;
    private String objectId;
    private boolean isSync;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public int getIsCollect() { return isCollect; }
    public void setIsCollect(int collect) { isCollect = collect; }

    public int getIsTop() { return isTop; }
    public void setIsTop(int top) { isTop = top; }

    public String getObjectId() { return objectId; }
    public void setObjectId(String objectId) { this.objectId = objectId; }

    public boolean isSync() { return isSync; }
    public void setSync(boolean sync) { isSync = sync; }
}