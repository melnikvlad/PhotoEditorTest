package com.example.photoeditor;

import android.app.Application;

import com.example.photoeditor.di.component.AppComponent;

import com.example.photoeditor.di.component.DaggerAppComponent;
import com.example.photoeditor.di.module.AppModule;
import com.example.photoeditor.di.module.ContentProvideModule;
import com.example.photoeditor.di.module.DBModule;
import com.example.photoeditor.di.module.PreferenceModule;
import com.example.photoeditor.di.module.RemoteModule;

public class App extends Application {

    private static App sInstance = null;

    private AppComponent appComponent;

    public static App getApp() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(sInstance))
                .dBModule(new DBModule())
                .contentProvideModule(new ContentProvideModule())
                .preferenceModule(new PreferenceModule())
                .remoteModule(new RemoteModule())
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
