package com.appdev.moodapp.Reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.appdev.moodapp.CalenderViewActivity;
import com.appdev.moodapp.Login_screen;
import com.appdev.moodapp.R;

import java.util.Random;

public class NotifyHandler extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                // reset all alarms
            /*
            context?.let {
                AlarmScheduler(it, mainViewModel.recordingsList, mainViewModel.ringtoneSystemList).rescheduleAllAlarms()
            }
            */
            } else {
                String message = intent.getStringExtra("notify");
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                int requestID = (int) System.currentTimeMillis();
                Intent newIntent = new Intent(context, Login_screen.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK ); // Add appropriate flags
                PendingIntent pendingIntent = PendingIntent.getActivity(context, requestID, newIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


                // Notification channel is required for Android Oreo and above
                NotificationChannel channel = new NotificationChannel("reminder_channel", "Reminder", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reminder_channel")
                        .setContentTitle("Reminder")
                        .setContentText(message).setSmallIcon(R.drawable.baseline_notifications_active_24)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                // Handle additional intent data here if needed

                Notification notification = builder.build();
                notificationManager.notify(generateRandomRequestCode(), notification);
            }
        }
    }
    private int generateRandomRequestCode() {
        Random random = new Random();
        return random.nextInt(99999 - 100 + 1) + 100;
    }
}