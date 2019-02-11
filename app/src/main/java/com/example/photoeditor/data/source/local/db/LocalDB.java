package com.example.photoeditor.data.source.local.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.photoeditor.data.ImageItem;

@Database(entities = {ImageItem.class}, version = 1, exportSchema = false)
public abstract class LocalDB extends RoomDatabase {

    public abstract PhotosDao photosDao();

}
