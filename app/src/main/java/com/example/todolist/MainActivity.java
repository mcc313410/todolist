package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todolist.ui.activity.NoteListActivity;
import com.example.todolist.ui.activity.TodoListActivity;

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
    }
}