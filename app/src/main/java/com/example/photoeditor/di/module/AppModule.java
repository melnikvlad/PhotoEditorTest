package com.example.photoeditor.di.module;

import android.app.Application;

import com.example.photoeditor.data.source.MainRepository;
import com.example.photoeditor.data.source.local.LocalDataSourceImpl;
import com.example.photoeditor.data.source.remote.RemoteDataSourceImpl;
import com.example.photoeditor.utils.ContentProviderManager;
import com.example.photoeditor.utils.PreferencesManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    MainRepository provideRepository(LocalDataSourceImpl localDataSource,
                                     RemoteDataSourceImpl remoteDataSource,
                                     PreferencesManager preferencesManager,
                                     ContentProviderManager contentProvider) {

        return new MainRepository(localDataSource, remoteDataSource, preferencesManager, contentProvider);
    }
}