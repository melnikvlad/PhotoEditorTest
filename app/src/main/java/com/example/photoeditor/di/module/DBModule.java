package com.example.photoeditor.di.module;

import android.app.Application;

import com.example.photoeditor.data.source.local.LocalDataSourceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DBModule {

    @Provides
    @Singleton
    LocalDataSourceImpl provideDB(Application application) {
        return new LocalDataSourceImpl(application);
    }
}
