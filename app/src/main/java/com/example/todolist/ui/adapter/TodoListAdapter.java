package com.example.todolist.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist.R;
import com.example.todolist.entity.TodoEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 待办列表适配器
 * 支持：条目点击、复选框状态切换回调
 */
public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoViewHolder> {

    private final Context mContext;
    private List<TodoEntity> mTodoList;

    // 条目点击回调接口
    public interface OnItemClickListener {
        void onItemClick(int position, TodoEntity todo);
    }

    // 复选框状态切换回调接口
    public interface OnCheckChangeListener {
        void onCheckChange(int position, CheckBox checkBox, TodoEntity todo);
    }

    private OnItemClickListener mItemClickListener;
    private OnCheckChangeListener mCheckChangeListener;

    public TodoListAdapter(Context context, List<TodoEntity> todoList) {
        this.mContext = context;
        // 初始化排序
        this.mTodoList = sortTopFirst(todoList);
    }

    /**
     * 刷新数据
     */
    public void setData(List<TodoEntity> list) {
        // 刷新数据也执行排序
        this.mTodoList = sortTopFirst(list);
        notifyDataSetChanged();
    }

    /**
     * 设置条目点击监听
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    /**
     * 设置复选框切换监听
     */
    public void setOnCheckChangeListener(OnCheckChangeListener listener) {
        this.mCheckChangeListener = listener;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_todo_list, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        TodoEntity todo = mTodoList.get(position);
        holder.tvTitle.setText(todo.getTitle());
        holder.cbComplete.setChecked(todo.isCompleted());

        // ========== 新增样式逻辑 ==========
        // 1. 优先级颜色：0普通灰 / 1一般蓝 / 2紧急红
        switch (todo.getPriority()) {
            case 0:
                holder.viewPriorityColor.setBackgroundColor(Color.GRAY);
                break;
            case 1:
                holder.viewPriorityColor.setBackgroundColor(Color.BLUE);
                break;
            case 2:
                holder.viewPriorityColor.setBackgroundColor(Color.RED);
                break;
        }

        // 2. 标签展示，无标签则隐藏
        String tag = todo.getTag();
        if (tag != null && !tag.isEmpty()) {
            holder.tvItemTag.setText("标签：" + tag);
            holder.tvItemTag.setVisibility(View.VISIBLE);
        } else {
            holder.tvItemTag.setVisibility(View.GONE);
        }

        // 3. 置顶图标
        if (todo.isTop()) {
            holder.ivTop.setVisibility(View.VISIBLE);
        } else {
            holder.ivTop.setVisibility(View.GONE);
        }

        // 原有点击、复选框逻辑不变
        holder.itemView.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(position, todo);
            }
        });

        holder.cbComplete.setOnClickListener(v -> {
            if (mCheckChangeListener != null) {
                mCheckChangeListener.onCheckChange(position, holder.cbComplete, todo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTodoList == null ? 0 : mTodoList.size();
    }

    /**
     * ViewHolder
     */
    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbComplete;
        TextView tvTitle;
        // ===== 新增控件 =====
        View viewPriorityColor;
        TextView tvItemTag;
        ImageView ivTop;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            cbComplete = itemView.findViewById(R.id.cb_complete);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            // ===== 绑定新增控件 =====
            viewPriorityColor = itemView.findViewById(R.id.view_priority_color);
            tvItemTag = itemView.findViewById(R.id.tv_item_tag);
            ivTop = itemView.findViewById(R.id.iv_top);
        }
    }

    // 置顶任务排在最前面
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
}
