package com.example.photoeditor.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class DateUtils {

    static String generateName() {
        return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US).format(new Date());
    }
}
