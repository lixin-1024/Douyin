package com.example.douyin.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.douyin.model.User;
import com.example.douyin.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;


public class UserViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final LiveData<List<User>> followedUsers;//从repository获取的已关注用户列表
    private final MediatorLiveData<List<User>> displayUsers = new MediatorLiveData<>();//暴露给UI层的用户列表
    private final MediatorLiveData<Integer> followCount = new MediatorLiveData<>();//暴露给UI层的关注数
    private final List<User> cachedUsers= new ArrayList<>();//缓存的用户列表
    private boolean hasInitializedDisplay = false;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
        followedUsers = repository.getFollowedUsers();
        displayUsers.addSource(followedUsers, this::syncDisplayUsers);
        followCount.addSource(followedUsers, this::updateFollowCount);
    }

    private void syncDisplayUsers(List<User> users) {
        //repository.deleteAll();
        if ((users == null || users.isEmpty())) {
            repository.insertSampleData();
            return;
        }
        if (!hasInitializedDisplay) {
            loadFollowedUsers();
            hasInitializedDisplay = true;
        }

        displayUsers.setValue(new ArrayList<>(cachedUsers));
    }

    private void updateFollowCount(List<User> followedUsers) {
        followCount.setValue(followedUsers != null ? followedUsers.size() : 0);
    }

    public LiveData<List<User>> getDisplayUsers() {
        return displayUsers;
    }

    public LiveData<Integer> getFollowCount() {
        return followCount;
    }

    public void loadFollowedUsers() {
        List<User> followed = followedUsers.getValue();
        if (followed != null) {
            cachedUsers.clear();
            cachedUsers.addAll(followed);
            displayUsers.setValue(new ArrayList<>(cachedUsers));
        }
    }

    public void followUser(User user) {
        if (user == null || user.isFollowed()) {
            return;
        }
        user.setFollowed(true);
        repository.update(user);
    }

    public void unfollowUser(User user) {
        if (user == null || !user.isFollowed()) {
            return;
        }
        user.setFollowed(false);
        user.setSpecialFollowed(false);
        repository.update(user);
    }

    public void setSpecialFollow(User user, boolean isSpecialFollowed) {
        if (user == null) {
            return;
        }
        user.setSpecialFollowed(isSpecialFollowed);
        repository.update(user);
    }

    public void setRemark(User user, String remark) {
        if (user == null) {
            return;
        }
        String nickname = user.getNickname() != null ? user.getNickname() : "";
        String normalized = remark != null ? remark.trim() : "";
        if (normalized.equals(nickname)) {
            normalized = "";
        }
        user.setRemark(normalized.isEmpty() ? null : normalized);
        repository.update(user);
    }
}

