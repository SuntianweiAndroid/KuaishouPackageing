package com.kuaishoulibrary.utils;

import android.util.Log;

import com.kuaishoulibrary.BuildConfig;


public class Logcat {
    private static final String TAG = "kuaishou";

    public static void d(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }
}
