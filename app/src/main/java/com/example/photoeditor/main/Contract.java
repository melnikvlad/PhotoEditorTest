package com.example.photoeditor.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;

import com.example.photoeditor.BasePresenter;
import com.example.photoeditor.BaseView;
import com.example.photoeditor.data.ImageItem;

import java.io.File;
import java.util.List;

public interface Contract {

    interface View extends BaseView<Presenter> {

        void enableButtons();

        void disableButtons();

        void requestGallery();

        void requestCamera(Intent intent, File file);

        void requestAddToGallery();

        void checkCamera();

        void setPreviewPhoto(Bitmap bitmap);

        void addToList(ImageItem item);

        void addToListWithProgress(ImageItem item);

        boolean hasCamera();

        boolean resolveActivity(Intent intent);

        void error(String msg);

        void error(int id);

        void showDialog(int pos, ImageItem item);

        void showInputUrlDialog();

        void rotateImage(int angle);

        void toGrayScale();

        void flip();

        void initRecyclerView();

        void initButtons();

        void initDefaultPreview();

        void addAll(List<ImageItem> items);

        void exif();

        void removeFromList(int position);

        void setImageLoadingProgress(int progress);

        void stopProgress();

        void clearList();
    }

    interface Presenter extends BasePresenter {

        void createImageFile(Intent takePictureIntent);

        void setPreviewPhoto();

        void rotate(Bitmap src, int angle);

        void toGrayScale(Bitmap src);

        void flip(Bitmap src);

        void setPhotosHistoryList();

        void savePathToPrefs();

        void deletePhotoFromCache(int pos, String uuid);

        void setPhotoProgressIsLoaded(String uuid);

        void updatePhotoLoadingProgress(String uuid, int progress);

        void setPath(String previewPhotoPath);

        String getPath();

        boolean hasPreview();

        void setHasPreview(boolean condition);

        void generateNewPreview(String encodedBitmapString);

        void loadPhotoFromURL(String url);

        void cachePhoto(ImageItem item);

        void showSelectedPhoto(Uri data);

    }
}
