package com.example.photoeditor.utils;

import android.media.ExifInterface;

import java.io.IOException;

public class ExifUtils {
    private String path;
    private static ExifInterface exif;

    public ExifUtils(String path) {
        this.path = path;
    }

    public String getAll() throws IOException {
        exif = new ExifInterface(path);

        String atrs = "Exif information ---\n";
        atrs += getTagString(ExifInterface.TAG_DATETIME, exif);
        atrs += getTagString(ExifInterface.TAG_FLASH, exif);
        atrs += getTagString(ExifInterface.TAG_GPS_LATITUDE, exif);
        atrs += getTagString(ExifInterface.TAG_GPS_LATITUDE_REF, exif);
        atrs += getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif);
        atrs += getTagString(ExifInterface.TAG_GPS_LONGITUDE_REF, exif);
        atrs += getTagString(ExifInterface.TAG_IMAGE_LENGTH, exif);
        atrs += getTagString(ExifInterface.TAG_IMAGE_WIDTH, exif);
        atrs += getTagString(ExifInterface.TAG_MAKE, exif);
        atrs += getTagString(ExifInterface.TAG_MODEL, exif);
        atrs += getTagString(ExifInterface.TAG_ORIENTATION, exif);
        atrs += getTagString(ExifInterface.TAG_WHITE_BALANCE, exif);

        return atrs;
    }

    private String getTagString(String tag, ExifInterface exif) {
        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }

    public void setModel(String value) throws IOException {
        exif = new ExifInterface(path);

        exif.setAttribute(ExifInterface.TAG_MODEL, value);
        exif.saveAttributes();
    }
}
