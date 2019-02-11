package com.example.photoeditor.data;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.example.photoeditor.data.source.local.db.DBConstant;
import com.example.photoeditor.utils.UUIDUtils;

@Entity(tableName = DBConstant.TABLE_NAME)
public class ImageItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DBConstant.ID)
    private int id;

    @ColumnInfo(name = DBConstant.UUID)
    private String uuid;

    @ColumnInfo(name = DBConstant.ENCODED_BITMAP)
    private String encodedBitmapString;

    @ColumnInfo(name = DBConstant.SOURCE_PATH)
    private String sourcePath;

    @ColumnInfo(name = DBConstant.PROGRESS)
    private int progress;

    public ImageItem(String encodedBitmapString, String sourcePath) {
        this.uuid = UUIDUtils.generateUUID();
        this.encodedBitmapString = encodedBitmapString;
        this.sourcePath = sourcePath;
        this.progress = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEncodedBitmapString() {
        return encodedBitmapString;
    }

    public void setEncodedBitmapString(String encodedBitmapString) {
        this.encodedBitmapString = encodedBitmapString;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }


    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
