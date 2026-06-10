# QingNote 项目上下文摘要

## 项目路径
- QingNote 主项目：`C:\Users\qingguang\dev\QingNote\QingNote`
- 原版 Tasks.org：`C:\Users\qingguang\dev\QingNote\tasks`

## 当前状态
- 项目文件夹已从 `IdeaMemo-master` 重命名为 `QingNote`
- 当前包名：`com.ldlywt.note`（需要改为 `com.qingguang.qingnote`）
- 当前 applicationId：`com.ldlywt.note`
- 数据库名：`ssndb`，当前 version = 5
- 编译状态：`assembleRelease` BUILD SUCCESSFUL
- APK 输出：`IdeaMemo-release-1.1.1.apk`（约 4.8MB）

## 已完成的工作

### 数据层（在 `com.ldlywt.note.tasks.data.*` 下）
已创建但将被原版代码替换：
- 12 个 Entity：Task, Alarm, Tag, TagData, Place, Geofence, CaldavAccount, CaldavCalendar, CaldavTask, UserActivity, TaskAttachment, Attachment, Notification, Filter, TaskListMetadata
- 8 个 DAO：TasksDao, AlarmDao, TagDao, TagDataDao, LocationDao, CaldavDao, UserActivityDao, TaskAttachmentDao, NotificationDao
- 数据库 MIGRATION_4_5（创建所有新表）

### 核心服务（在 `com.ldlywt.note.tasks.service.*` 下）
- TaskCompleter, TaskDeleter, AlarmScheduler, AlarmReceiver, BootReceiver

### 重复规则（在 `com.ldlywt.note.tasks.repeats.*` 下）
- RecurrenceUtils, RepeatRuleToString

### ViewModel（在 `com.ldlywt.note.tasks.ui.*` 下）
- TaskEditViewModel, TaskListViewModel, DrawerViewModel

### UI Composable（在 `com.ldlywt.note.tasks.ui.compose.*` 下）
- TaskEditScreen（完整编辑界面 + DueDateBottomSheet + StartDateBottomSheet + RepeatPickerDialog + CustomRecurrenceDialog + AddAlarmOptionsDialog + TagPickerDialog）
- TaskListScreen（任务列表 + 滑动删除）
- TaskDrawer（侧边栏）

### QingNote 原有功能（保留不动）
- Notes 功能：AllNotePage, NoteCard, ChatInputDialog, SearchPage, TagListPage 等
- 日历页面：CalenderPage（已集成显示 tasks）
- 设置页面：HomeSettingsPage（已删除相册浏览和标签修正）
- 任务设置：TaskSettingsPage（5个分组）
- 主题：SaltTheme（亮色/暗色/动态颜色）
- 导航：HorizontalPager（Notes/Tasks 左右滑动切换）
- 底部栏：Home/Calendar/Settings

### 集成点
- `AllNotePage.kt`：Tasks tab 使用 `TaskListWithDrawer` + `TaskToolbar`
- `App.kt`：`Screen.TaskEdit` 路由指向 `com.ldlywt.note.tasks.ui.compose.TaskEditScreen`
- `CalenderPage.kt`：调用 `noteViewModel.getNewTasksOnSelectedDate()` 显示 tasks
- `NoteViewModel.kt`：注入了 `TasksDao`，提供 `getNewTasksOnSelectedDate()`
- `DatabaseModule.kt`：提供所有新 DAO 的 `@Provides`
- `AppDatabase.kt`：version 5，包含所有新 Entity
- `AndroidManifest.xml`：注册了 AlarmReceiver, BootReceiver，声明了日历权限

## 原版 Tasks.org 结构

### 模块
- `tasks/app/` — Android 主模块（Fragment + Compose + Hilt）
- `tasks/kmp/` — Kotlin Multiplatform 共享逻辑（jvmCommonMain + commonMain）
- `tasks/data/` — Room Entity/DAO（commonMain，KMP 格式）
- `tasks/composeApp/` — Desktop/多平台 Compose 入口（不需要）

### 关键依赖
- ical4j: 3.2.19（RRULE 解析）
- Room: 2.6.x
- Hilt
- kotlinx-collections-immutable
- kermit（日志，需替换为 android.util.Log）
- Compose MP Resources（`Res.string.*`）
- Fragment Compose（`androidx.fragment:fragment-compose`）

### 原版 KMP 特有注解（需转换）
- `CommonParcelize` → `@Parcelize`
- `CommonParcelable` → `Parcelable`
- `CommonRawValue` → `@RawValue`
- `co.touchlab.kermit.Logger` → `android.util.Log`

## 决策记录
| 决策 | 选择 |
|------|------|
| Theme | A — 混用，Tasks 内部用 MaterialTheme，外层包装适配 |
| List 概念 | A — 保留完整 CaldavCalendar 本地版 |
| 系统日历 | 不调用系统日历 API，但在 QingNote 内部日历显示 task |
| 字符串资源 | A — 接入 Compose MP Resources 插件 |
| 旧数据 | B — 清空重建 |
| 位置功能 | 删除（不申请定位权限） |
| 计时器 | 删除 |
| 备份/云服务/插件/高级/帮助反馈 | 删除 |

## 应用图标
- 源文件：`C:\Users\qingguang\dev\QingNote\应用图标.jpg`
- 已裁剪去白边，生成 5 个 mipmap 尺寸（48/72/96/144/192）
- 应用名：`晴 note`（在 strings.xml 中）
