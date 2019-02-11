package com.example.photoeditor.di.component;

import com.example.photoeditor.di.module.AppModule;
import com.example.photoeditor.di.module.ContentProvideModule;
import com.example.photoeditor.di.module.DBModule;
import com.example.photoeditor.di.module.PreferenceModule;
import com.example.photoeditor.di.module.RemoteModule;
import com.example.photoeditor.main.MainPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        AppModule.class,
        ContentProvideModule.class,
        DBModule.class,
        PreferenceModule.class,
        RemoteModule.class
})
@Singleton
public interface AppComponent {
    void inject(MainPresenter presenter);
}
