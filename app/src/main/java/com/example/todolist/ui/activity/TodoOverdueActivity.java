package com.example.todolist.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.todolist.R;
import com.example.todolist.db.TodoQueryDao;
import com.example.todolist.entity.TodoEntity;
import com.example.todolist.utils.DateUtil;
import java.util.List;

public class TodoOverdueActivity extends AppCompatActivity {

    private TextView tvContent;
    private TodoQueryDao todoQueryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_overdue);

        tvContent = findViewById(R.id.tv_content);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        todoQueryDao = TodoQueryDao.getInstance(this);
        loadOverdueData();
    }

    // 加载逾期数据，拼接文本展示
    private void loadOverdueData() {
        long todayZero = DateUtil.getTodayZeroMillis();
        List<TodoEntity> overdueList = todoQueryDao.getAllOverdueTodo(todayZero);
        if (overdueList.isEmpty()) {
            tvContent.setText("暂无逾期任务");
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("共有").append(overdueList.size()).append("条逾期任务：\n\n");
            // 循环拼接每条任务信息
            for (TodoEntity todo : overdueList) {
                stringBuilder.append("标题：").append(todo.getTitle()).append("\n");
                stringBuilder.append("标签：").append(todo.getTag()).append("\n");
                stringBuilder.append("------------------------\n");
            }
            tvContent.setText(stringBuilder.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOverdueData();
    }
}