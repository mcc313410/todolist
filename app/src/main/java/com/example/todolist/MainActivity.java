package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todolist.ui.activity.NoteListActivity;
import com.example.todolist.ui.activity.TodoListActivity;
import com.example.todolist.ui.activity.TaskActivity;
import com.example.todolist.ui.activity.MineActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 笔记按钮
        findViewById(R.id.card_note).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteListActivity.class);
            startActivity(intent);
        });

        // 待办按钮
        findViewById(R.id.card_task).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TodoListActivity.class);
            startActivity(intent);
        });

        // 我的页面
        findViewById(R.id.card_mine).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MineActivity.class);
            startActivity(intent);
        });
    }
}