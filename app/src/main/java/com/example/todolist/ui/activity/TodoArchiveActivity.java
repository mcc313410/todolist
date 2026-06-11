package com.example.todolist.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.base.BaseActivity;
import com.example.todolist.db.TodoDeleteDao;
import com.example.todolist.db.TodoQueryDao;
import com.example.todolist.entity.TodoEntity;
import com.example.todolist.ui.adapter.TodoArchiveAdapter;

import java.util.List;

public class TodoArchiveActivity extends BaseActivity {

    private RecyclerView rvArchive;
    private TodoArchiveAdapter mAdapter;
    private List<TodoEntity> mArchiveList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_archive);

        rvArchive = findViewById(R.id.rv_archive_list);
        rvArchive.setLayoutManager(new LinearLayoutManager(this));

        loadArchiveData();
    }

    // 加载所有已归档任务
    private void loadArchiveData() {
        mArchiveList = TodoQueryDao.getInstance(this).queryAllArchiveTodo();
        mAdapter = new TodoArchiveAdapter(this, mArchiveList);
        rvArchive.setAdapter(mAdapter);
    }

    // 一键清空归档
    public void clearAllArchive(View view) {
        TodoDeleteDao.getInstance(this).deleteAllArchiveTodo();
        Toast.makeText(this, "归档已清空", Toast.LENGTH_SHORT).show();
        loadArchiveData();
    }
}