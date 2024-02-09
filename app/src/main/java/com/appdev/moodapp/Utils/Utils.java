package com.appdev.moodapp.Utils;

import android.app.Activity;
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
