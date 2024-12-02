package com.example.myapplication.ui.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insertUser(com.example.myapplication.database.User user);
    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    com.example.myapplication.database.User authenticate(String email, String password);

    @Query("SELECT * FROM users WHERE email = :email")
    com.example.myapplication.database.User getUserByEmail(String email);

    @Query("SELECT * FROM users")
    List<com.example.myapplication.database.User> getAllUsers();

    @Update
    void updateUser(com.example.myapplication.database.User user);

}
