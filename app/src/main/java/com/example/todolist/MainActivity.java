package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todolist.ui.activity.NoteListActivity;
import com.example.todolist.ui.activity.TodoListActivity;

// 你需要自己新建这两个页面（先新建，否则会报错）
import com.example.todolist.ui.activity.TaskActivity;   // 待办页面
import com.example.todolist.ui.activity.MineActivity;   // 我的页面

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 笔记
        findViewById(R.id.card_note).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteListActivity.class);
            startActivity(intent);
        });

        // 待办（对应布局 id: card_task）
        findViewById(R.id.card_task).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TodoListActivity.class);
            startActivity(intent);
        });

        // ===================== 新增：点击【待办】卡片 =====================
        findViewById(R.id.card_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                startActivity(intent);
            }
        });

        // ===================== 新增：点击【我的】卡片 =====================
        findViewById(R.id.card_mine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MineActivity.class);
                startActivity(intent);
            }
        });
    }
}