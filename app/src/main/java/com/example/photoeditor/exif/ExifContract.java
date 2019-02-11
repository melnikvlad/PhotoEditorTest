package com.example.photoeditor.exif;

import com.example.photoeditor.BasePresenter;
import com.example.photoeditor.BaseView;

public interface ExifContract {
    interface View extends BaseView<Presenter> {
        void viewExifData(String data);

        void error(String msg);
    }

    interface Presenter extends BasePresenter {

        void update(String value);

        void getExifData();
    }
}
