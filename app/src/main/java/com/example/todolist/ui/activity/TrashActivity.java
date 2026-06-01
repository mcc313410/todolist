package com.example.todolist.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todolist.R;
import com.example.todolist.db.TrashBean;
import com.example.todolist.db.TrashDao;
import com.example.todolist.ui.adapter.TrashAdapter;
import java.util.List;

public class TrashActivity extends AppCompatActivity {
    private ListView listView;
    private TrashDao trashDao;
    private List<TrashBean> trashList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        listView = findViewById(R.id.listView);
        trashDao = new TrashDao(this);
        loadTrashList();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            TrashBean bean = trashList.get(position);
            showCustomDialog(bean);
        });

        // ======================= 一键清空回收站（我已经帮你加好了！）
        Button btnClearAll = findViewById(R.id.btn_clear_all);
        btnClearAll.setOnClickListener(v -> {
            new AlertDialog.Builder(TrashActivity.this)
                    .setTitle("确认清空")
                    .setMessage("确定要清空回收站吗？无法恢复！")
                    .setPositiveButton("确定", (d, w) -> {
                        trashDao.clearAllTrash();
                        loadTrashList();
                        Toast.makeText(TrashActivity.this, "已清空", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }

    private void loadTrashList() {
        trashList = trashDao.getAllTrash();
        TrashAdapter adapter = new TrashAdapter(this, trashList);
        listView.setAdapter(adapter);
    }

    private void showCustomDialog(TrashBean bean) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_trash_operation);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvNoteInfo = dialog.findViewById(R.id.tv_note_info);
        Button btnRestore = dialog.findViewById(R.id.btn_restore);
        Button btnDelete = dialog.findViewById(R.id.btn_delete);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        String title = (bean.getTitle() != null && !bean.getTitle().trim().isEmpty())
                ? bean.getTitle().trim()
                : "无标题笔记";
        tvNoteInfo.setText("笔记标题：" + title);

        btnRestore.setOnClickListener(v -> {
            trashDao.restoreNote(bean.getNoteId());
            Toast.makeText(TrashActivity.this, "已恢复：" + title, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            loadTrashList();
        });

        btnDelete.setOnClickListener(v -> {
            trashDao.deleteForever(bean.getNoteId());
            Toast.makeText(TrashActivity.this, "已永久删除：" + title, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            loadTrashList();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}