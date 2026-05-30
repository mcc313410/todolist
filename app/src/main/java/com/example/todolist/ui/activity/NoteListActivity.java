package com.example.todolist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.todolist.databinding.ActivityNoteListBinding;
import com.example.todolist.db.NoteQueryDao;
import com.example.todolist.entity.NoteEntity;
import com.example.todolist.ui.adapter.NoteListAdapter;
import java.util.ArrayList;
import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    private ActivityNoteListBinding binding;
    private NoteListAdapter adapter;
    private NoteQueryDao noteQueryDao;
    private List<NoteEntity> allNotes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        noteQueryDao = new NoteQueryDao(this);
        initRecyclerView();
        initSearch();
        binding.btnAdd.setOnClickListener(v -> startActivity(new Intent(this, NoteAddActivity.class)));
    }

    private void initRecyclerView() {
        adapter = new NoteListAdapter(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void initSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterNotes(String keyword) {
        List<NoteEntity> filtered = new ArrayList<>();
        for (NoteEntity note : allNotes) {
            if (note.getTitle().contains(keyword) || note.getContent().contains(keyword)) {
                filtered.add(note);
            }
        }
        adapter.setData(filtered);
        updateEmptyState(filtered.size());
    }

    private void updateEmptyState(int count) {
        if (count == 0) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.emptyLayout.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.emptyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        allNotes = noteQueryDao.queryAllNotes();
        filterNotes(binding.etSearch.getText().toString());
    }
}