package com.example.douyin.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.douyin.model.AppDatabase;
import com.example.douyin.model.User;
import com.example.douyin.model.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class UserRepository {
    private final UserDao userDao;
    private final LiveData<List<User>> followedUsers;
    private final ExecutorService executorService; // 单线程执行器，用于执行数据库操作

    private boolean hasInsertedSampleData = false;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        followedUsers = userDao.getFollowedUsers();
        executorService = Executors.newSingleThreadExecutor();
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
        executorService.execute(userDao::deleteAll);
    }

    public void insertSampleData(){
        if (hasInsertedSampleData) return;

        executorService.execute(() -> {
            List<User> sampleUsers = new ArrayList<>();
            Random random = new Random();
            for (int i = 1; i <= 30; i++) {
                String userId = String.valueOf(i);
                String userName = "用户" + i;
                String avatar = "avatar_default";
                boolean isSpecialFollowed = random.nextFloat() < 0.3f;
                sampleUsers.add(new User(userId, userName, avatar, true, isSpecialFollowed, null));
            }
            for (User user : sampleUsers) {
                insert(user);
            }
            hasInsertedSampleData = true;
        });
    }
}

