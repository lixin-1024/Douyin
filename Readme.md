## 仿抖音关注列表

一个使用 **Java + Android Jetpack** 编写的抖音关注页项目，覆盖关注/取关、特别关注、备注、下拉刷新等核心交互，并结合 Room + MVVM 实现数据持久化。

## 功能亮点

- **多 Tab 结构**：`ViewPager2 + TabLayout` 管理互关 / 关注 / 粉丝 / 朋友四个页面，当前主要实现关注页。  
- **关注列表交互**  
  - 默认只展示 `已关注` 用户，取关后短暂保留在列表，刷新后才移除。  
  - 按钮状态、关注人数实时更新。  
  - 特别关注用户展示标志，且取关时弹出确认弹窗。  
- **更多动作 Bottom Sheet**：包含特别关注设置、备注设置、复制抖音号、取消关注等。  
- **备注弹窗**：支持无备注/已有备注两种态、清空按钮。  
- **下拉刷新**：`SwipeRefreshLayout` 触发重新过滤。  
- **数据持久化**：Room + LiveData + ViewModel 让 UI 与数据实时同步，并带有示例数据初始化。

## 技术栈

- **语言**：Java 8  
- **UI**：ConstraintLayout、RecyclerView、TabLayout、ViewPager2、BottomSheetDialog、SwipeRefreshLayout  
- **架构**：MVVM（ViewModel + Repository + Room）  
- **数据**：Room Database、LiveData、ExecutorService

## 目录结构

```
app/src/main/java/com/example/douyin/
├── adapter/           # RecyclerView、ViewPager2 适配器
├── fragment/          # 互关、关注、粉丝、朋友等 Fragment
├── model/             # Room 实体、DAO、Database
├── repository/        # 数据仓库
├── viewmodel/         # UserViewModel
└── MainActivity.java  # 管理 Tab 与 ViewPager2
```

## 快速开始

1. 克隆或下载本仓库，并用 **Android Studio (Giraffe+)** 打开。  
2. 同步 Gradle。若首次运行数据库为空，`FollowFragment` 会自动插入示例用户。  
3. 连接模拟器或真机，运行 `app` 模块即可体验。
