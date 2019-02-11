package com.example.photoeditor.di.module;


import com.example.photoeditor.data.source.remote.RemoteDataSourceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RemoteModule {

    @Provides
    @Singleton
    RemoteDataSourceImpl provideRemoteDataSource() {
        return new RemoteDataSourceImpl();
    }
}
