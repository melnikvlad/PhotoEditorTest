package com.example.photoeditor.data.source.local;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.photoeditor.data.ImageItem;
import com.example.photoeditor.data.source.LocalDataSource;
import com.example.photoeditor.data.source.local.db.DBConstant;
import com.example.photoeditor.data.source.local.db.LocalDB;
import com.example.photoeditor.data.source.local.db.PhotosDao;

import java.util.List;

public class LocalDataSourceImpl implements LocalDataSource.DBSource {
    private static LocalDB sDB;

    public LocalDataSourceImpl(Application context) {
        sDB = Room.databaseBuilder(context, LocalDB.class, DBConstant.TABLE_NAME).build();
    }

    @Override
    public List<ImageItem> fetchImages(String sourceId) {
        return sDB.photosDao().getAll(sourceId);
    }

    @Override
    public long writeImage(final ImageItem imageItem) {
        return sDB.photosDao().insert(imageItem);
    }

    @Override
    public int deleteImage(String uuid) {
        return sDB.photosDao().delete(uuid);
    }

    @Override
    public int updateProgress(String uuid, int progress) {
        return sDB.photosDao().updateProgress(uuid, progress);
    }

    @Override
    public int setImageLoaded(String uuid) {
        return sDB.photosDao().setImageLoaded(uuid);
    }
}
