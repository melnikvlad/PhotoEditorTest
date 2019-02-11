package com.example.photoeditor.di.module;

import android.app.Application;

import com.example.photoeditor.utils.ContentProviderManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module

public class ContentProvideModule {

    @Provides
    @Singleton
    ContentProviderManager providePreferences(Application context) {
        return new ContentProviderManager(context);
    }
}
