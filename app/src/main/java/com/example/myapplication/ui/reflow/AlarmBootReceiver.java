package com.example.myapplication.ui.reflow;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.room.Room;

import com.example.myapplication.ui.database.Reminder;
import com.example.myapplication.ui.database.ReminderDatabase;

import java.util.List;

public class AlarmBootReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Device rebooted. Rescheduling alarms...");
            rescheduleReminders(context);
        }
    }

    /**
     * Reschedules all reminders stored in the database.
     */
    private void rescheduleReminders(Context context) {
        ReminderDatabase database = Room.databaseBuilder(
                context.getApplicationContext(),
                ReminderDatabase.class,
                "reminder_database"
        ).build();

        new Thread(() -> {
            List<Reminder> reminders = database.reminderDao().getAllReminders();
            for (Reminder reminder : reminders) {
                if (reminder.getTimeInMillis() > System.currentTimeMillis()) {
                    scheduleAlarm(context, reminder);
                    Log.d(TAG, "Rescheduled reminder: " + reminder.getText());
                }
            }
        }).start();
    }

    /**
     * Schedules an alarm for a given reminder.
     *
     * @param context  the application context
     * @param reminder the reminder to schedule
     */
    private void scheduleAlarm(Context context, Reminder reminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("reminderText", reminder.getText());
        intent.putExtra("reminderPriority", reminder.getPriority());
        intent.putExtra("reminderNotify", reminder.isNotify());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        try {
            if (alarmManager != null && canScheduleExactAlarms(context)) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminder.getTimeInMillis(),
                        pendingIntent
                );
                Log.d(TAG, "Alarm scheduled for: " + reminder.getText() + " at " + reminder.getTimeInMillis());
            } else {
                Log.e(TAG, "Exact alarms not allowed or AlarmManager is null.");
                requestExactAlarmPermission(context);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Failed to schedule exact alarm", e);
        }
    }

    /**
     * Checks if the app can schedule exact alarms.
     *
     * @param context the application context
     * @return true if exact alarms are allowed, false otherwise
     */
    private boolean canScheduleExactAlarms(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            return alarmManager != null && alarmManager.canScheduleExactAlarms();
        } else {
            // For devices below API level 31, exact alarms are allowed by default.
            return true;
        }
    }
    /**
     * Redirects the user to the settings page to grant the exact alarm permission.
     *
     * @param context the application context
     */
    private void requestExactAlarmPermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
