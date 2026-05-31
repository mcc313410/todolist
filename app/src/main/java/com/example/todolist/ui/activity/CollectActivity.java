package com.example.todolist.ui.activity;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.todolist.databinding.ActivityCollectBinding;
import com.example.todolist.db.NoteQueryDao;
import com.example.todolist.entity.NoteEntity;
import com.example.todolist.ui.adapter.NoteListAdapter;
import java.util.ArrayList;
import java.util.List;

public class CollectActivity extends AppCompatActivity {

    private ActivityCollectBinding binding;
    private NoteListAdapter adapter;
    private NoteQueryDao noteQueryDao;
    private List<NoteEntity> collectNotes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivBack.setOnClickListener(v -> finish());
        noteQueryDao = new NoteQueryDao(this);
        adapter = new NoteListAdapter(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCollectNotes();
    }

    private void loadCollectNotes() {
        collectNotes.clear();
        List<NoteEntity> allNotes = noteQueryDao.queryAllNotes();
        for (NoteEntity note : allNotes) {
            if (note.getIsCollect() == 1) {
                collectNotes.add(note);
            }
        }
        adapter.setData(collectNotes);

        if (collectNotes.isEmpty()) {
            binding.emptyCollect.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        } else {
            binding.emptyCollect.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        }
    }
}