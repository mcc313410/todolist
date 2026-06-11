package com.example.todolist.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {
    private static final String SP_NAME = "user_info";
    private static SharedPreferences sp;

    // 初始化
    public static void init(Context context) {
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    // 保存用户登录信息
    public static void saveUser(String username, String password, boolean isLogin) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putBoolean("is_login", isLogin);
        editor.apply();
    }

    // 获取保存的用户名
    public static String getUsername() {
        return sp.getString("username", "");
    }

    // 获取保存的密码
    public static String getPassword() {
        return sp.getString("password", "");
    }

    // 获取登录状态
    public static boolean isLogin() {
        return sp.getBoolean("is_login", false);
    }

    // 清空用户信息（退出登录）
    public static void clearUser() {
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
    // 保存头像本地路径
    // 保存头像本地路径
    public static void saveAvatarPath(String path) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("avatar_path", path);
        editor.apply();
    }

    // 获取头像本地路径
    public static String getAvatarPath() {
        return sp.getString("avatar_path", "");
    }

}