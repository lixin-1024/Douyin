package com.example.douyin.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.douyin.model.AppDatabase;
import com.example.douyin.model.FollowResponse;
import com.example.douyin.model.User;
import com.example.douyin.model.UserDao;
import com.example.douyin.network.ApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class UserRepository {
    private final UserDao userDao;
    private final LiveData<List<User>> followedUsers;
    private final ExecutorService executorService; // 单线程执行器，用于执行数据库操作
    private final ApiService apiService;

    public interface LoadCallback {
        void onSuccess(List<User> users, int total);
        void onError(String error);
    }

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        followedUsers = userDao.getFollowedUsers();
        executorService = Executors.newSingleThreadExecutor();
        apiService = ApiService.getInstance();
    }

    public LiveData<List<User>> getFollowedUsers() {
        return followedUsers;
    }

    public void insert(User user) {
        executorService.execute(() -> userDao.insert(user));
    }

    public void insertAll(List<User> users) {
        executorService.execute(() -> {
            for (User user : users) {
                userDao.insert(user);
            }
        });
    }

    public void update(User user) {
        executorService.execute(() -> userDao.update(user));
    }

    public void deleteAll() {
        executorService.execute(userDao::deleteAll);
    }

    /**
     * 从网络加载分页数据
     * @param page 页码，从1开始
     * @param callback 加载回调
     */
    public void loadFollowingFromNetwork(int page, LoadCallback callback) {
        executorService.execute(() -> {
            try {
                FollowResponse response = apiService.getFollowingList(page);
                if (response.getCode() == 200 && response.getData() != null) {
                    List<User> users = new ArrayList<>();
                    if (response.getData().getList() != null) {
                        for (FollowResponse.UserResponse userResponse : response.getData().getList()) {
                            users.add(userResponse.toUser());
                        }
                    }
                    // 保存到数据库
                    insertAll(users);
                    // 回调成功
                    if (callback != null) {
                        callback.onSuccess(users, response.getData().getTotal());
                    }
                } else {
                    if (callback != null) {
                        callback.onError("服务器返回错误: " + (response.getMsg() != null ? response.getMsg() : "未知错误"));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("网络请求失败: " + e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("数据解析失败: " + e.getMessage());
                }
            }
        });
    }

    /**
     * 刷新数据：清空数据库并从第一页开始加载
     * @param callback 加载回调
     */
    public void refreshFollowing(LoadCallback callback) {
        executorService.execute(() -> {
            deleteAll();
            loadFollowingFromNetwork(1, callback);
        });
    }
}

