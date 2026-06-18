package com.example.todolist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.example.todolist.R;
import com.example.todolist.base.BaseActivity;
import com.example.todolist.db.TodoCompleteDao;
import com.example.todolist.db.TodoDeleteDao;
import com.example.todolist.db.TodoEditDao;
import com.example.todolist.db.TodoQueryDao;
import com.example.todolist.entity.TodoEntity;
import com.example.todolist.utils.AlarmUtil;

public class TodoDetailActivity extends BaseActivity {

    private TextView tvTitle, tvContent;
    private int mTodoId;
    private TodoEntity mTodo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);

        tvTitle = findViewById(R.id.tv_detail_title);
        tvContent = findViewById(R.id.tv_detail_content);
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        mTodoId = getIntent().getIntExtra("todo_id", -1);
        loadTodoData();
    }

    private void loadTodoData() {
        if (mTodoId == -1) {
            Toast.makeText(this, "任务不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mTodo = TodoQueryDao.getInstance(this).queryTodoById(mTodoId);
        if (mTodo == null) {
            Toast.makeText(this, "任务已删除", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        tvTitle.setText(mTodo.getTitle());
        tvContent.setText(mTodo.getContent());
    }

    // 编辑按钮 -> 跳转到编辑页（复用Add页面）
    public void editTodoClick(View view) {
        Intent intent = new Intent(this, TodoAddActivity.class);
        intent.putExtra("todo_id", mTodoId);
        startActivity(intent);
    }

    // 删除按钮
    public void deleteTodoClick(View view) {
        // 先取消对应闹钟
        AlarmUtil.cancelAlarm(this, mTodoId);
        // 再删除数据
        TodoDeleteDao.getInstance(this).deleteTodoById(mTodoId);
        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    // 标记为已完成
    public void completeTodoClick(View view) {
        TodoCompleteDao.getInstance(this).switchCompleteStatus(mTodoId, true);
        Toast.makeText(this, "已标记完成", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTodoData();
    }

    // 切换置顶状态
    public void switchTopClick(View view) {
        boolean currentTop = mTodo.isTop();
        mTodo.setTop(!currentTop);
        TodoEditDao.getInstance(this).updateTodo(mTodo);
        Toast.makeText(this, currentTop ? "已取消置顶" : "已置顶", Toast.LENGTH_SHORT).show();
        finish();
    }

    // 系统分享
    public void shareClick(View view) {
        String content = "任务标题：" + mTodo.getTitle() + "\n任务内容：" + mTodo.getContent();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        startActivity(Intent.createChooser(shareIntent, "分享任务"));
    }

    // 归档任务
    public void archiveClick(View view) {
        mTodo.setArchived(true);
        TodoEditDao.getInstance(this).updateTodo(mTodo);
        Toast.makeText(this, "任务已归档", Toast.LENGTH_SHORT).show();
        finish();
    }
}