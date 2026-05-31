package com.example.todolist.base;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.todolist.utils.ThemeUtil;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
    }
}