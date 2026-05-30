package com.example.todolist.ui.activity;

import android.os.Bundle;
import android.widget.Toast;
import com.example.todolist.base.BaseActivity;
import com.example.todolist.databinding.ActivityNoteEditBinding;
import com.example.todolist.db.NoteEditDao;
import com.example.todolist.db.NoteQueryDao;
import com.example.todolist.entity.NoteEntity;

public class NoteEditActivity extends BaseActivity {
    private ActivityNoteEditBinding binding;
    private NoteEditDao editDao;
    private NoteQueryDao queryDao;
    private NoteEntity note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        editDao = new NoteEditDao(this);
        queryDao = new NoteQueryDao(this);

        long id = getIntent().getLongExtra("note_id", -1);
        if (id == -1) { finish(); return; }
        note = queryDao.queryNoteById(id);
        if (note == null) { finish(); return; }

        binding.etTitle.setText(note.getTitle());
        binding.etContent.setText(note.getContent());
        binding.btnSave.setOnClickListener(v -> update());
    }

    private void update() {
        String title = binding.etTitle.getText().toString().trim();
        String content = binding.etContent.getText().toString().trim();
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "标题和内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        note.setTitle(title);
        note.setContent(content);
        editDao.updateNote(note);
        Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
        finish();
    }
}