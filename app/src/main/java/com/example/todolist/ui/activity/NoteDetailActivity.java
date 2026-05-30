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
    private NoteDeleteDao deleteDao;
    private NoteEntity note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        queryDao = new NoteQueryDao(this);
        editDao = new NoteEditDao(this);
        deleteDao = new NoteDeleteDao(this);

        long id = getIntent().getLongExtra("note_id", -1);
        if (id == -1) {
            finish();
            return;
        }
        note = queryDao.queryNoteById(id);
        if (note == null) {
            finish();
            return;
        }

        binding.ivBack.setOnClickListener(v -> finish());
        showData();
        refreshBtn();

        // 编辑笔记
        binding.btnDetailEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, NoteEditActivity.class);
            intent.putExtra("note_id", note.getId());
            startActivity(intent);
        });

        // 置顶/取消置顶
        binding.btnDetailTop.setOnClickListener(v -> {
            note.setIsTop(note.getIsTop() == 1 ? 0 : 1);
            editDao.updateNote(note);
            Toast.makeText(this, note.getIsTop() == 1 ? "已置顶" : "已取消置顶", Toast.LENGTH_SHORT).show();
            refreshBtn();
        });

        // 收藏/取消收藏
        binding.btnDetailCollect.setOnClickListener(v -> {
            note.setIsCollect(note.getIsCollect() == 1 ? 0 : 1);
            editDao.updateNote(note);
            Toast.makeText(this, note.getIsCollect() == 1 ? "已收藏" : "已取消收藏", Toast.LENGTH_SHORT).show();
            refreshBtn();
        });

        // 同步云端（防止重复上传）
        binding.btnDetailUpload.setOnClickListener(v -> {
            if (note.isSync()) {
                Toast.makeText(this, "该笔记已同步，无需重复操作", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadToBmob();
        });

        // 删除笔记（本地 + 云端联动删除）
        binding.btnDetailDelete.setOnClickListener(v -> {
            // 先删除本地数据
            deleteDao.deleteNote(note.getId());

            // 存在云端ID则同步删除云端
            if (note.getObjectId() != null && !note.getObjectId().isEmpty()) {
                BmobObject obj = new BmobObject("NoteCloud");
                obj.setObjectId(note.getObjectId());
                obj.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(NoteDetailActivity.this, "本地+云端删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(NoteDetailActivity.this, "本地已删除，云端删除失败", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                });
            } else {
                Toast.makeText(NoteDetailActivity.this, "本地删除成功（无云端数据）", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // 上传数据到Bmob云端
    private void uploadToBmob() {
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
                    // 同步成功：保存云端ID + 标记为已同步
                    note.setObjectId(objectId);
                    note.setSync(true);
                    editDao.updateNote(note);
                    Toast.makeText(NoteDetailActivity.this, "同步云端成功", Toast.LENGTH_SHORT).show();
                    showData();
                } else {
                    Toast.makeText(NoteDetailActivity.this, "同步失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showData() {
        binding.tvDetailTitle.setText(note.getTitle());
        binding.tvDetailContent.setText(note.getContent());
        binding.tvDetailTime.setText(note.getCreateTime());
        // 根据同步状态改变按钮文字
        binding.btnDetailUpload.setText(note.isSync() ? "已同步" : "同步到云端");
    }

    private void refreshBtn() {
        binding.btnDetailTop.setText(note.getIsTop() == 1 ? "取消置顶" : "置顶");
        binding.btnDetailCollect.setText(note.getIsCollect() == 1 ? "取消收藏" : "收藏");
    }

    @Override
    protected void onResume() {
        super.onResume();
        note = queryDao.queryNoteById(note.getId());
        if (note != null) {
            showData();
            refreshBtn();
        }
    }
}