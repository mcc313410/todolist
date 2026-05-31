package com.example.todolist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.example.todolist.base.BaseActivity;
import com.example.todolist.databinding.ActivityNoteDetailBinding;
import com.example.todolist.db.NoteDeleteDao;
import com.example.todolist.db.NoteEditDao;
import com.example.todolist.db.NoteQueryDao;
import com.example.todolist.entity.NoteEntity;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class NoteDetailActivity extends BaseActivity {
    private ActivityNoteDetailBinding binding;
    private NoteQueryDao queryDao;
    private NoteEditDao editDao;
    private NoteEntity note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        queryDao = new NoteQueryDao(this);
        editDao = new NoteEditDao(this);

        long id = getIntent().getLongExtra("note_id", -1);
        note = queryDao.queryNoteById(id);
        if (note == null) { finish(); return; }

        binding.ivBack.setOnClickListener(v -> finish());
        showData();
        refreshUI();

        binding.btnDetailEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, NoteEditActivity.class);
            intent.putExtra("note_id", note.getId());
            startActivityForResult(intent, 100);
        });

        binding.btnDetailTop.setOnClickListener(v -> {
            note.setIsTop(note.getIsTop() == 1 ? 0 : 1);
            editDao.update(note);
            Toast.makeText(this, note.getIsTop() == 1 ? "已置顶" : "取消置顶", Toast.LENGTH_SHORT).show();
            refreshUI();
        });

        binding.btnDetailCollect.setOnClickListener(v -> {
            note.setIsCollect(note.getIsCollect() == 1 ? 0 : 1);
            editDao.update(note);
            refreshUI();
            Toast.makeText(this, note.getIsCollect() == 1 ? "已收藏" : "取消收藏", Toast.LENGTH_SHORT).show();
        });

        binding.btnDetailUpload.setOnClickListener(v -> {
            if (note.isSync()) {
                Toast.makeText(this, "已同步", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadToCloud();
        });

        binding.btnDetailDelete.setOnClickListener(v -> {
            deleteNoteWithCloud();
        });
    }

    private void uploadToCloud() {
        BmobObject obj = new BmobObject("NoteCloud");
        obj.setValue("title", note.getTitle());
        obj.setValue("content", note.getContent());
        obj.setValue("createTime", note.getCreateTime());
        obj.setValue("isTop", note.getIsTop());
        obj.setValue("isCollect", note.getIsCollect());
        obj.setValue("localId", note.getId());

        obj.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    note.setObjectId(objectId);
                    note.setSync(true);
                    editDao.update(note);
                    Toast.makeText(NoteDetailActivity.this, "同步成功", Toast.LENGTH_SHORT).show();
                    showData();
                } else {
                    Toast.makeText(NoteDetailActivity.this, "同步失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteNoteWithCloud() {
        NoteDeleteDao deleteDao = new NoteDeleteDao(NoteDetailActivity.this);
        deleteDao.deleteToTrash(note.getId());

        if (note.isSync() && note.getObjectId() != null) {
            BmobObject obj = new BmobObject("NoteCloud");
            obj.setObjectId(note.getObjectId());
            obj.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Toast.makeText(NoteDetailActivity.this, "本地+云端已删除", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(NoteDetailActivity.this, "已移入回收站", Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();
    }

    private void showData() {
        binding.tvDetailTitle.setText(note.getTitle());
        binding.tvDetailContent.setText(note.getContent());
        binding.tvDetailTime.setText(note.getCreateTime());
        binding.btnDetailUpload.setText(note.isSync() ? "已同步" : "同步云端");
    }

    private void refreshUI() {
        binding.btnDetailTop.setText(note.getIsTop() == 1 ? "取消置顶" : "置顶");
        binding.btnDetailCollect.setText(note.getIsCollect() == 1 ? "取消收藏" : "收藏");
        binding.ivCollectIcon.setVisibility(note.getIsCollect() == 1 ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            note = queryDao.queryNoteById(note.getId());
            showData();
            refreshUI();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        note = queryDao.queryNoteById(note.getId());
        if (note != null) {
            showData();
            refreshUI();
        }
    }
}