package com.example.myapplication.ui.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ReminderDao {

    @Query("SELECT * FROM Reminder")
    List<Reminder> getAllReminders();

    @Insert
    void insertReminder(Reminder reminder);

    @Update
    void updateReminder(Reminder reminder);

    @Delete
    void deleteReminder(Reminder reminder);

    @Query("SELECT * FROM Reminder WHERE timeInMillis BETWEEN :startOfDay AND :endOfDay")
    LiveData<List<Reminder>> getRemindersByTimeRange(long startOfDay, long endOfDay);


}
