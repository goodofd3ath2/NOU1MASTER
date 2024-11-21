package com.example.myapplication.ui.reflow;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ReminderDao {

    @Insert
    void insertReminder(Reminder reminder);

    @Update
    void updateReminder(Reminder reminder);

    @Delete
    void deleteReminder(Reminder reminder);

    @Query("SELECT * FROM Reminder WHERE id = :id")
    Reminder getReminderById(String id);

    @Query("SELECT * FROM Reminder")
    List<Reminder> getAllReminders();
}
