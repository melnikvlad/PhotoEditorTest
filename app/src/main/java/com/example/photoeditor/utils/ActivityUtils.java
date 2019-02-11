package com.example.photoeditor.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class ActivityUtils {

    public static final String BUNDLE_PATH_KEY = "shared path";

    public static void addFragment(final FragmentManager fm, final Fragment fragment, int frameId) {
        if (fm != null && fragment != null) {
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(frameId, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
