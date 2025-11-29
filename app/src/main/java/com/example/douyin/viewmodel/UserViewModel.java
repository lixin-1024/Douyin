package com.example.douyin.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.douyin.model.User;
import com.example.douyin.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;


public class UserViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final LiveData<List<User>> followedUsers;//从repository获取的已关注用户列表
    private final MediatorLiveData<List<User>> displayUsers = new MediatorLiveData<>();//暴露给UI层的用户列表
    private final MediatorLiveData<Integer> followCount = new MediatorLiveData<>();//暴露给UI层的关注数
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);//加载状态
    private final MutableLiveData<Boolean> isLoadingMore = new MutableLiveData<>(false);//加载更多状态
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();//错误信息
    
    private int currentPage = 1;
    private int totalCount = 0;
    private boolean hasMore = true;
    private boolean isInitialized = false;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
        followedUsers = repository.getFollowedUsers();
        displayUsers.addSource(followedUsers, this::syncDisplayUsers);
        followCount.addSource(followedUsers, this::updateFollowCount);
    }

    private void syncDisplayUsers(List<User> users) {
        if (users != null) {
            displayUsers.setValue(new ArrayList<>(users));
        }
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

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsLoadingMore() {
        return isLoadingMore;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public boolean hasMore() {
        return hasMore;
    }

    /**
     * 初始化加载第一页数据
     */
    public void loadInitialData() {
        if (isInitialized) {
            return;
        }
        isInitialized = true;
        currentPage = 1;
        hasMore = true;
        isLoading.setValue(true);
        
        repository.loadFollowingFromNetwork(currentPage, new UserRepository.LoadCallback() {
            @Override
            public void onSuccess(List<User> users, int total) {
                totalCount = total;
                // 判断是否还有更多数据：如果返回的数据量等于每页大小且当前数据量小于总数，说明还有更多
                hasMore = users.size() >= 10 && users.size() < total;
                currentPage = 1;
                isLoading.postValue(false);
                errorMessage.postValue(null);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    /**
     * 加载更多数据
     */
    public void loadMore() {
        if (isLoadingMore.getValue() != null && isLoadingMore.getValue()) {
            return; // 正在加载中
        }
        if (!hasMore) {
            return; // 没有更多数据
        }
        
        isLoadingMore.setValue(true);
        int nextPage = currentPage + 1;
        
        repository.loadFollowingFromNetwork(nextPage, new UserRepository.LoadCallback() {
            @Override
            public void onSuccess(List<User> users, int total) {
                totalCount = total;
                List<User> currentList = displayUsers.getValue();
                int currentSize = currentList != null ? currentList.size() : 0;
                // 检查是否还有更多数据
                hasMore = currentSize < total;
                currentPage = nextPage;
                isLoadingMore.postValue(false);
                errorMessage.postValue(null);
            }

            @Override
            public void onError(String error) {
                isLoadingMore.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    /**
     * 刷新数据
     */
    public void refresh() {
        currentPage = 1;
        hasMore = true;
        isLoading.setValue(true);
        
        repository.refreshFollowing(new UserRepository.LoadCallback() {
            @Override
            public void onSuccess(List<User> users, int total) {
                totalCount = total;
                // 刷新后，如果返回的数据少于总数，说明还有更多数据
                hasMore = users.size() < total;
                currentPage = 1;
                isLoading.postValue(false);
                errorMessage.postValue(null);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    public void followUser(User user) {
        if (user == null || user.isFollowed()) {
            return;
        }
        user.setFollowed(true);
        repository.update(user);//写入数据库后触发Room的LiveData回调
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

