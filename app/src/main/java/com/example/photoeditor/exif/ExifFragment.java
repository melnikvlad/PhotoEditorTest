package com.example.photoeditor.exif;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.photoeditor.R;
import com.example.photoeditor.utils.ActivityUtils;

public class ExifFragment extends Fragment implements ExifContract.View {

    private ExifContract.Presenter mPresenter;

    private EditText inputView;
    private Button btnChange;
    private TextView textExifData;

    public ExifFragment() {
        // empty constructor
    }

    public static ExifFragment getInstance() {
        return new ExifFragment();
    }

    @Override
    public void setPresenter(ExifContract.Presenter presenter) {
        if (presenter != null)
            mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_exif, container, false);

        inputView = root.findViewById(R.id.input);
        btnChange = root.findViewById(R.id.button);
        textExifData = root.findViewById(R.id.text_exif);

        btnChange.setOnClickListener(v -> mPresenter.update(inputView.getText().toString()));

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
    public void error(String msg) {
        if (getActivity() != null)
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void viewExifData(String data) {
        inputView.setText("");
        textExifData.setText(data);
    }
}
