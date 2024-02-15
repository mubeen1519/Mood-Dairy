package com.appdev.moodapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.appdev.moodapp.Reminder.ReminderScheduler;
import com.appdev.moodapp.Utils.Utils;
import com.appdev.moodapp.databinding.ActivityReminderSetterBinding;
import com.google.android.material.timepicker.TimeFormat;
import com.ozcanalasalvar.datepicker.view.datepicker.DatePicker;
import com.ozcanalasalvar.datepicker.view.timepicker.TimePicker;

import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

public class ReminderSetter extends AppCompatActivity {

    private ActivityReminderSetterBinding binding;

    int hour, min, day, month, year;
    private ReminderScheduler reminderScheduler;
    String format;
    private ActivityResultLauncher<String> requestLauncher;

    AlertDialog notificationGuidanceDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReminderSetterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if(Utils.isDarkModeActivated(ReminderSetter.this)){
            Utils.status_bar_dark(ReminderSetter.this, R.color.black);
        } else{
            Utils.status_bar(ReminderSetter.this, R.color.lig_bkg);
        }
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SharedPreferences preferences = getSharedPreferences("text_size_prefs", Context.MODE_PRIVATE);

        String textSizeName = preferences.getString("text_size", "");
        switch (textSizeName) {
            case "medium":
                forMedium();
                break;
            case "large":
                forLarge();
                break;
            default:
                forDefault();
                break;
        }

        binding.timepicker.setTimeFormat(TimeFormat.CLOCK_12H);
        binding.datepicker.setDate(System.currentTimeMillis());

        requestLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (!isGranted) {
                showErrorMessageOrSettings();
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isNotificationPermissionGranted()) {
            askForNotificationPermission();
        }

        // Your button click listener
        binding.btnClose.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (isNotificationPermissionGranted()) {
                    if (Objects.requireNonNull(binding.suData.getText()).toString().isEmpty()) {
                        Toast.makeText(ReminderSetter.this, "To-do message cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        calculateTimeToTrigger();
                        finish();
                    }
                } else {
                    askForNotificationPermission();
                }
            }
        });


        binding.timepicker.setTimeChangeListener(new TimePicker.TimeChangeListener() {
            @Override
            public void onTimeChanged(int i, int i1, @Nullable String s) {
                hour = i;
                min = i1;
                format = s;
            }
        });


        binding.datepicker.setDateChangeListener(new DatePicker.DateChangeListener() {
            @Override
            public void onDateChanged(long l, int i, int i1, int i2) {
                day = i;
                month = i1;
                year = i2;
            }
        });

    }

    private void showErrorMessageOrSettings() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            // Permission rationale can be shown, display an error message
            showErrorMessage();
        } else {
            // Permission rationale cannot be shown, take the user to app settings
            showNotificationGuidanceDialog();
        }
    }

    public void forDefault() {
        binding.timepicker.setTextSize(16);
        binding.datepicker.setTextSize(16);
        binding.suData.setTextAppearance(R.style.WeekRow);
    }

    public void forMedium() {
        binding.timepicker.setTextSize(18);
        binding.datepicker.setTextSize(18);
        binding.suData.setTextAppearance(R.style.WeekRowMedium);
    }

    public void forLarge() {
        binding.timepicker.setTextSize(20);
        binding.datepicker.setTextSize(20);
        binding.suData.setTextAppearance(R.style.WeekRowLarge);
    }

    private void showNotificationGuidanceDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Notification Permission Required");
        dialogBuilder.setMessage(
                "To enable notifications:\n" +
                        "\n" +
                        "1. Go to 'App Settings.'\n" +
                        "2. Select 'Manage Notifications.'\n" +
                        "3. Enable 'Allow Notifications.'"
        );
        dialogBuilder.setPositiveButton("Open Settings", (dialog, which) -> openAppSettings());
        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
            if (notificationGuidanceDialog != null && notificationGuidanceDialog.isShowing()) {
                notificationGuidanceDialog.dismiss();
            }
        });
        notificationGuidanceDialog = dialogBuilder.create();
        notificationGuidanceDialog.show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void askForNotificationPermission() {
        requestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    private void showErrorMessage() {
        Toast.makeText(this, "Permission is required for the application to work!", Toast.LENGTH_SHORT).show();
    }

    private boolean isNotificationPermissionGranted() {
        return NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

    private void calculateTimeToTrigger() {
        int minRequestCode = 1; // Minimum request code
        int maxRequestCode = 199999; // Maximum request code

        int requestCode = generateRandomRequestCode(minRequestCode, maxRequestCode);
        Calendar calendar = Calendar.getInstance();

        // Set selected date and time
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, format.equals("PM") ? hour + 12 : hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);

        // Convert selected date and time to milliseconds
        long timeToTrigger = calendar.getTimeInMillis();
        reminderScheduler = new ReminderScheduler(getApplicationContext(), timeToTrigger);
        reminderScheduler.schedule(Objects.requireNonNull(binding.suData.getText()).toString(), requestCode);
        Toast.makeText(ReminderSetter.this, "Reminder is Set ! ", Toast.LENGTH_SHORT).show();
    }

    private int generateRandomRequestCode(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}