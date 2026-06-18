package com.example.todolist.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
    /**
     * 获取今日凌晨0点时间戳
     */
    public static long getTodayZeroMillis() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date today = sdf.parse(sdf.format(new Date()));
            return today.getTime();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }

    /**
     * 获取明日凌晨0点时间戳
     */
    public static long getTomorrowZeroMillis() {
        return getTodayZeroMillis() + 24 * 60 * 60 * 1000L;
    }
}