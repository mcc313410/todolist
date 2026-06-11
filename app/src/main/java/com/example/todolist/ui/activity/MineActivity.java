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
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

public class MineActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ALBUM = 101;
    private static final int REQUEST_CODE_CROP = 102;
    private static final int PERMISSION_REQUEST_CODE = 103;

    private ImageView ivAvatar;
    private TextView tvUsername, tvNoteCount, tvFinishRate, tvOverdueCount, tvTodayTask;
    private Button btnEdit, btnLogout;
    private Uri tempCropUri; // 裁剪临时文件Uri

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

        SPUtil.init(this);

        if (!SPUtil.isLogin()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        loadUserInfo();

        // 点击头像 → 权限 → 相册 → 裁剪
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
        NoteQueryDao dao = new NoteQueryDao(this);
        List<NoteEntity> list = dao.queryAllNotes();
        tvNoteCount.setText(String.valueOf(list.size()));

        tvFinishRate.setText("0%");
        tvOverdueCount.setText("0");
        tvTodayTask.setText("0");
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
        // 创建临时文件保存裁剪结果
        File tempCropFile = new File(getExternalCacheDir(), "crop_temp.jpg");
        tempCropUri = Uri.fromFile(tempCropFile);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1); // 1:1 方形裁剪
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempCropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // 关闭人脸识别
        startActivityForResult(intent, REQUEST_CODE_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // 相册选图成功 → 进入裁剪
            if (requestCode == REQUEST_CODE_ALBUM && data != null) {
                Uri uri = data.getData();
                if (uri != null) cropImage(uri);
            }
            // 裁剪成功 → 压缩并保存到本地
            else if (requestCode == REQUEST_CODE_CROP) {
                handleCropImage(tempCropUri);
            }
        }
    }

    // ------------------------------
    // 处理裁剪后的图片：压缩 + 保存到本地 + 缓存路径
    // ------------------------------
    private void handleCropImage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos); // 压缩质量70%
            byte[] bytes = baos.toByteArray();

            // 保存到应用私有目录
            String fileName = "avatar_" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
            File avatarFile = new File(getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(avatarFile);
            fos.write(bytes);
            fos.flush();
            fos.close();

            // 保存路径到SP缓存
            SPUtil.saveAvatarPath(avatarFile.getAbsolutePath());

            // 显示头像
            Glide.with(this).load(avatarFile).into(ivAvatar);
            Toast.makeText(this, "头像修改成功", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "头像修改失败", Toast.LENGTH_SHORT).show();
        }
    }
}