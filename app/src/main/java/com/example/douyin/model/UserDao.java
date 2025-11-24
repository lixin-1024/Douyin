package com.example.douyin.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface UserDao {
    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT * FROM users WHERE is_followed = 1")
    LiveData<List<User>> getFollowedUsers();

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Query("DELETE FROM users")
    void deleteAll();
}

