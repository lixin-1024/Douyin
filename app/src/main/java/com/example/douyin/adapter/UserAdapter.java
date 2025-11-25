package com.example.douyin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.douyin.R;
import com.example.douyin.model.User;
import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private final Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onFollowClick(int position);
        void onMoreClick(int position);
    }

    public UserAdapter(Context context, List<User> userList, OnItemClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        
        //优先显示备注，如果没有备注则显示昵称
        String displayName = (user.getRemark() != null && !user.getRemark().isEmpty()) 
            ? user.getRemark() 
            : (user.getNickname() != null ? user.getNickname() : "");
        holder.nickname.setText(displayName);

        //设置头像
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            int avatarResId = context.getResources().getIdentifier(user.getAvatar(), "drawable", context.getPackageName());
            if (avatarResId != 0) {
                holder.avatar.setImageResource(avatarResId);
            }
        }

        //根据关注状态更新按钮样式
        if (user.isFollowed()) {
            holder.followButton.setText("已关注");
            holder.followButton.setBackgroundResource(R.drawable.selector_followed_button);
            holder.followButton.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        } else {
            holder.followButton.setText("关注");
            holder.followButton.setBackgroundResource(R.drawable.selector_follow_button);
            holder.followButton.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        }

        //根据特别关注状态显示标识
        if (user.isSpecialFollowed()) {
            holder.specialFollowText.setVisibility(View.VISIBLE);
        } else {
            holder.specialFollowText.setVisibility(View.GONE);
        }

        //绑定点击事件
        holder.followButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFollowClick(holder.getAdapterPosition());
            }
        });
        
        holder.moreButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMoreClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView nickname;
        TextView specialFollowText;
        TextView followButton;
        TextView moreButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.iv_avatar);
            nickname = itemView.findViewById(R.id.tv_nickname);
            specialFollowText = itemView.findViewById(R.id.tv_special_follow);
            followButton = itemView.findViewById(R.id.tv_follow);
            moreButton = itemView.findViewById(R.id.tv_more);
        }
    }
}

