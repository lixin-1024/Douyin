package com.example.douyin.fragment;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.douyin.R;
import com.example.douyin.adapter.UserAdapter;
import com.example.douyin.model.User;
import com.example.douyin.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;


public class FollowFragment extends Fragment implements UserAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private UserViewModel userViewModel;
    private TextView tvFollowCount;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final List<User> displayedUsers = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow, container, false);

        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        setupViewModel();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rv_follow_list);
        tvFollowCount = view.findViewById(R.id.tv_follow_count);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(getContext(), new ArrayList<>(), this);
        recyclerView.setAdapter(userAdapter);
    }

    //下拉刷新
    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(android.R.color.darker_gray);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (userViewModel != null) {
                userViewModel.loadFollowedUsers();
            }
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void setupViewModel() {
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getDisplayUsers().observe(getViewLifecycleOwner(), users -> {
            displayedUsers.clear();
            if (users != null) {
                displayedUsers.addAll(users);
                userAdapter.setUserList(new ArrayList<>(displayedUsers));
            } else {
                userAdapter.setUserList(new ArrayList<>());
            }
        });
        userViewModel.getFollowCount().observe(getViewLifecycleOwner(), count -> {
            int safeCount = count != null ? count : 0;
            tvFollowCount.setText("我的关注 (" + safeCount + "人)");
        });
    }

    //关注和取关按钮点击事件
    @Override
    public void onFollowClick(int position) {
        User user = getUserAt(position);
        if (user == null) return;

        //判断是否为特别关注
        if (user.isFollowed()) {
            if (user.isSpecialFollowed()) {
                showSpecialUnfollowDialog(user);//特别关注用户，弹出二次确认
            } else {
                performUnfollow(user, "已取关: " + user.getNickname());
            }
        } else {
            userViewModel.followUser(user);
            Toast.makeText(getContext(), "关注成功: " + user.getNickname(), Toast.LENGTH_SHORT).show();
        }
    }

    //更多选项按钮点击事件
    @Override
    public void onMoreClick(int position) {
        User user = getUserAt(position);
        if (user == null) return;

        //如果用户已取关
        if (!user.isFollowed()) {
            Toast.makeText(getContext(), "已取关，无法使用", Toast.LENGTH_SHORT).show();
            return;
        }

        showMoreOptionsDialog(user);
    }

    //更多选项对话框
    private void showMoreOptionsDialog(User user) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_more_options, null);

        com.google.android.material.bottomsheet.BottomSheetDialog dialog =
            new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        dialog.setContentView(dialogView);

        //设置底部弹出行为
        View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            com.google.android.material.bottomsheet.BottomSheetBehavior<View> behavior =
                com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheet);
            behavior.setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED);
        }

        //获取视图元素
        ImageView ivClose = dialogView.findViewById(R.id.iv_close);
        TextView tvNickname = dialogView.findViewById(R.id.tv_nickname);
        LinearLayout llNameSection = dialogView.findViewById(R.id.ll_name_section);
        TextView tvNameValue = dialogView.findViewById(R.id.tv_name_value);
        TextView tvDouyinIdValue = dialogView.findViewById(R.id.tv_douyin_id_value);
        ImageView ivCopy = dialogView.findViewById(R.id.iv_copy);
        LinearLayout llSpecialFollow = dialogView.findViewById(R.id.ll_special_follow);
        Switch switchSpecialFollow = dialogView.findViewById(R.id.switch_special_follow);
        LinearLayout llRemark = dialogView.findViewById(R.id.ll_remark);
        LinearLayout llUnfollow = dialogView.findViewById(R.id.ll_unfollow);

        //设置用户信息显示
        String nickname = user.getNickname() != null ? user.getNickname() : "";
        String remark = user.getRemark() != null && !user.getRemark().isEmpty() ? user.getRemark() : null;
        String douyinId = user.getId();

        //判断有无备注
        if (remark != null) {
            //第一行显示备注
            tvNickname.setText(remark);
            //第二行显示昵称|抖音号
            llNameSection.setVisibility(View.VISIBLE);
            tvNameValue.setText(nickname);
        } else {
            //第一行显示昵称
            tvNickname.setText(nickname);
            //第二行只显示抖音号
            llNameSection.setVisibility(View.GONE);
        }
        tvDouyinIdValue.setText(douyinId);

        //关闭按钮
        ivClose.setOnClickListener(v -> dialog.dismiss());

        //复制抖音号
        ivCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("抖音号", douyinId);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "已复制", Toast.LENGTH_SHORT).show();
        });

        //设置特别关注开关状态
        switchSpecialFollow.setChecked(user.isSpecialFollowed());

        //特别关注选项
        llSpecialFollow.setOnClickListener(v -> {
            boolean newState = !switchSpecialFollow.isChecked();
            switchSpecialFollow.setChecked(newState);
            userViewModel.setSpecialFollow(user, newState);
            String message = newState ? "已设为特别关注" : "已取消特别关注";
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });

        switchSpecialFollow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            userViewModel.setSpecialFollow(user, isChecked);
            String message = isChecked ? "已设为特别关注" : "已取消特别关注";
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });

        //设置备注选项
        llRemark.setOnClickListener(v -> {
            showRemarkDialog(user);
            dialog.dismiss();
        });

        //取消关注选项
        llUnfollow.setOnClickListener(v -> {
            if (user.isSpecialFollowed()) {
                dialog.dismiss();
                showSpecialUnfollowDialog(user);
            } else {
                performUnfollow(user, "已取消关注: " + user.getNickname());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showSpecialUnfollowDialog(User user) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_special_unfollow, null);
        com.google.android.material.bottomsheet.BottomSheetDialog dialog =
                new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        dialog.setContentView(dialogView);

        View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            com.google.android.material.bottomsheet.BottomSheetBehavior<View> behavior =
                    com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheet);
            behavior.setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED);
        }

        TextView tvConfirm = dialogView.findViewById(R.id.tv_confirm);
        TextView tvCancel = dialogView.findViewById(R.id.tv_cancel);

        tvConfirm.setOnClickListener(v -> {
            performUnfollow(user, "已取消特别关注: " + user.getNickname());
            dialog.dismiss();
        });

        tvCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void performUnfollow(User user, String toastMessage) {
        userViewModel.unfollowUser(user);
        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    //设置备注对话框
    private void showRemarkDialog(User user) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_remark, null);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        //获取视图元素
        TextView tvNameLabel = dialogView.findViewById(R.id.tv_name_label);
        TextView tvNameValue = dialogView.findViewById(R.id.tv_name_value);
        EditText etRemark = dialogView.findViewById(R.id.et_remark);
        ImageView ivClear = dialogView.findViewById(R.id.iv_clear);
        TextView tvCancel = dialogView.findViewById(R.id.tv_cancel);
        TextView tvConfirm = dialogView.findViewById(R.id.tv_confirm);

        String nickname = user.getNickname() != null ? user.getNickname() : "";
        String remark = user.getRemark() != null && !user.getRemark().isEmpty() ? user.getRemark() : null;

        View inputContainer = dialogView.findViewById(R.id.ll_input_container);

        if (remark != null) {
            //有备注：显示"名字: 昵称"，输入框显示备注内容
            tvNameLabel.setVisibility(View.VISIBLE);
            tvNameValue.setVisibility(View.VISIBLE);
            tvNameValue.setText(nickname);
            etRemark.setText(remark);
            // 调整输入框位置：在名字下方
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) inputContainer.getLayoutParams();
            params.topToBottom = R.id.tv_name_value;
            params.topToTop = ConstraintLayout.LayoutParams.UNSET;
            inputContainer.setLayoutParams(params);
        } else {
            //没有备注：不显示名字，输入框预填充昵称
            tvNameLabel.setVisibility(View.GONE);
            tvNameValue.setVisibility(View.GONE);
            etRemark.setText(nickname);
            etRemark.setSelection(nickname.length());
        }

        //清除按钮显示/隐藏
        etRemark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ivClear.setVisibility(!s.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //初始状态：如果有文本就显示清除按钮
        ivClear.setVisibility(!etRemark.getText().isEmpty() ? View.VISIBLE : View.GONE);

        //清除按钮点击事件
        ivClear.setOnClickListener(v -> {
            etRemark.setText("");
            etRemark.requestFocus();
        });

        //取消按钮
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        //确认按钮
        tvConfirm.setOnClickListener(v -> {
            String newRemark = etRemark.getText().toString().trim();
            userViewModel.setRemark(user, newRemark);

            Toast.makeText(getContext(), "备注已设置", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private User getUserAt(int position) {
        if (position < 0 || position >= displayedUsers.size()) {
            return null;
        }
        return displayedUsers.get(position);
    }
}
