package com.example.todolist.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist.databinding.ItemNoteListBinding;
import com.example.todolist.entity.NoteEntity;
import com.example.todolist.ui.activity.NoteDetailActivity;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.VH> {
    private final Context context;
    private List<NoteEntity> data;

    public NoteListAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<NoteEntity> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteListBinding binding = ItemNoteListBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        NoteEntity note = data.get(position);
        holder.binding.tvTitle.setText(note.getTitle());
        holder.binding.tvContent.setText(note.getContent());
        holder.binding.tvTime.setText(note.getCreateTime());

        if(note.getIsTop() == 1){
            holder.itemView.setBackgroundColor(0xFFFFFBEB);
        }else{
            holder.itemView.setBackgroundColor(0xFFFFFFFF);
        }

        if(note.getIsCollect() == 1){
            holder.binding.ivCollect.setVisibility(android.view.View.VISIBLE);
            holder.binding.ivCollect.setImageResource(android.R.drawable.btn_star_big_on);
        }else{
            holder.binding.ivCollect.setVisibility(android.view.View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoteDetailActivity.class);
            intent.putExtra("note_id", note.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        ItemNoteListBinding binding;
        public VH(ItemNoteListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}