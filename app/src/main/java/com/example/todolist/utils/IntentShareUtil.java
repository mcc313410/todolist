package com.example.todolist.utils;

import android.content.Context;
import android.content.Intent;

import com.example.todolist.entity.TodoEntity;

public class IntentShareUtil {
    public static void shareTodo(Context context, TodoEntity todo) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareContent = "待办任务：" + todo.getTitle() + "\n详情：" + todo.getContent();
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        context.startActivity(Intent.createChooser(shareIntent, "分享任务到"));
    }
}
