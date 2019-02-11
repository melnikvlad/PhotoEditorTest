package com.example.photoeditor.exif;

import com.example.photoeditor.utils.ExifUtils;

import java.io.IOException;

public class ExifPresenter implements ExifContract.Presenter {

    private ExifContract.View mView;
    private ExifUtils mManager;

    public ExifPresenter(ExifContract.View view, String filePath) {
        if (view != null) {
            mView = view;

            mView.setPresenter(this);
        }

        mManager = new ExifUtils(filePath);
    }

    @Override
    public void subscribe() {
        getExifData();
    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void getExifData() {
        try {
            mView.viewExifData(mManager.getAll());
        } catch (IOException e) {
            mView.error(e.getMessage());
        }
    }

    @Override
    public void update(String value) {
        try {
            mManager.setModel(value);
            getExifData();
        } catch (IOException e) {
            mView.error(e.getMessage());
        }


    }
}
