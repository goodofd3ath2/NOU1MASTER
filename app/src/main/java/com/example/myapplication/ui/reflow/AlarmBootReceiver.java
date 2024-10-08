package com.example.myapplication.ui.reflow;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AlarmBootReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Device rebooted, restarting alarms...");
            restartAlarms(context);
        }
    }

    private void restartAlarms(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("reminders", Context.MODE_PRIVATE);
        String remindersJson = sharedPreferences.getString("reminders_list", null);

        if (remindersJson != null) {
            List<Reminder> reminders = parseReminders(remindersJson);
            for (Reminder reminder : reminders) {
                setAlarm(context, reminder);
            }
        } else {
            Log.d(TAG, "No reminders found to restart.");
        }
    }

    private List<Reminder> parseReminders(String remindersJson) {
        List<Reminder> reminders = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(remindersJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                reminders.add(new Reminder(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing reminders: " + e.getMessage());
        }
        return reminders;
    }

    private int getPriorityCode(String priority) {
        switch (priority) {
            case "Importante":
                return 1;  // High priority
            case "Normal":
            default:
                return 0;  // Normal priority
        }
    }

    private void setAlarm(Context context, Reminder reminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("reminderText", reminder.getText());

        // Generate a unique request code using time or some other unique identifier
        int requestCode = (int) reminder.getTimeInMillis(); // You could also hash the reminder text or another field

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,  // Use the integer value of priority as requestCode
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.getTimeInMillis(), pendingIntent);
            } else {
                showPermissionDialog(context);
            }
        } else {
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.getTimeInMillis(), pendingIntent);
            }
        }
    }


    private void showPermissionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alarm Permission Required")
                .setMessage("To schedule exact alarms, please enable the permission in the app settings.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
