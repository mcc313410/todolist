package com.example.todolist.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.base.BaseActivity;
import com.example.todolist.db.TodoCompleteDao;
import com.example.todolist.db.TodoQueryDao;
import com.example.todolist.entity.TodoEntity;
import com.example.todolist.ui.adapter.TodoListAdapter;

import java.util.List;

public class TodoListActivity extends BaseActivity {

    private RecyclerView rvTodoList;
    private TodoListAdapter mAdapter;
    private List<TodoEntity> mTodoList;
    private LinearLayout emptyLayout;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        // 申请通知权限
        requestNotifyPermission();

        initView();
        initData();
        initEvent();
        // 绑定搜索监听
        initSearch();
    }

    private void initView() {
        rvTodoList = findViewById(R.id.rv_todo_list);
        emptyLayout = findViewById(R.id.emptyLayout);
        etSearch = findViewById(R.id.etSearch);
        rvTodoList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        TodoCompleteDao.getInstance(this).autoArchiveCompletedTodo();
        mTodoList = TodoQueryDao.getInstance(this).queryAllUnArchiveTodo();
        mAdapter = new TodoListAdapter(this, mTodoList);
        rvTodoList.setAdapter(mAdapter);
        checkEmpty();
    }

    private void initEvent() {
        // 列表项点击 -> 跳转详情
        mAdapter.setOnItemClickListener((position, todo) -> {
            Intent intent = new Intent(TodoListActivity.this, TodoDetailActivity.class);
            intent.putExtra("todo_id", todo.getId());
            startActivity(intent);
        });

        // 复选框切换完成状态
        mAdapter.setOnCheckChangeListener((position, checkBox, todo) -> {
            boolean isChecked = checkBox.isChecked();
            TodoCompleteDao.getInstance(this).switchCompleteStatus(todo.getId(), isChecked);
            refreshData();
        });

        // 归档列表跳转
        findViewById(R.id.tvArchive).setOnClickListener(v -> {
            Intent intent = new Intent(TodoListActivity.this, TodoArchiveActivity.class);
            startActivity(intent);
        });
    }

    // 实时搜索逻辑
    private void initSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 输入变化时执行搜索
                String keyword = s.toString();
                mTodoList = TodoQueryDao.getInstance(TodoListActivity.this).searchUnArchiveTodo(keyword);
                mAdapter.setData(mTodoList);
                mAdapter.notifyDataSetChanged();
                checkEmpty();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // 统一处理空布局显隐
    private void checkEmpty() {
        if (mTodoList.isEmpty()) {
            emptyLayout.setVisibility(View.VISIBLE);
            rvTodoList.setVisibility(View.GONE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            rvTodoList.setVisibility(View.VISIBLE);
        }
    }

    // 新增待办
    public void addTodoClick(View view) {
        Intent intent = new Intent(this, TodoAddActivity.class);
        startActivity(intent);
    }

    // 页面返回刷新
    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
        // 返回页面清空搜索框，恢复全列表
        etSearch.setText("");
    }

    private void refreshData() {
        TodoCompleteDao.getInstance(this).autoArchiveCompletedTodo();
        mTodoList = TodoQueryDao.getInstance(this).queryAllUnArchiveTodo();
        mAdapter.setData(mTodoList);
        mAdapter.notifyDataSetChanged();
        checkEmpty();
    }

    // 通知权限申请
    private void requestNotifyPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }
    }
}