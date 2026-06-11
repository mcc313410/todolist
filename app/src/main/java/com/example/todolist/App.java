package com.example.todolist;
import android.app.Application;

import com.example.todolist.utils.SPUtil;

import cn.bmob.v3.Bmob;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, "88f2a2ab271a2ae61df583759bff472a");
        SPUtil.init(this); // 初始化SP
    }

}