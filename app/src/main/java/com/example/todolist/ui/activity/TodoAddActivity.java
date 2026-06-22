package com.example.todolist.ui.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolist.base.BaseActivity;
import com.example.todolist.db.TodoAddDao;
import com.example.todolist.db.TodoEditDao;
import com.example.todolist.db.TodoQueryDao;
import com.example.todolist.entity.TodoEntity;
import com.example.todolist.R;
import com.example.todolist.utils.AlarmUtil;
import com.example.todolist.utils.SPUtil; // 新增导入用户工具类

import java.util.Calendar;

public class TodoAddActivity extends BaseActivity {

    // 原有控件
    private EditText etTitle, etContent;
    private TextView tvRemindTime;
    private Spinner spRepeatType;

    // ===== 新增控件 =====
    private Spinner spPriority;
    private EditText etTag;
    private Switch swTop;

    private int mTodoId = -1; // -1 = 新增  大于0 = 编辑
    private TodoEntity mEditTodo;

    // 时间变量
    private long mDeadlineTime = 0;
    private final Calendar mCalendar = Calendar.getInstance();
    private DatePickerDialog mDatePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_add);

        etTitle = findViewById(R.id.et_todo_title);
        etContent = findViewById(R.id.et_todo_content);
        tvRemindTime = findViewById(R.id.tv_remind_time);
        spRepeatType = findViewById(R.id.sp_repeat_type);

        // ===== 新增绑定 =====
        spPriority = findViewById(R.id.sp_priority);
        etTag = findViewById(R.id.et_tag);
        swTop = findViewById(R.id.sw_top);
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        // 接收编辑ID
        mTodoId = getIntent().getIntExtra("todo_id", -1);
        if (mTodoId != -1) {
            // 编辑模式：回显数据
            setTitle("编辑任务");
            mEditTodo = TodoQueryDao.getInstance(this).queryTodoById(mTodoId);
            if (mEditTodo != null) {
                etTitle.setText(mEditTodo.getTitle());
                etContent.setText(mEditTodo.getContent());
                // 回显时间 & 重复类型
                mDeadlineTime = mEditTodo.getDeadlineTime();
                spRepeatType.setSelection(mEditTodo.getRepeatType());
                if (mDeadlineTime > 0) {
                    mCalendar.setTimeInMillis(mDeadlineTime);
                    tvRemindTime.setText(formatTime(mCalendar));
                }
                // ===== 新增：回显 优先级、标签、置顶 =====
                spPriority.setSelection(mEditTodo.getPriority());
                etTag.setText(mEditTodo.getTag());
                swTop.setChecked(mEditTodo.isTop());
            }
        } else {
            setTitle("新增任务");
        }

        // ========== 新增这一行：绑定点击事件 ==========
        tvRemindTime.setOnClickListener(v -> selectRemindTime(v));

        // 请求必要的权限（通知和精确闹钟）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                // 引导用户去设置中开启精确闹钟权限
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }

    }



    public void selectRemindTime(View view) {
        Toast.makeText(this, "点击了", Toast.LENGTH_SHORT).show();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        Calendar tempCal = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                android.R.style.Theme_DeviceDefault_Dialog,
                (dp, year, month, day) -> {
                    tempCal.set(year, month, day);
                    // 时间选择器同样使用系统默认主题
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            android.R.style.Theme_DeviceDefault_Dialog,
                            (tp, hour, minute) -> {
                                tempCal.set(Calendar.HOUR_OF_DAY, hour);
                                tempCal.set(Calendar.MINUTE, minute);
                                tempCal.set(Calendar.SECOND, 0);
                                mDeadlineTime = tempCal.getTimeInMillis();
                                mCalendar.setTimeInMillis(mDeadlineTime);
                                tvRemindTime.setText(formatTime(mCalendar));
                                Toast.makeText(this, "提醒时间已设置", Toast.LENGTH_SHORT).show();
                            },
                            tempCal.get(Calendar.HOUR_OF_DAY),
                            tempCal.get(Calendar.MINUTE),
                            true);
                    timePickerDialog.show();
                },
                tempCal.get(Calendar.YEAR),
                tempCal.get(Calendar.MONTH),
                tempCal.get(Calendar.DAY_OF_MONTH)
        );

        view.postDelayed(() -> {
            try {
                datePickerDialog.show();
                Log.e("TODO_DEBUG", "show() 执行成功");
            } catch (Exception e) {
                Log.e("TODO_DEBUG", "show() 出错", e);
            }
        }, 300);
    }

    // 时间格式化
    private String formatTime(Calendar cal) {
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DAY_OF_MONTH);
        int h = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        return y + "-" + m + "-" + d + " " + h + ":" + String.format("%02d", min);
    }

    // 保存按钮
    public void saveTodoClick(View view) {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        int repeatType = spRepeatType.getSelectedItemPosition();

        // ===== 新增取值 =====
        int priority = spPriority.getSelectedItemPosition();
        String tag = etTag.getText().toString().trim();
        boolean isTop = swTop.isChecked();

        if (title.isEmpty()) {
            Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mTodoId == -1) {
            // 新增任务【核心修复：绑定当前登录用户ID】
            TodoEntity todo = new TodoEntity();
            todo.setTitle(title);
            todo.setContent(content);
            todo.setCreateTime(System.currentTimeMillis());
            todo.setDeadlineTime(mDeadlineTime);
            todo.setRepeatType(repeatType);
            todo.setCompleted(false);
            todo.setArchived(false);
            todo.setTop(isTop);
            todo.setPriority(priority);
            todo.setTag(tag);
            // 关键代码：绑定登录用户唯一ID
            todo.setUserId(SPUtil.getCurrentUserId());

            long newId = TodoAddDao.getInstance(this).addTodo(todo);
            TodoEntity newTodo = TodoQueryDao.getInstance(this).queryTodoById((int) newId);
            // 新增：设置闹钟
            AlarmUtil.setAlarm(this, newTodo);

        } else {
            // 编辑任务：无需重新设置userId，数据库原有数据自带
            AlarmUtil.cancelAlarm(this, mTodoId);

            mEditTodo.setTitle(title);
            mEditTodo.setContent(content);
            mEditTodo.setDeadlineTime(mDeadlineTime);
            mEditTodo.setRepeatType(repeatType);
            mEditTodo.setTop(isTop);
            mEditTodo.setPriority(priority);
            mEditTodo.setTag(tag);
            TodoEditDao.getInstance(this).updateTodo(mEditTodo);

            // 新增：重新设置新闹钟
            AlarmUtil.setAlarm(this, mEditTodo);
        }

        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    // 取消按钮
    public void cancelClick(View view) {
        finish();
    }
}