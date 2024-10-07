package com.example.myapplication.ui.reflow;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "ReminderNotificationChannel";
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String reminder = intent.getStringExtra("reminder");
        Log.d(TAG, "Alarm received: " + reminder);

        // Show a toast message with the reminder
        Toast.makeText(context, "Reminder: " + reminder, Toast.LENGTH_LONG).show();

        if (reminder != null && !reminder.isEmpty()) {
            createNotification(context, reminder);
        } else {
            Log.e(TAG, "No reminder found.");
        }
    }

    @SuppressLint("MissingPermission")  // Suppress warning for missing permission
    private void createNotification(Context context, String reminder) {
        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Set a notification icon
                .setContentTitle("Reminder")
                .setContentText(reminder)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build()); // Use a unique ID for each notification
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminders";
            String description = "Channel for reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
