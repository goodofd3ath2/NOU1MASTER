package com.example.myapplication.ui.reflow;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReminderDao {
    @Query("SELECT * FROM reminder_table")  // Substitua "reminder_table" pelo nome real da sua tabela
    List<Reminder> getReminders();

    @Insert
    void insertReminder(Reminder reminder);
}