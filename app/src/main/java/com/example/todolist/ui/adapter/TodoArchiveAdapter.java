package com.example.todolist.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist.R;
import com.example.todolist.entity.TodoEntity;
import java.util.ArrayList;
import java.util.List;

public class TodoArchiveAdapter extends RecyclerView.Adapter<TodoArchiveAdapter.ArchiveViewHolder> {

    private final Context mContext;
    private List<TodoEntity> mList;

    public TodoArchiveAdapter(Context context, List<TodoEntity> list) {
        this.mContext = context;
        this.mList = sortTopFirst(list);
    }

    private List<TodoEntity> sortTopFirst(List<TodoEntity> list) {
        List<TodoEntity> topList = new ArrayList<>();
        List<TodoEntity> normalList = new ArrayList<>();
        for (TodoEntity entity : list) {
            if (entity.isTop()) {
                topList.add(entity);
            } else {
                normalList.add(entity);
            }
        }
        topList.addAll(normalList);
        return topList;
    }

    public void setData(List<TodoEntity> list) {
        this.mList = sortTopFirst(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArchiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_todo_list, parent, false);
        return new ArchiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchiveViewHolder holder, int position) {
        TodoEntity todo = mList.get(position);
        holder.tvTitle.setText(todo.getTitle());
        holder.cbComplete.setChecked(todo.isCompleted());
        holder.cbComplete.setEnabled(false); // 归档页禁止修改完成状态

        // 优先级颜色
        switch (todo.getPriority()) {
            case 0:
                holder.viewColor.setBackgroundColor(Color.GRAY);
                break;
            case 1:
                holder.viewColor.setBackgroundColor(Color.BLUE);
                break;
            case 2:
                holder.viewColor.setBackgroundColor(Color.RED);
                break;
        }

        // 标签
        String tag = todo.getTag();
        if (tag != null && !tag.isEmpty()) {
            holder.tvTag.setText("标签：" + tag);
            holder.tvTag.setVisibility(View.VISIBLE);
        } else {
            holder.tvTag.setVisibility(View.GONE);
        }

        // 置顶图标
        if (todo.isTop()) {
            holder.ivTop.setVisibility(View.VISIBLE);
        } else {
            holder.ivTop.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public static class ArchiveViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbComplete;
        TextView tvTitle, tvTag;
        View viewColor;
        ImageView ivTop;

        public ArchiveViewHolder(@NonNull View itemView) {
            super(itemView);
            cbComplete = itemView.findViewById(R.id.cb_complete);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvTag = itemView.findViewById(R.id.tv_item_tag);
            viewColor = itemView.findViewById(R.id.view_priority_color);
            ivTop = itemView.findViewById(R.id.iv_top);
        }
    }
}