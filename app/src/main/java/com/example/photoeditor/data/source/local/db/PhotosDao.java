package com.example.photoeditor.data.source.local.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.photoeditor.data.ImageItem;

import java.util.List;

@Dao
public interface PhotosDao {

    @Query("SELECT * FROM " + DBConstant.TABLE_NAME + " WHERE source_path = :sourcePath")
    List<ImageItem> getAll(String sourcePath);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ImageItem item);

    @Query("DELETE FROM " + DBConstant.TABLE_NAME + " WHERE uuid = :uuid")
    int delete(String uuid);

    @Query("UPDATE " + DBConstant.TABLE_NAME + " SET progress = :progress WHERE uuid = :uuid")
    int updateProgress(String uuid, int progress);

    @Query("UPDATE " + DBConstant.TABLE_NAME + " SET progress = 100 WHERE uuid = :uuid")
    int setImageLoaded(String uuid);
}
