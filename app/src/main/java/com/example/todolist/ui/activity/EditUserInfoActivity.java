package com.example.todolist.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.todolist.R;
import com.example.todolist.entity.UserBean;  // 关键：用你自己的 UserBean
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class EditUserInfoActivity extends AppCompatActivity {

    private EditText etNickname;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);

        etNickname = findViewById(R.id.et_nickname);
        rgGender = findViewById(R.id.rg_gender);
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);

        // 回显：用 UserBean，不是 BmobUser
        UserBean user = UserBean.getCurrentUser(UserBean.class);
        if (user != null) {
            String nick = user.getNickname();
            if (!TextUtils.isEmpty(nick)) {
                etNickname.setText(nick);
            }
            String gender = user.getGender();
            if ("男".equals(gender)) {
                rbMale.setChecked(true);
            } else if ("女".equals(gender)) {
                rbFemale.setChecked(true);
            }
        }

        findViewById(R.id.btn_save).setOnClickListener(v -> saveUserInfo());
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
    }

    private void saveUserInfo() {
        String nick = etNickname.getText().toString().trim();
        if (TextUtils.isEmpty(nick)) {
            Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String gender = "男";
        if (rgGender.getCheckedRadioButtonId() == R.id.rb_female) {
            gender = "女";
        }

        // 同样用 UserBean
        UserBean user = UserBean.getCurrentUser(UserBean.class);
        if (user == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        user.setNickname(nick);
        user.setGender(gender);

        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(EditUserInfoActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditUserInfoActivity.this, "修改失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}