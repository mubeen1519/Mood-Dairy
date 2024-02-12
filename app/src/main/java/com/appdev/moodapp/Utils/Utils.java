package com.appdev.moodapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class Utils {
    public static void status_bar(Activity activity, int color)
    {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity,color));
    }
    // Check if the device has a fingerprint sensor
    public static boolean isFingerprintSensorAvailable(Context context) {
        FingerprintManager fingerprintManager = context.getSystemService(FingerprintManager.class);
        return fingerprintManager != null && fingerprintManager.isHardwareDetected();
    }

    // Check if there are enrolled fingerprints on the device
    public static boolean hasEnrolledFingerprints(Context context) {
        FingerprintManager fingerprintManager = context.getSystemService(FingerprintManager.class);
        return fingerprintManager != null && fingerprintManager.hasEnrolledFingerprints();
    }
    private static final String PREF_NAME = "MyPreferences";
    private static final String KEY_BOOLEAN_VALUE = "booleanValue";

    // Method to store a boolean value in SharedPreferences
    public static void saveBoolean(Context context, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_BOOLEAN_VALUE, value);
        editor.apply();
    }

    // Method to get a boolean value from SharedPreferences
    public static boolean getBoolean(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_BOOLEAN_VALUE, false); // Default value is false if not found
    }

    // Method to edit (update) a boolean value in SharedPreferences
    public static void editBoolean(Context context, boolean newValue) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_BOOLEAN_VALUE, newValue);
        editor.apply();
    }

    // Method to delete a boolean value from SharedPreferences
    public static void deleteBoolean(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.remove(KEY_BOOLEAN_VALUE);
        editor.apply();
    }

    public static String displayText(YearMonth yearMonth, boolean shortFormat) {
        return monthToText(yearMonth.getMonth(), shortFormat) + " " + yearMonth.getYear();
    }

    public static String monthToText(Month month, boolean shortFormat) {
        TextStyle style = shortFormat ? TextStyle.SHORT : TextStyle.FULL;
        return month.getDisplayName(style, Locale.ENGLISH);
    }

    public static String displayText(DayOfWeek dayOfWeek, boolean uppercase) {
        String value = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        return uppercase ? value.toUpperCase(Locale.ENGLISH) : value;
    }
    public static void makeVisible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void makeInVisible(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    public static void makeGone(View view) {
        view.setVisibility(View.GONE);
    }
}
