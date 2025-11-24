## 仿抖音关注列表

使用 Java + Android Jetpack 编写的抖音关注页，包括关注/取关、特别关注、备注、下拉刷新等核心交互，并结合 Room + MVVM 实现数据持久化。

## 功能亮点

- **多 Tab 结构**：`ViewPager2 + TabLayout` 管理互关 / 关注 / 粉丝 / 朋友四个页面，当前先实现了关注页。  
- **关注列表交互**  
  - 默认只展示 `已关注` 用户，取关后短暂保留在列表，刷新后才移除。  
  - 按钮状态、关注人数实时更新。  
  - 特别关注用户展示标志，且取关时弹出确认弹窗。  
- **更多选项窗口**：包含特别关注设置、备注设置、复制抖音号、取消关注等。  
- **下拉刷新**：`SwipeRefreshLayout` 触发重新过滤。  
- **数据持久化**：Room + LiveData + ViewModel 让 UI 与数据实时同步。

## 技术栈

- **语言**：Java 
- **UI**：ConstraintLayout、RecyclerView、TabLayout、ViewPager2、BottomSheetDialog、SwipeRefreshLayout  
- **架构**：MVVM（ViewModel + Repository + Room）  
- **数据**：Room Database、LiveData、ExecutorService

## 目录结构

```
com.example.douyin/
├── MainActivity.java                  // 主活动，管理TabLayout与ViewPager2
├── adapter/
│   └── TabPagerAdapter.java           // ViewPager2适配器，管理多个Fragment
│   └── UserAdapter.java               // RecyclerView适配器，展示用户列表
├── fragment/
│   ├── FollowFragment.java            // 关注页面Fragment
│   ├── MutualFollowFragment.java      // 互关页面Fragment
│   ├── FansFragment.java              // 粉丝页面Fragment
│   └── FriendsFragment.java           // 朋友页面Fragment
├── model/
│   ├── User.java                      // 用户实体类（包含id、昵称、头像等属性）
│   ├── UserDao.java                   // 数据访问接口，定义数据库操作方法
│   └── AppDatabase.java               // Room数据库类，管理数据访问
├── repository/
│   └── UserRepository.java            // 数据仓库，协调本地数据与ViewModel交互
└── viewmodel/
    └── UserViewModel.java             // 视图模型，管理UI数据与业务逻辑
```

## 快速开始

1. 克隆或下载本仓库，并用 **Android Studio** 打开。  
2. 同步 Gradle。若首次运行数据库为空，会自动插入示例用户。  
3. 连接模拟器或真机，运行 `app` 模块即可体验。
