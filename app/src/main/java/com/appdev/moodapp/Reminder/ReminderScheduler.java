package com.appdev.moodapp.Reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ReminderScheduler {
    private Context context;
    private final AlarmManager alarmManager;
    private Long timeToRemind;

    public ReminderScheduler(Context context, Long timeToRemind) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.timeToRemind = timeToRemind;
    }

    public void schedule(String message, int requestCode) {

        Intent intent = new Intent(context, NotifyHandler.class);
        intent.putExtra("notify", message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeToRemind, pendingIntent);
    }

    public void cancel(String message, int requestCode) {
        Intent intent = new Intent(context, NotifyHandler.class);
        intent.putExtra("notify", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }

}
