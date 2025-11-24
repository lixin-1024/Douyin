package com.example.douyin;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.douyin.adapter.TabPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class MainActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TabPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupViewPager();
        setupTabLayout();
        setupBackButton();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }

    private void setupViewPager() {
        pagerAdapter = new TabPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1, false); //默认显示关注tab
    }

    private void setupTabLayout() {
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("互关");
                    break;
                case 1:
                    tab.setText("关注");
                    break;
                case 2:
                    tab.setText("粉丝");
                    break;
                case 3:
                    tab.setText("朋友");
                    break;
            }
        }).attach();
    }

    private void setupBackButton() {
        ivBack.setOnClickListener(v -> finish());
    }
}

