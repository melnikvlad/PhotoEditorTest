package com.example.photoeditor.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class ContentProviderManager {
    private Context mContext;

    public ContentProviderManager(Context context) {
        mContext = context;
    }

    public String getSelectedImagePath(Uri selectedImage) {
        Cursor cursor = null;
        String picturePath = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        if (selectedImage != null) {
            cursor = mContext.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        }

        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
        }

        return picturePath;
    }
}
