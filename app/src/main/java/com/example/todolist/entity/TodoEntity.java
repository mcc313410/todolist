package com.example.todolist.entity;

public class TodoEntity {
    private int id;
    private String title;       // 任务标题
    private String content;     // 任务详情
    private long createTime;    // 创建时间
    private long deadlineTime;  // 截止时间
    private boolean isCompleted;// 完成状态
    private int priority;       // 优先级：1-低 2-中 3-高
    private int colorRes;        // 标记颜色
    private String tag;          // 标签
    private boolean isTop;      // 是否置顶
    private boolean isArchived;  // 是否归档
    private int repeatType;     // 重复类型：0-不重复 1-每天 2-每周 3-每月

    // 无参构造
    public TodoEntity() {
    }

    // 全参构造（可选，方便批量赋值）
    public TodoEntity(int id, String title, String content, long createTime, long deadlineTime,
                      boolean isCompleted, int priority, int colorRes, String tag,
                      boolean isTop, boolean isArchived, int repeatType) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createTime = createTime;
        this.deadlineTime = deadlineTime;
        this.isCompleted = isCompleted;
        this.priority = priority;
        this.colorRes = colorRes;
        this.tag = tag;
        this.isTop = isTop;
        this.isArchived = isArchived;
        this.repeatType = repeatType;
    }

    public int getColorRes() {
        return colorRes;
    }

    public void setColorRes(int colorRes) {
        this.colorRes = colorRes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getDeadlineTime() {
        return deadlineTime;
    }

    public void setDeadlineTime(long deadlineTime) {
        this.deadlineTime = deadlineTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}