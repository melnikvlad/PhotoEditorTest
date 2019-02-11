package com.example.photoeditor.di.module;

import android.app.Application;

import com.example.photoeditor.utils.PreferencesManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class PreferenceModule {
    private final static String PREEF_FILE_NAME = "photo_editor_preferences";

    @Provides
    @Singleton
    PreferencesManager providePreferences(Application context) {
        return new PreferencesManager(context, PREEF_FILE_NAME);
    }
}
