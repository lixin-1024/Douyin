package com.example.douyin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.douyin.R;
import com.example.douyin.model.User;
import java.util.ArrayList;
import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private final Context context;
    private final OnItemClickListener listener;
    
    // Glide配置：优化图片加载性能
    private static final RequestOptions requestOptions = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL) // 缓存所有图片
            .centerCrop() // 居中裁剪
            .placeholder(R.drawable.avatar_default) // 占位图
            .error(R.drawable.avatar_default); // 错误图

    public interface OnItemClickListener {
        void onFollowClick(int position);
        void onMoreClick(int position);
    }

    public UserAdapter(Context context, List<User> userList, OnItemClickListener listener) {
        this.context = context;
        this.userList = userList != null ? new ArrayList<>(userList) : new ArrayList<>();
        this.listener = listener;
    }

    /**
     * 使用DiffUtil优化列表更新性能
     */
    public void setUserList(List<User> newUserList) {
        if (newUserList == null) {
            newUserList = new ArrayList<>();
        }
        
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new UserDiffCallback(this.userList, newUserList));
        this.userList.clear();
        this.userList.addAll(newUserList);
        diffResult.dispatchUpdatesTo(this);
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
        if (user == null) {
            return;
        }
        
        //优先显示备注，如果没有备注则显示昵称
        String displayName = (user.getRemark() != null && !user.getRemark().isEmpty()) 
            ? user.getRemark() 
            : (user.getNickname() != null ? user.getNickname() : "");
        holder.nickname.setText(displayName);

        //使用Glide加载头像，优化性能和内存使用
        String avatarUrl = getAvatarUrl(user.getAvatar());
        Glide.with(context)
                .load(avatarUrl)
                .apply(requestOptions)
                .into(holder.avatar);

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
        holder.specialFollowText.setVisibility(user.isSpecialFollowed() ? View.VISIBLE : View.GONE);

        //绑定点击事件 - 使用position参数而不是getAdapterPosition()以提高性能
        holder.followButton.setOnClickListener(v -> {
            if (listener != null && position >= 0 && position < userList.size()) {
                listener.onFollowClick(position);
            }
        });
        
        holder.moreButton.setOnClickListener(v -> {
            if (listener != null && position >= 0 && position < userList.size()) {
                listener.onMoreClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    /**
     * 获取头像URL
     * 根据mock数据格式，avatar可能是"avatar_1"到"avatar_10"
     * 这里使用占位符服务生成头像URL，实际项目中应该使用真实的图片服务器地址
     */
    private String getAvatarUrl(String avatar) {
        if (avatar == null || avatar.isEmpty()) {
            return null;
        }
        // 从avatar字符串中提取数字（如"avatar_1" -> "1"）
        try {
            String number = avatar.replace("avatar_", "");
            int avatarNum = Integer.parseInt(number);
            // 使用占位符服务，根据avatar编号生成固定URL，确保相同编号返回相同图片
            return "https://picsum.photos/seed/" + avatarNum + "/50/50";
        } catch (Exception e) {
            // 如果解析失败，使用hashCode作为随机种子
            return "https://picsum.photos/seed/" + avatar.hashCode() + "/50/50";
        }
    }

    /**
     * DiffUtil回调，用于优化列表更新
     */
    private static class UserDiffCallback extends DiffUtil.Callback {
        private final List<User> oldList;
        private final List<User> newList;

        public UserDiffCallback(List<User> oldList, List<User> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            User oldUser = oldList.get(oldItemPosition);
            User newUser = newList.get(newItemPosition);
            return oldUser.getNickname().equals(newUser.getNickname())
                    && oldUser.getAvatar().equals(newUser.getAvatar())
                    && oldUser.isFollowed() == newUser.isFollowed()
                    && oldUser.isSpecialFollowed() == newUser.isSpecialFollowed()
                    && (oldUser.getRemark() == null ? newUser.getRemark() == null 
                        : oldUser.getRemark().equals(newUser.getRemark()));
        }
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

