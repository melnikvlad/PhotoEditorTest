package com.example.photoeditor.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;

public class ImageUtils {

    public static Bitmap createImage(String path) {
        return BitmapFactory.decodeFile(path);
    }

    public static Bitmap rotateImage(Bitmap src, int angle) {
        Matrix matrix = new Matrix();

        matrix.postRotate(angle);

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public static Bitmap toGrayScale(Bitmap src) {
        int width;
        int height;

        height = src.getHeight();
        width = src.getWidth();

        Bitmap grayScaleBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(grayScaleBmp);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(src, 0, 0, paint);

        return grayScaleBmp;
    }

    public static Bitmap flip(Bitmap src) {
        Matrix m = new Matrix();

        m.preScale(-1, 1);
        Bitmap flipBmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        flipBmp.setDensity(DisplayMetrics.DENSITY_DEFAULT);

        return flipBmp;
    }
}
