package com.example.todolist.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.todolist.R;
import com.example.todolist.ui.activity.TodoDetailActivity;
import com.example.todolist.utils.AlarmUtil;

public class TodoAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "TodoAlarmReceiver";
    private static final String CHANNEL_ID = "todo_remind_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 1. 正确顺序：先取值，后使用
        int todoId = intent.getIntExtra("todo_id", -1);
        String todoTitle = intent.getStringExtra("todo_title");
        int repeatType = intent.getIntExtra("repeat_type", 0);
        long nextTime = intent.getLongExtra("next_time", 0);

        // 打印日志方便调试
        Log.d(TAG, "onReceive 被调用，todoId=" + todoId + ", title=" + todoTitle + ", repeatType=" + repeatType);

        // 2. 校验数据有效性
        if (todoId == -1 || todoTitle == null || todoTitle.isEmpty()) {
            Log.e(TAG, "无效待办任务，取消提醒");
            return;
        }

        // 3. 弹出通知
        sendNotification(context, todoId, todoTitle);

        // 4. 如果是重复提醒，安排下一次闹钟
        if (repeatType != 0) {
            AlarmUtil.setRepeatAlarm(context, todoId, repeatType, nextTime);
        }
    }

    /**
     * 发送系统通知
     */
    private void sendNotification(Context context, int todoId, String title) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android 8.0+ 必须创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "待办事项提醒",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("待办到期、重复任务提醒");
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }

        // 点击通知跳转详情页
        Intent detailIntent = new Intent(context, TodoDetailActivity.class);
        detailIntent.putExtra("todo_id", todoId);
        detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(
                    context,
                    todoId,
                    detailIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
        } else {
            pendingIntent = PendingIntent.getActivity(
                    context,
                    todoId,
                    detailIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
        }

        // 使用系统默认图标，避免资源缺失
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_recent_history)
                .setContentTitle("待办提醒")
                .setContentText(title)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify(todoId, builder.build());
    }
}