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

    public String getPrefsName() {
        return PREFS_NAME;
    }

    public LiveData<List<Reminder>> getReminders() {
        return reminders;
    }

    // Carregar lembretes com base na data (ano, mês, dia) e contexto
    public void loadReminders(int year, int month, int dayOfMonth, Context context) {
        new Thread(() -> {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String dateKey = String.format("%d_%d_%d", year, month, dayOfMonth);
            String reminderData = prefs.getString(dateKey, null);

            List<Reminder> reminderList = new ArrayList<>();
            if (reminderData != null) {
                try {
                    JSONArray jsonArray = new JSONArray(reminderData);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        reminderList.add(Reminder.fromJson(jsonArray.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            reminders.postValue(reminderList);
        }).start();
    }

    // Salvar lembretes no SharedPreferences
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
                reminderList.add(reminder);

                // Criar um novo JSONArray para salvar todos os lembretes
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

    // Deletar um lembrete específico
    public void deleteReminder(int year, int month, int dayOfMonth, Reminder reminder, Context context) {
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

                // Remover o lembrete específico da lista
                reminderList.remove(reminder);

                // Criar um novo JSONArray após remover o lembrete
                JSONArray newJsonArray = new JSONArray();
                for (Reminder rem : reminderList) {
                    newJsonArray.put(rem.toJson());
                }

                prefs.edit().putString(dateKey, newJsonArray.toString()).apply();

                // Atualiza a lista de lembretes no LiveData
                reminders.postValue(reminderList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
