package com.example.todolist.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemeUtil {
    private static final String SP_NAME = "theme_config";
    private static final String KEY_DARK = "is_dark_mode";

    public static void setDarkMode(Context context, boolean isDark) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(KEY_DARK, isDark).apply();
    }

    public static boolean isDarkMode(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_DARK, false);
    }

    public static void applyTheme(Context context) {
        if (isDarkMode(context)) {
            context.setTheme(androidx.appcompat.R.style.Theme_AppCompat_DayNight_DarkActionBar);
        } else {
            context.setTheme(androidx.appcompat.R.style.Theme_AppCompat_Light);
        }
    }
}