package com.example.myapplication.ui.reflow;

import androidx.room.Database;
import androidx.room.RoomDatabase;


// Defina a versão e as entidades do banco de dados
@Database(entities = {Reminder.class}, version = 1)
public abstract class ReminderDatabase extends RoomDatabase {
    // Método abstrato para o DAO
    public abstract ReminderDao reminderDao();
}
