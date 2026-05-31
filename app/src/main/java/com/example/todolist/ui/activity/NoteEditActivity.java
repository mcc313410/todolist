package com.example.todolist.ui.activity;

import android.os.Bundle;
import android.widget.Toast;
import com.example.todolist.base.BaseActivity;
import com.example.todolist.databinding.ActivityNoteEditBinding;
import com.example.todolist.db.NoteEditDao;
import com.example.todolist.db.NoteQueryDao;
import com.example.todolist.entity.NoteEntity;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class NoteEditActivity extends BaseActivity {
    private ActivityNoteEditBinding binding;
    private NoteEditDao editDao;
    private NoteEntity note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editDao = new NoteEditDao(this);
        long id = getIntent().getLongExtra("note_id", -1);
        note = new NoteQueryDao(this).queryNoteById(id);

        if (note != null) {
            binding.etTitle.setText(note.getTitle());
            binding.etContent.setText(note.getContent());
        }

        binding.ivBack.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> save());
    }

    private void save() {
        String title = binding.etTitle.getText().toString().trim();
        String content = binding.etContent.getText().toString().trim();

        note.setTitle(title);
        note.setContent(content);
        editDao.update(note);

        if (note.isSync() && note.getObjectId() != null) {
            BmobObject obj = new BmobObject("NoteCloud");
            obj.setObjectId(note.getObjectId());
            obj.setValue("title", title);
            obj.setValue("content", content);
            obj.setValue("isTop", note.getIsTop());
            obj.setValue("isCollect", note.getIsCollect());

            obj.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Toast.makeText(NoteEditActivity.this, "保存并同步云端成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();
    }
}