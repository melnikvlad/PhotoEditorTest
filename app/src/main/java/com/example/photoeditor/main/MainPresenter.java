package com.example.photoeditor.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.photoeditor.App;
import com.example.photoeditor.data.ImageItem;
import com.example.photoeditor.data.source.MainRepository;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.photoeditor.data.source.remote.RemoteDataSourceImpl.LOAD_IMAGE_BITMAP;
import static com.example.photoeditor.data.source.remote.RemoteDataSourceImpl.LOAD_IMAGE_MESSAGE;
import static com.example.photoeditor.data.source.remote.RemoteDataSourceImpl.LOAD_IMAGE_PATH;

public class MainPresenter implements Contract.Presenter {
    @Inject
    MainRepository mRepository;

    private Contract.View mView;
    private CompositeDisposable mCompositeDisposable;
    private String mPreviewPhotoPath = null;
    private boolean mHasPreview = false;

    MainPresenter(Contract.View view) {
        if (view != null) {
            mView = view;
            mView.setPresenter(this);

            App.getApp().getAppComponent().inject(this);
        }
    }

    @Override
    public void subscribe() {
        mCompositeDisposable = new CompositeDisposable();
        init();
    }

    @Override
    public void unsubscribe() {
        savePathToPrefs();
        mCompositeDisposable.clear();
    }

    private void init() {
        if (mPreviewPhotoPath != null) {
            setPreviewPhoto();
            setPhotosHistoryList();
        } else {
            mCompositeDisposable.add(
                    mRepository.getSavedPreviewPath()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    (path) -> {
                                        setPath(path);
                                        setPreviewPhoto();
                                        setPhotosHistoryList();
                                    },
                                    (throwable) -> {
                                        mView.initDefaultPreview();
                                        setHasPreview(false);
                                    }
                            ));
        }
    }

    @Override
    public void showSelectedPhoto(Uri data) {
        mCompositeDisposable.add(
                mRepository.getSelectedPhotoPath(data)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (path) -> {
                                    if (!path.equals(mPreviewPhotoPath)) mView.clearList();
                                    setPath(path);
                                    setPreviewPhoto();
                                },
                                (throwable) -> {
                                    mView.initDefaultPreview();
                                    setHasPreview(false);
                                }
                        ));
    }

    @Override
    public void setPreviewPhoto() {
        Bitmap bitmap = mRepository.createBitmap(mPreviewPhotoPath);
        if (bitmap != null) {
            setHasPreview(true);
            mView.setPreviewPhoto(bitmap);
        }
    }

    @Override
    public void setPhotosHistoryList() {
        mCompositeDisposable.add(
                mRepository.fetchPhotos(mPreviewPhotoPath)
                        .flatMapIterable(photos -> photos)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (photo) -> mView.addToListWithProgress(photo)
                        ));
    }

    @Override
    public void deletePhotoFromCache(int pos, String uuid) {
        mCompositeDisposable.add(
                mRepository.deletePhoto(uuid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (res) -> mView.removeFromList(pos)
                        ));
    }

    @Override
    public void setPhotoProgressIsLoaded(String uuid) {
        mCompositeDisposable.add(
                mRepository.setImageLoaded(uuid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe());
    }

    @Override
    public void updatePhotoLoadingProgress(String uuid, int progress) {
        mCompositeDisposable.add(
                mRepository.updateProgress(uuid, progress)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe());
    }

    @Override
    public void createImageFile(final Intent takePictureIntent) {
        mCompositeDisposable.add(
                mRepository.createImageFile()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (file) -> mView.requestCamera(takePictureIntent, file),
                                (throwable) -> mView.error(throwable.getMessage())
                        ));
    }

    @Override
    public void generateNewPreview(String encodedBitmapString) {
        mCompositeDisposable.add(
                mRepository.generateBitmapFromString(encodedBitmapString)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (bitmap) -> {
                                    mView.setPreviewPhoto(bitmap);
                                    setHasPreview(true);
                                }
                        ));
    }

    @Override
    public void rotate(final Bitmap src, int angle) {
        mCompositeDisposable.add(
                mRepository.rotate(mPreviewPhotoPath, src, angle)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (imageItem) -> {
                                    mView.addToList(imageItem);
                                    cachePhoto(imageItem);
                                }
                        ));
    }

    @Override
    public void toGrayScale(final Bitmap src) {
        mCompositeDisposable.add(
                mRepository.toGrayScale(mPreviewPhotoPath, src)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (imageItem) -> {
                                    mView.addToList(imageItem);
                                    cachePhoto(imageItem);
                                }
                        ));
    }

    @Override
    public void flip(final Bitmap src) {
        mCompositeDisposable.add(
                mRepository.flip(mPreviewPhotoPath, src)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (imageItem) -> {
                                    mView.addToList(imageItem);
                                    cachePhoto(imageItem);
                                }
                        ));
    }

    @Override
    public void loadPhotoFromURL(String url) {

        mView.initDefaultPreview();
        mView.setImageLoadingProgress(0);

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                int progress = bundle.getInt(LOAD_IMAGE_MESSAGE);
                String encodedString = bundle.getString(LOAD_IMAGE_BITMAP);
                String path = bundle.getString(LOAD_IMAGE_PATH);

                mView.setImageLoadingProgress(progress);

                if (encodedString != null && path != null) {
                    mView.stopProgress();
                    setPath(path);
                    generateNewPreview(encodedString);
                }
            }
        };

        mRepository.loadRemoteImage(handler, url);
    }

    @Override
    public void cachePhoto(ImageItem item) {
        mCompositeDisposable.add(
                mRepository.cachePhoto(item)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe());
    }

    @Override
    public void savePathToPrefs() {
        if (mPreviewPhotoPath != null)
            mRepository.savePreviewPath(mPreviewPhotoPath);
    }

    @Override
    public String getPath() {
        return mPreviewPhotoPath;
    }

    @Override
    public void setPath(String previewPhotoPath) {
        mPreviewPhotoPath = previewPhotoPath;
    }

    @Override
    public boolean hasPreview() {
        return mHasPreview;
    }

    @Override
    public void setHasPreview(boolean condition) {
        mHasPreview = condition;
    }
}
