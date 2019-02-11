package com.example.photoeditor.data.source;

import android.graphics.Bitmap;

import java.io.IOException;
import android.os.Handler;

public interface RemoteDataSource {

    void loadImageAsync(final Handler handler, String url) throws IOException;

}
