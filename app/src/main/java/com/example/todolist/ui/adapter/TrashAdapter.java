package com.example.todolist.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.todolist.R;
import com.example.todolist.db.TrashBean;
import java.util.List;

public class TrashAdapter extends BaseAdapter {
    private Context context;
    private List<TrashBean> list;

    public TrashAdapter(Context context, List<TrashBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_trash, parent, false);
            holder = new ViewHolder();
            holder.tvTitle = convertView.findViewById(R.id.tv_title);
            holder.tvTime = convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TrashBean bean = list.get(position);
        String title = (bean.getTitle() != null && !bean.getTitle().trim().isEmpty())
                ? bean.getTitle().trim()
                : "无标题笔记";
        holder.tvTitle.setText(title);
        holder.tvTime.setText("删除时间：" + bean.getDeleteTime());

        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle;
        TextView tvTime;
    }
}