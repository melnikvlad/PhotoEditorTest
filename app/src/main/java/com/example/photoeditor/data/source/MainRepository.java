package com.example.photoeditor.data.source;

import android.content.ContentProvider;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;

import com.example.photoeditor.data.ImageItem;
import com.example.photoeditor.data.source.local.LocalDataSourceImpl;
import com.example.photoeditor.data.source.remote.RemoteDataSourceImpl;
import com.example.photoeditor.utils.BitmapDecoder;
import com.example.photoeditor.utils.ContentProviderManager;
import com.example.photoeditor.utils.FileUtils;
import com.example.photoeditor.utils.ImageUtils;
import com.example.photoeditor.utils.PreferencesManager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class MainRepository {
    @Inject
    LocalDataSourceImpl mLocalDataSource;
    @Inject
    RemoteDataSourceImpl mRemoteDataSource;
    @Inject
    PreferencesManager mPreferences;
    @Inject
    ContentProviderManager mContentManager;

    public MainRepository(LocalDataSourceImpl localDataSource,
                          RemoteDataSourceImpl remoteDataSource,
                          PreferencesManager preferencesManager,
                          ContentProviderManager contentProvider) {
        mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;
        mPreferences = preferencesManager;
        mContentManager = contentProvider;
    }

    public Single<File> createImageFile() {
        return Single.create(emitter -> {
            try {
                emitter.onSuccess(FileUtils.createFile());
            } catch (IOException e) {
                emitter.onError(new Throwable(e.getMessage()));
            }
        });
    }

    public Single<Bitmap> generateBitmapFromString(String encodedBitmapString) {
        return Single.create(emitter -> emitter.onSuccess(BitmapDecoder.stringToBitmap(encodedBitmapString)));
    }

    public Bitmap createBitmap(String path) {
        return ImageUtils.createImage(path);
    }

    public Single<ImageItem> rotate(String mainImagePath, final Bitmap src, int angle) {
        return Single.create(emitter -> {

            Bitmap bitmap = ImageUtils.rotateImage(src, angle);

            String encodedBitmapString = BitmapDecoder.bitmapToString(bitmap);

            ImageItem imageItem = new ImageItem(encodedBitmapString, mainImagePath);

            emitter.onSuccess(imageItem);
        });

    }

    public Single<ImageItem> toGrayScale(String mainImagePath, final Bitmap src) {
        return Single.create(emitter -> {

            Bitmap bitmap = ImageUtils.toGrayScale(src);

            String encodedBitmapString = BitmapDecoder.bitmapToString(bitmap);

            ImageItem imageItem = new ImageItem(encodedBitmapString, mainImagePath);

            emitter.onSuccess(imageItem);
        });
    }

    public Single<ImageItem> flip(String mainImagePath, final Bitmap src) {
        return Single.create(emitter -> {

            Bitmap bitmap = ImageUtils.flip(src);

            String encodedBitmapString = BitmapDecoder.bitmapToString(bitmap);

            ImageItem imageItem = new ImageItem(encodedBitmapString, mainImagePath);

            emitter.onSuccess(imageItem);
        });
    }

    public Completable cachePhoto(final ImageItem item) {
        return Completable.fromCallable(() -> mLocalDataSource.writeImage(item));
    }

    public void loadRemoteImage(final Handler handler, String url) {
        mRemoteDataSource.loadImageAsync(handler, url);
    }

    public Observable<List<ImageItem>> fetchPhotos(String sourceId) {
        return Observable.fromCallable(() -> mLocalDataSource.fetchImages(sourceId));
    }

    public Single<String> getSavedPreviewPath() {
        return Single.create(emitter -> {

            String path = mPreferences.fetchPreviewPath();

            if (path != null) {
                emitter.onSuccess(path);
            } else {
                emitter.onError(new Throwable("No such file"));
            }
        });
    }

    public Single<Integer> deletePhoto(String uuid) {
        return Single.fromCallable(() -> mLocalDataSource.deleteImage(uuid));
    }

    public Completable updateProgress(String uuid, int progress) {
        return Completable.fromCallable((Callable<Integer>) () -> mLocalDataSource.updateProgress(uuid, progress));
    }

    public Completable setImageLoaded(String uuid) {
        return Completable.fromCallable((Callable<Integer>) () -> mLocalDataSource.setImageLoaded(uuid));
    }

    public void savePreviewPath(String path) {
        mPreferences.savePreviewPath(path);
    }

    public Observable<String> getSelectedPhotoPath(final Uri data) {
        return Observable.fromCallable(() -> mContentManager.getSelectedImagePath(data));
    }
}
