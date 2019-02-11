package com.example.photoeditor.data.source.remote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.photoeditor.data.source.RemoteDataSource;
import com.example.photoeditor.utils.BitmapDecoder;
import com.example.photoeditor.utils.FileUtils;
import com.example.photoeditor.utils.TimeRandomizer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


public class RemoteDataSourceImpl implements RemoteDataSource {

    private static final int PAUSE_FROM = 1;
    private static final int PAUSE_TO = 10;
    public static final String LOAD_IMAGE_MESSAGE = "load image message";
    public static final String LOAD_IMAGE_BITMAP = "load image bitmap";
    public static final String LOAD_IMAGE_PATH = "load image path";

    @Override
    public void loadImageAsync(Handler handler, String url) {

        int pause = TimeRandomizer.getRandomNumberInRange(PAUSE_FROM, PAUSE_TO);
        int pauseInMillis = pause * 10;

        new Thread(() -> {
            Bitmap result = null;
            int progressStatus = 0;

            InputStream in = null;

            while (progressStatus < 100) {
                progressStatus++;

                try {
                    Thread.sleep(pauseInMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (result == null) {
                    try {
                        in = new URL(url).openStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    result = BitmapFactory.decodeStream(in);

                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt(LOAD_IMAGE_MESSAGE, progressStatus);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            String path = FileUtils.saveToExternal(result);

            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString(LOAD_IMAGE_BITMAP, BitmapDecoder.bitmapToString(result));
            bundle.putString(LOAD_IMAGE_PATH, path);
            msg.setData(bundle);
            handler.sendMessage(msg);

        }).start();
    }

}
