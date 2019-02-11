package com.example.photoeditor.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.photoeditor.OnNavigationListener;
import com.example.photoeditor.R;
import com.example.photoeditor.data.ImageItem;
import com.example.photoeditor.main.adapter.ImageAdapter;

import java.io.File;
import java.util.List;

import static com.example.photoeditor.main.adapter.ImageAdapter.START;

public class MainFragment extends Fragment implements Contract.View {
    private static final int REQUEST_TAKE_PHOTO = 1001;
    private static final int IMAGE_PICKER_SELECT = 1003;
    private static final int ANGLE = 90;

    private Contract.Presenter mPresenter;
    private OnNavigationListener mListener;
    private ImageAdapter mAdapter;

    //Views
    private ImageView imgHolder;
    private RecyclerView rv;
    private Button btnCamera;
    private Button btnGallery;
    private Button btnRotateLeft;
    private Button btnRotateRight;
    private Button btnToGray;
    private Button btnFlip;
    private Button btnURL;
    private Button btnExif;
    private ProgressBar progressBar;

    public MainFragment() {
        // empty constructor
    }

    public static MainFragment getInstance() {
        return new MainFragment();
    }

    @Override
    public void setPresenter(Contract.Presenter presenter) {
        if (presenter != null)
            mPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ImageAdapter(getContext(), new ImageAdapter.OnImageItemActionListener() {
            @Override
            public void onItemClick(int pos, ImageItem item) {
                showDialog(pos, item);
            }

            @Override
            public void onItemLoaded(String uuid) {
                mPresenter.setPhotoProgressIsLoaded(uuid);
            }

            @Override
            public void onItemChangedProgress(String uuid, int progress) {
                mPresenter.updatePhotoLoadingProgress(uuid, progress);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context != null) mListener = (OnNavigationListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        imgHolder = root.findViewById(R.id.imgHolder);
        rv = root.findViewById(R.id.rv);
        btnCamera = root.findViewById(R.id.btnCamera);
        btnGallery = root.findViewById(R.id.btnGallery);
        btnRotateLeft = root.findViewById(R.id.btnRtLeft);
        btnRotateRight = root.findViewById(R.id.btnRtRight);
        btnToGray = root.findViewById(R.id.btnGray);
        btnFlip = root.findViewById(R.id.btnFlip);
        btnURL = root.findViewById(R.id.btnURL);
        btnExif = root.findViewById(R.id.btnExif);
        progressBar = root.findViewById(R.id.progress);

        initRecyclerView();
        initButtons();

        btnCamera.setOnClickListener((v) -> checkCamera());
        btnGallery.setOnClickListener((v) -> requestGallery());
        btnRotateLeft.setOnClickListener((v) -> rotateImage(-ANGLE));
        btnRotateRight.setOnClickListener((v) -> rotateImage(ANGLE));
        btnToGray.setOnClickListener((v) -> toGrayScale());
        btnFlip.setOnClickListener((v) -> flip());
        btnURL.setOnClickListener((v) -> showInputUrlDialog());
        btnExif.setOnClickListener((v) -> exif());

        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onStop() {
        mPresenter.unsubscribe();
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {

            requestAddToGallery();

            mPresenter.setPreviewPhoto();

        } else if (requestCode == IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {
            mPresenter.showSelectedPhoto(data.getData());
        }
    }

    @Override
    public void checkCamera() {
        if (!hasCamera()) {
            error(R.string.error_no_camera);
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (!resolveActivity(takePictureIntent)) {
            error(R.string.error_no_camera_activity);
            return;
        }

        mPresenter.createImageFile(takePictureIntent);
    }

    @Override
    public boolean hasCamera() {
        return getActivity() != null && getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public boolean resolveActivity(final Intent intent) {
        return getActivity() != null && intent.resolveActivity(getActivity().getPackageManager()) != null;
    }

    @Override
    public void requestGallery() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, IMAGE_PICKER_SELECT);
    }

    @Override
    public void requestAddToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        if (getActivity() != null) {
            File fileToSave = new File(mPresenter.getPath());
            Uri uri = Uri.fromFile(fileToSave);
            mediaScanIntent.setData(uri);
            getActivity().sendBroadcast(mediaScanIntent);
        }
    }

    @Override
    public void requestCamera(final Intent intent, final File file) {
        if (file != null && getActivity() != null) {

            Uri photoURI = FileProvider.getUriForFile(getActivity(), "com.example.photoeditor.fileprovider", file);

            mPresenter.setPath(file.getAbsolutePath());

            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    public void rotateImage(final int angle) {
        Bitmap src = ((BitmapDrawable) imgHolder.getDrawable()).getBitmap();
        mPresenter.rotate(src, angle);
    }

    @Override
    public void toGrayScale() {
        Bitmap src = ((BitmapDrawable) imgHolder.getDrawable()).getBitmap();
        mPresenter.toGrayScale(src);
    }

    @Override
    public void flip() {
        Bitmap src = ((BitmapDrawable) imgHolder.getDrawable()).getBitmap();
        mPresenter.flip(src);
    }

    @Override
    public void exif() {
        mListener.onExifClicked(mPresenter.getPath());
    }

    @Override
    public void setImageLoadingProgress(int progress) {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(progress);
    }

    @Override
    public void stopProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void addToList(final ImageItem item) {
        mAdapter.add(item);
        rv.scrollToPosition(START);
    }

    @Override
    public void addAll(final List<ImageItem> items) {
        mAdapter.addAll(items);
        rv.scrollToPosition(START);
    }

    @Override
    public void addToListWithProgress(ImageItem item) {
        mAdapter.addWithProgress(item);
        rv.scrollToPosition(START);
    }

    @Override
    public void removeFromList(int position) {
        mAdapter.remove(position);
    }

    @Override
    public void clearList() {
        mAdapter.clear();
    }

    @Override
    public void showDialog(int pos, final ImageItem item) {
        AlertDialog.Builder builder;

        if (getActivity() != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(getActivity());
            }

            builder.setTitle(getString(R.string.choose_action))
                    .setMessage(getString(R.string.alert_message))
                    .setPositiveButton(getString(R.string.remove), (dialog, which) -> mPresenter.deletePhotoFromCache(pos, item.getUuid()))
                    .setNegativeButton(R.string.set_as_default, (dialog, which) -> mPresenter.generateNewPreview(item.getEncodedBitmapString()))
                    .show();
        }
    }

    @Override
    public void showInputUrlDialog() {
        AlertDialog.Builder builder;

        if (getActivity() != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(getActivity());
            }

            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);

            builder.setTitle(getString(R.string.dialog_load_url_title))
                    .setMessage(getString(R.string.dialog_load_url_message))
                    .setView(input)
                    .setPositiveButton(getString(R.string.load), (dialog, which) -> mPresenter.loadPhotoFromURL(input.getText().toString()))
                    .setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel())
                    .show();
        }
    }

    @Override
    public void initDefaultPreview() {
        if (getActivity() != null)
            imgHolder.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));

        initButtons();
    }

    @Override
    public void initRecyclerView() {
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(mAdapter);
    }

    @Override
    public void initButtons() {
        if (mPresenter.hasPreview()) {
            enableButtons();
        } else {
            disableButtons();
        }
    }

    @Override
    public void setPreviewPhoto(final Bitmap bitmap) {
        if (getActivity() != null)
            imgHolder.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.main));

        imgHolder.setImageBitmap(bitmap);
        initButtons();
    }

    @Override
    public void error(final String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void error(final int id) {
        Toast.makeText(getActivity(), getString(id), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void enableButtons() {
        btnFlip.setVisibility(View.VISIBLE);
        btnExif.setVisibility(View.VISIBLE);
        btnRotateLeft.setVisibility(View.VISIBLE);
        btnRotateRight.setVisibility(View.VISIBLE);
        btnToGray.setVisibility(View.VISIBLE);
    }

    @Override
    public void disableButtons() {
        btnFlip.setVisibility(View.GONE);
        btnExif.setVisibility(View.GONE);
        btnRotateLeft.setVisibility(View.GONE);
        btnRotateRight.setVisibility(View.GONE);
        btnToGray.setVisibility(View.GONE);
    }
}
