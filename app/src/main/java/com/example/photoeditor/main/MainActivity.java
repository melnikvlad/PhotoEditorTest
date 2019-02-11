package com.example.photoeditor.main;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.example.photoeditor.OnNavigationListener;

import com.example.photoeditor.R;
import com.example.photoeditor.data.source.MainRepository;
import com.example.photoeditor.data.source.local.LocalDataSourceImpl;

import com.example.photoeditor.data.source.local.db.DBConstant;
import com.example.photoeditor.data.source.local.db.LocalDB;
import com.example.photoeditor.data.source.remote.RemoteDataSourceImpl;
import com.example.photoeditor.exif.ExifFragment;
import com.example.photoeditor.exif.ExifPresenter;
import com.example.photoeditor.utils.ActivityUtils;
import com.example.photoeditor.utils.ContentProviderManager;
import com.example.photoeditor.utils.PreferencesManager;

public class MainActivity extends AppCompatActivity implements OnNavigationListener {

    private final static String CAPTURED_PHOTO_PATH_KEY = "preview_photo_path";
    private final static String BUTTONS_STATE_KEY = "buttons visibility option";

    private final static int PERMISSIONS_EXTERNAL_KEY = 2001;

    private final String[] mPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();
    }

    private void start() {
        MainFragment view = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (view == null) {
            view = MainFragment.getInstance();
            ActivityUtils.addFragment(getSupportFragmentManager(), view, R.id.contentFrame);
        }

        mPresenter = new MainPresenter(view);
    }

    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, mPermissions[0]) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, mPermissions[1]) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, mPermissions[0])) {
                showDialog();
            } else {
                ActivityCompat.requestPermissions(this, mPermissions, PERMISSIONS_EXTERNAL_KEY);
            }
        } else {
            start();
        }
    }

    @Override
    public void onExifClicked(String filePath) {
        ExifFragment view = ExifFragment.getInstance();

        ActivityUtils.addFragment(getSupportFragmentManager(), view, R.id.contentFrame);

        new ExifPresenter(view, filePath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_EXTERNAL_KEY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    start();
                } else {
                    checkPermissions();
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        String previewPhotoPath = mPresenter.getPath();
        boolean buttonsVisibility = mPresenter.hasPreview();

        if (previewPhotoPath != null) {
            savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, previewPhotoPath);
        }

        savedInstanceState.putBoolean(BUTTONS_STATE_KEY, buttonsVisibility);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
            mPresenter.setPath(savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY));
        }

        if (savedInstanceState.containsKey(BUTTONS_STATE_KEY)) {
            mPresenter.setHasPreview(savedInstanceState.getBoolean(BUTTONS_STATE_KEY));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void showDialog() {
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }

        builder.setTitle(getString(R.string.permissions_title))
                .setMessage(getString(R.string.permissions_message))
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> checkPermissions())
                .show();
    }
}
