package com.example.todolist.entity;

public class NoteEntity {
    private long id;             // 本地ID
    private String title;       // 标题
    private String content;     // 内容
    private String createTime;  // 创建时间
    private int isCollect = 0;  // 是否收藏：0-未收藏，1-收藏（默认未收藏）
    private int isTop = 0;      // 是否置顶：0-未置顶，1-置顶（默认未置顶）
    private String objectId;    // Bmob云端ID
    private boolean isSync = false; // 是否同步到云端（默认未同步）

    // Getter & Setter
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public int getIsCollect() { return isCollect; }
    public void setIsCollect(int collect) { this.isCollect = collect; }

    public int getIsTop() { return isTop; }
    public void setIsTop(int top) { this.isTop = top; }

    public String getObjectId() { return objectId; }
    public void setObjectId(String objectId) { this.objectId = objectId; }

    public boolean isSync() { return isSync; }
    public void setSync(boolean sync) { this.isSync = sync; }
}