package com.example.douyin.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.douyin.model.AppDatabase;
import com.example.douyin.model.User;
import com.example.douyin.model.UserDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class UserRepository {
    private UserDao userDao;
    private LiveData<List<User>> allUsers;
    private LiveData<List<User>> followedUsers;
    private ExecutorService executorService; // 单线程执行器，用于执行数据库操作

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        allUsers = userDao.getAllUsers();
        followedUsers = userDao.getFollowedUsers();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public LiveData<List<User>> getFollowedUsers() {
        return followedUsers;
    }

    public void insert(User user) {
        executorService.execute(() -> userDao.insert(user));
    }

    public void update(User user) {
        executorService.execute(() -> userDao.update(user));
    }

    public void deleteAll() {
        executorService.execute(() -> userDao.deleteAll());
    }
}

