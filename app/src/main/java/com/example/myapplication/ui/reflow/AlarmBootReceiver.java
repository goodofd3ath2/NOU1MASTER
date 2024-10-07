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
            List<Reminder> reminders = parseReminders(remindersJson); // Convert JSON to List<Reminder>
            for (Reminder reminder : reminders) {
                setAlarm(context, reminder); // Set the alarm for each reminder
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
                // Assuming Reminder has a constructor that takes a JSON object
                reminders.add(new Reminder(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing reminders: " + e.getMessage());
        }
        return reminders;
    }

    private void setAlarm(Context context, Reminder reminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Create an Intent for the AlarmReceiver
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("reminder", reminder.getText()); // Pass the reminder text

        // Create a PendingIntent with a unique request code for each reminder
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.getPriority(), // Unique request code for each alarm
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for security
        );

        // Check if the app can schedule exact alarms for Android 12 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                // Set the alarm using setExact for precise timing
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.getTimeInMillis(), pendingIntent);
                Log.d(TAG, "Alarm set for: " + reminder.getText() + " at " + reminder.getTimeInMillis());
            } else {
                Log.e(TAG, "Cannot schedule exact alarms. Prompting user to enable permission.");
                showPermissionDialog(context); // Show permission dialog
            }
        } else {
            // For API levels below 31, set the alarm directly
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.getTimeInMillis(), pendingIntent);
                Log.d(TAG, "Alarm set for: " + reminder.getText() + " at " + reminder.getTimeInMillis());
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
