package com.example.comandera.utils;

import android.content.Context;
import android.provider.Settings;

public class DeviceInfo {
    public static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
