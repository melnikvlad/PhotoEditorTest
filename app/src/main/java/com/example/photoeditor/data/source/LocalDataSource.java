package com.example.photoeditor.data.source;

import com.example.photoeditor.data.ImageItem;

import java.util.List;


public interface LocalDataSource {

    interface DBSource {
        List<ImageItem> fetchImages(String sourceId);

        long writeImage(ImageItem imageItem);

        int deleteImage(String uuid);

        int updateProgress(String uuid, int progress);

        int setImageLoaded(String uuid);

    }
}
