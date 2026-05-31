package com.example.todolist.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist.databinding.ItemNoteListBinding;
import com.example.todolist.entity.NoteEntity;
import com.example.todolist.ui.activity.NoteDetailActivity;
import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {

    private List<NoteEntity> data = new ArrayList<>();
    private Context context;

    public NoteListAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<NoteEntity> list) {
        this.data = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteListBinding binding = ItemNoteListBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NoteEntity note = data.get(position);
        holder.binding.tvTitle.setText(note.getTitle());
        holder.binding.tvContent.setText(note.getContent());
        holder.binding.tvTime.setText(note.getCreateTime());

        if (note.getIsCollect() == 1) {
            holder.binding.ivCollect.setVisibility(View.VISIBLE);
        } else {
            holder.binding.ivCollect.setVisibility(View.GONE);
        }

        if (note.getIsTop() == 1) {
            holder.binding.tvTop.setVisibility(View.VISIBLE);
            holder.binding.content.setBackgroundColor(0xFFE8F0FE);
        } else {
            holder.binding.tvTop.setVisibility(View.GONE);
            holder.binding.content.setBackgroundColor(0xFFFFFFFF);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoteDetailActivity.class);
            intent.putExtra("note_id", note.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        public ItemNoteListBinding binding;
        public NoteViewHolder(ItemNoteListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}