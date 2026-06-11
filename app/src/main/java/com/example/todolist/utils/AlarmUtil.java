package com.example.todolist.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.todolist.db.TodoEditDao;
import com.example.todolist.db.TodoQueryDao;
import com.example.todolist.entity.TodoEntity;
import com.example.todolist.receiver.TodoAlarmReceiver;

import java.util.Calendar;

public class AlarmUtil {
    private static final String TAG = "AlarmUtil";

    /**
     * 设置单次到期提醒闹钟
     */
    public static void setAlarm(Context context, TodoEntity todo) {
        if (todo == null || todo.getDeadlineTime() <= System.currentTimeMillis()) {
            Log.e(TAG, "时间已过期，不设置闹钟");
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TodoAlarmReceiver.class);
        intent.putExtra("todo_id", todo.getId());
        intent.putExtra("todo_title", todo.getTitle());
        intent.putExtra("repeat_type", todo.getRepeatType());
        intent.putExtra("next_time", todo.getDeadlineTime());

        PendingIntent pendingIntent = getBroadcastPendingIntent(context, todo.getId(), intent);

        // 区分版本设置精准闹钟
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    todo.getDeadlineTime(),
                    pendingIntent
            );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, todo.getDeadlineTime(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, todo.getDeadlineTime(), pendingIntent);
        }
    }

    /**
     * 设置重复闹钟（每日/每周/每月）
     */
    public static void setRepeatAlarm(Context context, int todoId, int repeatType, long currentTime) {
        long nextTime = calculateNextTime(currentTime, repeatType);
        TodoEntity todo = TodoQueryDao.getInstance(context).queryTodoById(todoId);
        if (todo == null) return;

        // 更新下一次提醒时间到数据库
        todo.setDeadlineTime(nextTime);
        TodoEditDao.getInstance(context).updateTodo(todo);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TodoAlarmReceiver.class);
        intent.putExtra("todo_id", todoId);
        intent.putExtra("todo_title", todo.getTitle());
        intent.putExtra("repeat_type", repeatType);
        intent.putExtra("next_time", nextTime);

        PendingIntent pendingIntent = getBroadcastPendingIntent(context, todoId, intent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTime, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextTime, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextTime, pendingIntent);
        }
    }

    /**
     * 取消指定任务闹钟（删除/编辑任务时调用）
     */
    public static void cancelAlarm(Context context, int todoId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TodoAlarmReceiver.class);
        PendingIntent pendingIntent = getBroadcastPendingIntent(context, todoId, intent);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    /**
     * 计算下一次提醒时间
     * 0:不重复 1:每天 2:每周 3:每月
     */
    private static long calculateNextTime(long currentTime, int repeatType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        switch (repeatType) {
            case 1:
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                break;
            case 2:
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case 3:
                calendar.add(Calendar.MONTH, 1);
                break;
            default:
                return currentTime;
        }
        return calendar.getTimeInMillis();
    }

    /**
     * 统一获取广播 PendingIntent，兼容全版本
     */
    private static PendingIntent getBroadcastPendingIntent(Context context, int requestCode, Intent intent) {
        int flag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flag = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            flag = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        return PendingIntent.getBroadcast(context, requestCode, intent, flag);
    }
}