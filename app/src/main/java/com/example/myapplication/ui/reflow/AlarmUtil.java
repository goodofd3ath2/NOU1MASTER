package com.example.myapplication.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.example.myapplication.ui.reflow.AlarmReceiver;

import java.util.Calendar;

public class AlarmUtil {
    private static final String TAG = "AlarmUtil";

    // Method to set the alarm
    public static void setAlarm(Context context, int year, int month, int dayOfMonth, int hour, int minute, String reminderText) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Create the intent for AlarmReceiver
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("reminder", reminderText); // Pass the reminder text

        // Create a PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0, // Unique request code for each alarm
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for security
        );

        // Set the calendar time for the alarm
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth, hour, minute, 0);

        // Check if the alarm manager is available
        if (alarmManager != null) {
            // Check for the AlarmManager's ability to schedule exact alarms on Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31 and above
                if (alarmManager.canScheduleExactAlarms()) {
                    setExactAlarm(alarmManager, calendar, pendingIntent, reminderText);
                } else {
                    // Show permission dialog for Android 12+
                    showPermissionDialog(context);
                }
            } else { // For Android versions below 12
                setExactAlarm(alarmManager, calendar, pendingIntent, reminderText);
            }
        } else {
            Log.e(TAG, "AlarmManager is null");
        }
    }

    private static void setExactAlarm(AlarmManager alarmManager, Calendar calendar, PendingIntent pendingIntent, String reminderText) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.d(TAG, "Alarm set for: " + reminderText + " at " + calendar.getTime());
    }

    // Method to show the permission dialog
    public static void showPermissionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alarm Permissions")
                .setMessage("To schedule exact alarms, you need to enable this permission in the app settings.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
