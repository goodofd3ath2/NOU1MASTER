package com.example.myapplication.ui.reflow;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
                reminders.add(Reminder.fromJson(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing reminders: " + e.getMessage());
        }
        return reminders;
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setAlarm(Context context, Reminder reminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("reminderText", reminder.getText());

        int requestCode = (int) reminder.getTimeInMillis();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.getTimeInMillis(), pendingIntent);
        }
    }
}
