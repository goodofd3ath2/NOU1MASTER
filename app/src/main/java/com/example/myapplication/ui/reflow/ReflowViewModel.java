package com.example.myapplication.ui.reflow;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ReflowViewModel extends ViewModel {

    private final MutableLiveData<List<Reminder>> reminders = new MutableLiveData<>();
    static final String PREFS_NAME = "Reminders";

    public LiveData<List<Reminder>> getReminders() {
        return reminders;
    }

    public LiveData<List<Reminder>> loadReminders(int year, int month, int dayOfMonth, Context context) {
        new Thread(() -> {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String dateKey = String.format("%d_%d_%d", year, month, dayOfMonth);
            String reminderData = prefs.getString(dateKey, "[]");

            List<Reminder> reminderList = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(reminderData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    reminderList.add(Reminder.fromJson(jsonArray.getJSONObject(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            reminders.postValue(reminderList);
        }).start();

        return reminders;
    }
}
