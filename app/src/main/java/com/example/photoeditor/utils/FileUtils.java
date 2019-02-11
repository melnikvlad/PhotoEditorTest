package com.example.photoeditor.utils;


import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {

    public static File createFile() throws IOException {
        String prefix = "PHOTO_EDITOR_" + DateUtils.generateName();
        String suffix = ".jpg";
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(prefix, suffix, directory);
    }

    public static String saveToExternal(Bitmap bitmap) {
        String prefix = "PHOTO_EDITOR_" + DateUtils.generateName();
        String suffix = ".jpg";
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        directory.mkdirs();

        String name = prefix + suffix;

        File file = new File(directory, name);

        if (file.exists()) file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }
}
