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

            reminders.postValue(reminderList); // Notificar os observadores
        }).start();

        return reminders;  // Retornar LiveData contendo os lembretes
    }


    // Salvar lembrete
    public void saveReminder(int year, int month, int dayOfMonth, Reminder reminder, Context context) {
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
                reminderList.add(reminder);  // Adicionar o novo lembrete

                // Salvar os lembretes atualizados
                JSONArray newJsonArray = new JSONArray();
                for (Reminder rem : reminderList) {
                    newJsonArray.put(rem.toJson());
                }

                prefs.edit().putString(dateKey, newJsonArray.toString()).apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Atualizar um lembrete
    public void updateReminder(int year, int month, int dayOfMonth, Reminder updatedReminder, Context context) {
        new Thread(() -> {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String dateKey = String.format("%d_%d_%d", year, month, dayOfMonth);
            String reminderData = prefs.getString(dateKey, "[]");

            try {
                JSONArray jsonArray = new JSONArray(reminderData);
                List<Reminder> reminderList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Reminder reminder = Reminder.fromJson(jsonArray.getJSONObject(i));
                    if (reminder.getId().equals(updatedReminder.getId())) {
                        reminderList.add(updatedReminder);  // Substituir o lembrete existente
                    } else {
                        reminderList.add(reminder);
                    }
                }

                // Salvar os lembretes atualizados
                JSONArray updatedArray = new JSONArray();
                for (Reminder rem : reminderList) {
                    updatedArray.put(rem.toJson());
                }
                prefs.edit().putString(dateKey, updatedArray.toString()).apply();
                reminders.postValue(reminderList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Excluir um lembrete
    public void deleteReminder(int year, int month, int dayOfMonth, Reminder reminder, Context context) {
        new Thread(() -> {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String dateKey = String.format("%d_%d_%d", year, month, dayOfMonth);
            String reminderData = prefs.getString(dateKey, "[]");

            List<Reminder> reminderList = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(reminderData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Reminder existingReminder = Reminder.fromJson(jsonArray.getJSONObject(i));
                    if (!existingReminder.getId().equals(reminder.getId())) {
                        reminderList.add(existingReminder);  // Manter apenas os lembretes que não foram deletados
                    }
                }

                // Salvar os lembretes atualizados
                JSONArray newJsonArray = new JSONArray();
                for (Reminder rem : reminderList) {
                    newJsonArray.put(rem.toJson());
                }

                prefs.edit().putString(dateKey, newJsonArray.toString()).apply();
                reminders.postValue(reminderList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
