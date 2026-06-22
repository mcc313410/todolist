package com.example.todolist.ui.activity;

import android.os.Bundle;
import android.widget.Toast;
import com.example.todolist.base.BaseActivity;
import com.example.todolist.databinding.ActivityNoteAddBinding;
import com.example.todolist.db.NoteAddDao;
import com.example.todolist.entity.NoteEntity;
import com.example.todolist.utils.SPUtil; // 新增用户工具类导入
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteAddActivity extends BaseActivity {

    private ActivityNoteAddBinding binding;
    private NoteAddDao addDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        addDao = new NoteAddDao(this);

        binding.ivBack.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> saveNote());
    }

    private void saveNote() {
        String title = binding.etTitle.getText().toString().trim();
        String content = binding.etContent.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (content.isEmpty()) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        NoteEntity note = new NoteEntity();
        note.setTitle(title);
        note.setContent(content);
        note.setCreateTime(getCurrentTime());
        note.setIsTop(0);
        note.setIsCollect(0);
        note.setSync(false);
        // 核心修复：绑定当前登录用户ID
        note.setUserId(SPUtil.getCurrentUserId());

        long id = addDao.insert(note);
        if (id > 0) {
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}