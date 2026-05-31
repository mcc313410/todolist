package com.example.todolist.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.example.todolist.R;
import com.example.todolist.base.BaseActivity;
import com.example.todolist.db.TrashBean;
import com.example.todolist.db.TrashDao;
import java.util.List;

public class TrashActivity extends BaseActivity {
    private ListView listView;
    private TrashDao trashDao;
    private List<TrashBean> trashList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);
        listView = findViewById(R.id.listView);
        trashDao = new TrashDao(this);
        loadTrash();

        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            TrashBean bean = trashList.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("笔记操作")
                    .setMessage("请选择：恢复 或 永久删除")
                    .setPositiveButton("恢复", (d, w) -> {
                        trashDao.restoreNote(bean.getNoteId());
                        Toast.makeText(this, "已恢复到笔记列表", Toast.LENGTH_SHORT).show();
                        loadTrash();
                    })
                    .setNegativeButton("永久删除", (d, w) -> {
                        trashDao.deleteForever(bean.getNoteId());
                        Toast.makeText(this, "已永久删除", Toast.LENGTH_SHORT).show();
                        loadTrash();
                    })
                    .show();
        });
    }

    private void loadTrash() {
        trashList = trashDao.getAllTrash();
        ArrayAdapter<TrashBean> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                trashList) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);
                TrashBean bean = getItem(position);
                text1.setText(bean.getTitle() != null ? bean.getTitle() : "无标题");
                text2.setText("删除时间：" + bean.getDeleteTime());
                return view;
            }
        };
        listView.setAdapter(adapter);
    }
}