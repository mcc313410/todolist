package com.example.todolist;

import android.app.Application;
import cn.bmob.v3.Bmob;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Application ID初始化
        Bmob.initialize(this, "88f2a2ab271a2ae61df583759bff472a");
    }
}