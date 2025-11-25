package com.example.douyin.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;


@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private String id;
    
    @ColumnInfo(name = "nickname")
    private String nickname;
    
    @ColumnInfo(name = "avatar")
    private String avatar;
    
    @ColumnInfo(name = "is_followed")
    private boolean isFollowed;
    
    @ColumnInfo(name = "is_special_followed")
    private boolean isSpecialFollowed;
    
    @ColumnInfo(name = "remark")
    private String remark;

    public User(@NonNull String id, String nickname, String avatar, boolean isFollowed, boolean isSpecialFollowed, String remark) {
        this.id = id;
        this.nickname = nickname;
        this.avatar = avatar;
        this.isFollowed = isFollowed;
        this.isSpecialFollowed = isSpecialFollowed;
        this.remark = remark;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public boolean isSpecialFollowed() {
        return isSpecialFollowed;
    }

    public void setSpecialFollowed(boolean specialFollowed) {
        isSpecialFollowed = specialFollowed;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}