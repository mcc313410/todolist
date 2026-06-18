package com.example.todolist.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
import com.example.todolist.R;
import com.example.todolist.db.NoteQueryDao;
import com.example.todolist.entity.NoteEntity;
import com.example.todolist.entity.UserBean;
import com.example.todolist.utils.SPUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import cn.bmob.v3.BmobUser;
import com.example.todolist.db.TodoQueryDao;
import com.example.todolist.utils.DateUtil;
import com.example.todolist.ui.activity.NoteListActivity;
import com.example.todolist.ui.activity.TodoListActivity;
import com.example.todolist.ui.activity.TodoOverdueActivity;

public class MineActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ALBUM = 101;
    private static final int REQUEST_CODE_CROP = 102;
    private static final int PERMISSION_REQUEST_CODE = 103;
    private LinearLayout llOverdueClick;
    private ImageView ivAvatar;
    private TextView tvUsername, tvNoteCount, tvFinishRate, tvOverdueCount, tvTodayTask;
    private Button btnEdit, btnLogout;
    private Uri tempCropUri; // 裁剪临时文件Uri
    private LinearLayout llNoteBox;
    private LinearLayout llTodayTaskBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);

        ivAvatar = findViewById(R.id.iv_avatar);
        tvUsername = findViewById(R.id.tv_username);
        tvNoteCount = findViewById(R.id.tv_note_count);
        tvFinishRate = findViewById(R.id.tv_finish_rate);
        tvOverdueCount = findViewById(R.id.tv_overdue_count);
        tvTodayTask = findViewById(R.id.tv_today_task);
        btnEdit = findViewById(R.id.btn_edit);
        btnLogout = findViewById(R.id.btn_logout);
        llOverdueClick = findViewById(R.id.ll_overdue_box);
        SPUtil.init(this);

        if (!SPUtil.isLogin()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        loadUserInfo();

        // 点击头像 → 权限 → 相册
        ivAvatar.setOnClickListener(v -> openImagePermission());

        btnEdit.setOnClickListener(v -> {
            startActivity(new Intent(MineActivity.this, EditUserInfoActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            BmobUser.logOut();
            SPUtil.clearUser();
            Toast.makeText(MineActivity.this, "已退出登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MineActivity.this, LoginActivity.class));
            finish();
        });
        llOverdueClick = findViewById(R.id.ll_overdue_box);
        llOverdueClick.setOnClickListener(v -> {
            Intent intent = new Intent(MineActivity.this, TodoOverdueActivity.class);
            startActivity(intent);
        });
        // 笔记总数点击跳转笔记列表
        llNoteBox = findViewById(R.id.ll_note_box);
        llNoteBox.setOnClickListener(v -> {
            startActivity(new Intent(MineActivity.this, NoteListActivity.class));
        });

        // 今日待办点击跳转待办列表
        llTodayTaskBox = findViewById(R.id.ll_today_task_box);
        llTodayTaskBox.setOnClickListener(v -> {
            startActivity(new Intent(MineActivity.this, TodoListActivity.class));
        });
    }

    // ------------------------------
    // 权限适配 Android 13+
    // ------------------------------
    private void openImagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
            } else {
                openAlbum();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                openAlbum();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum();
            } else {
                Toast.makeText(this, "请开启相册/图片权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo();
        loadStatistics();
    }

    // ------------------------------
    // 加载用户信息（去掉“用户：”前缀）
    // ------------------------------
    private void loadUserInfo() {
        UserBean user = BmobUser.getCurrentUser(UserBean.class);
        if (user != null) {
            String nick = user.getNickname();
            if (nick != null && !nick.isEmpty()) {
                tvUsername.setText(nick); // 去掉了"用户："前缀
            } else {
                tvUsername.setText(SPUtil.getUsername()); // 直接显示用户名
            }

            String avatarPath = SPUtil.getAvatarPath();
            if (avatarPath != null && !avatarPath.isEmpty()) {
                Glide.with(this).load(new File(avatarPath)).into(ivAvatar);
            }
        }
    }

    // ------------------------------
    // 真实笔记统计
    // ------------------------------
    private void loadStatistics() {
        // ========== 原有笔记统计 不变 ==========
        NoteQueryDao dao = new NoteQueryDao(this);
        List<NoteEntity> list = dao.queryAllNotes();
        tvNoteCount.setText(String.valueOf(list.size()));

        // ========== 待办基础统计（今日待办、逾期）原有代码不变 ==========
        TodoQueryDao todoQueryDao = TodoQueryDao.getInstance(this);
        long todayZero = DateUtil.getTodayZeroMillis();
        long tomorrowZero = DateUtil.getTomorrowZeroMillis();

        int todayTodo = todoQueryDao.getTodayUnCompleteTodoCount(todayZero, tomorrowZero);
        tvTodayTask.setText(String.valueOf(todayTodo));

        int overdueTodo = todoQueryDao.getOverdueUnCompleteTodoCount(todayZero);
        tvOverdueCount.setText(String.valueOf(overdueTodo));

        // ========== 新增：今日全量任务完成率（包含归档） ==========
        int allTodayTask = todoQueryDao.getAllTodayTodoCount(todayZero, tomorrowZero);
        int completeTodayTask = todoQueryDao.getCompletedTodayTodoCount(todayZero, tomorrowZero);
        int finishRate = 0;
        if (allTodayTask > 0) {
            finishRate = (completeTodayTask * 100) / allTodayTask;
        }
        tvFinishRate.setText(finishRate + "%");
    }

    // ------------------------------
    // 打开相册
    // ------------------------------
    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_ALBUM);
    }

    // ------------------------------
    // 图片裁剪
    // ------------------------------
    private void cropImage(Uri uri) {
        try {
            File tempCropFile = new File(getCacheDir(), "crop_temp.jpg");
            tempCropUri = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    tempCropFile
            );

            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 400);
            intent.putExtra("outputY", 400);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempCropUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, REQUEST_CODE_CROP);
        } catch (Exception e) {
            // 设备不支持裁剪，直接原图保存
            handleCropImage(uri);
            Toast.makeText(this, "该设备不支持裁剪，将直接使用原图", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // 相册选图成功，直接处理原图，跳过裁剪流程
            if (requestCode == REQUEST_CODE_ALBUM && data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    handleCropImage(uri);
                    Toast.makeText(this, "头像更换成功", Toast.LENGTH_SHORT).show();
                }
            }
            // 裁剪分支保留，兼容以后恢复裁剪功能
            else if (requestCode == REQUEST_CODE_CROP) {
                handleCropImage(tempCropUri);
            }
        }
    }

    // ------------------------------
    // 处理图片：压缩 + 保存到本地 + 缓存路径
    // ------------------------------
    private void handleCropImage(Uri uri) {
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) {
                Toast.makeText(this, "图片解析失败", Toast.LENGTH_SHORT).show();
                return;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] bytes = baos.toByteArray();

            String fileName = "avatar_" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
            File avatarFile = new File(getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(avatarFile);
            fos.write(bytes);
            fos.flush();
            fos.close();

            SPUtil.saveAvatarPath(avatarFile.getAbsolutePath());
            Glide.with(this).load(avatarFile).into(ivAvatar);
            Toast.makeText(this, "头像修改成功", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "头像修改失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}