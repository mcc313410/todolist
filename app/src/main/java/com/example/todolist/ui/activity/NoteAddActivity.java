package com.example.todolist.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.todolist.base.BaseActivity;
import com.example.todolist.databinding.ActivityNoteAddBinding;
import com.example.todolist.db.NoteEditDao;
import com.example.todolist.entity.NoteEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteAddActivity extends BaseActivity {

    private ActivityNoteAddBinding binding;
    private NoteEditDao editDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editDao = new NoteEditDao(this);

        // ✅ 返回箭头：空不填空都能直接退出
        binding.ivBack.setOnClickListener(v -> finish());

        // 保存笔记
        binding.btnSave.setOnClickListener(v -> saveNote());
    }

    private void saveNote() {
        String title = binding.etTitle.getText().toString().trim();
        String content = binding.etContent.getText().toString().trim();

        // 你原来的逻辑：可以保留“标题非空”校验，也可以去掉
        if (title.isEmpty()) {
            Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 当前时间
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        NoteEntity note = new NoteEntity();
        note.setTitle(title);
        note.setContent(content);
        note.setCreateTime(time);
        note.setIsTop(0);
        note.setIsCollect(0);
        note.setObjectId("");
        note.setSync(false);

        editDao.insertNote(note);
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        finish();
    }
}