package com.example.todolist.entity;

import cn.bmob.v3.BmobUser;

public class UserBean extends BmobUser {
    private String nickname;
    private String gender;
    private String avatarUrl; // 可选：存头像URL到Bmob

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}