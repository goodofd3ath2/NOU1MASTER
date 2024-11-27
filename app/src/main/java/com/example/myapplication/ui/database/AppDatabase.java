package com.example.myapplication.ui.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Reminder.class, com.example.myapplication.database.User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ReminderDao reminderDao();
    public abstract UserDao userDao();
}
