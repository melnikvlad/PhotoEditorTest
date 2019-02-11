package com.example.photoeditor.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static final String MAIN_IMAGE_PATH = "main_image_path";

    private final SharedPreferences mPrefs;

    public PreferencesManager(Context context, String prefFileName) {
        mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }

    public void savePreviewPath(String path) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(MAIN_IMAGE_PATH, path);
        editor.apply();
    }

    public String fetchPreviewPath() {
        return mPrefs.getString(MAIN_IMAGE_PATH, null);

    }
}
