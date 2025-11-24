package com.example.douyin.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.douyin.fragment.FansFragment;
import com.example.douyin.fragment.FollowFragment;
import com.example.douyin.fragment.FriendsFragment;
import com.example.douyin.fragment.MutualFollowFragment;


public class TabPagerAdapter extends FragmentStateAdapter {

    public TabPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MutualFollowFragment(); //互关
            case 1:
                return new FollowFragment(); //关注
            case 2:
                return new FansFragment(); //粉丝
            case 3:
                return new FriendsFragment(); //朋友
            default:
                return new FollowFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}

