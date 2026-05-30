package com.example.todolist.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;
import com.example.todolist.entity.NoteEntity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class FileExportUtil {

    public static void exportNoteToTxt(Context context, NoteEntity note) {
        // 检查外置存储
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "存储不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "NoteFile");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, note.getTitle() + ".txt");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write("标题：" + note.getTitle() + "\n");
            osw.write("时间：" + note.getCreateTime() + "\n");
            osw.write("内容：\n" + note.getContent());
            osw.flush();
            osw.close();
            fos.close();
            Toast.makeText(context, "导出成功，路径：文档/NoteFile", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "导出失败", Toast.LENGTH_SHORT).show();
        }
    }
}