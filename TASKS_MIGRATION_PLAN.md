# Tasks.org 模块迁移方案

## 目标
将原版 Tasks.org（`C:\Users\qingguang\dev\QingNote\tasks`）的完整 Tasks 功能集成到 QingNote 中，**完美复刻**所有功能（不简化），仅不同点是共用 QingNote 现有的 Room 数据库实例（同一个 `.db` 文件），其他代码、UI、ViewModel、Service 等全部搬迁。

## 原则
- **共用数据库**：复用 QingNote 的 `AppDatabase`，将 tasks.org 的所有 Entity/DAO 加入进去
- **代码复用**：直接拷贝 tasks.org 的源文件到 QingNote 对应包下，尽量保持包结构
- **不简化**：包括 recurrence (iCal RRULE)、多种 Alarm 类型、Geofence、Subtask、Tag、List/Caldav filter、Comments、Attachments、Timer、Calendar 等
- **去掉同步/商业化**：不保留 CalDAV/Google Tasks/Microsoft/Tasks.org 订阅/Firebase/Billing/广告 相关代码（用户不需要备份云服务和插件）
- **UI 集成**：Task 编辑/列表页面使用 tasks.org 的 Compose UI 组件（内部使用 MaterialTheme），包装在 QingNote 的 `SaltTheme` 外层

---

## 阶段一：数据库迁移（共用一个 Room DB）

### 1.1 迁移的 Entity（从 tasks.org → QingNote）
全部放入 `com.ldlywt.note.db.tasks.entity` 包下：

- **Task**（替换当前的简版 Task，字段全量迁移）：
  - `_id`, `title`, `importance`, `dueDate`, `hideUntil`, `created`, `modified`, `completed`, `deleted`, `notes`, `estimatedSeconds`, `elapsedSeconds`, `timerStart`, `notificationFlags`, `lastNotified`, `recurrence`（iCal RRULE）, `repeat_from`, `calendarUri`, `remoteId`, `collapsed`, `parent`, `order`, `read_only`
- **Alarm**：多 Alarm 支持（`task`, `time`, `type`, `repeat`, `interval`）
  - 类型：`TYPE_DATE_TIME`, `TYPE_REL_START`, `TYPE_REL_END`, `TYPE_RANDOM`, `TYPE_SNOOZE`, `TYPE_GEO_ENTER`, `TYPE_GEO_EXIT`
- **Tag** + **TagData**（任务标签，与 Note 的 tag 分离，不冲突表名）
- **Place**（地点）
- **Geofence**（地理围栏）
- **UserActivity**（评论/活动）
- **Attachment** + **TaskAttachment**（附件，复用 QingNote 的 attachment 字段？待定，先独立）
- **Notification**（通知队列）
- **TaskListMetadata**（列表元数据）
- **Filter**（自定义筛选）
- **CaldavCalendar** / **CaldavTask** / **CaldavAccount**（需要用于 List/Category，保留本地 list 概念）
  - 只保留 `TYPE_LOCAL`，移除远程同步字段的使用

### 1.2 AppDatabase 整合
在 `com.ldlywt.note.db.AppDatabase` 中：
- 升级 `version = 5`
- 在 `@Database` 的 `entities` 数组中添加所有新 Entity
- 提供所有新 DAO 的 abstract getter
- 编写 `MIGRATION_4_5`：
  - `DROP TABLE Task`（旧 Task 表）后重建新 Task 表（或重命名旧表为 `Task_legacy`，迁移数据到新 `tasks` 表）
  - 创建 `alarms`, `tags`, `tagdata`, `places`, `geofences`, `userActivity`, `attachment`, `task_attachments`, `notification`, `filters`, `caldav_accounts`, `caldav_lists`, `caldav_tasks`, `task_list_metadata` 等表
- 数据迁移：读取旧 `Task` 表 → 按字段映射插入新 `tasks` 表
  - `task_id` → `_id`
  - `task_title` → `title`
  - `task_content` → `notes`
  - `is_completed` + `completion_date` → `completed`
  - `priority` (0-3) → `importance`（注意方向：QingNote 0=NONE,3=HIGH；tasks 0=HIGH,3=NONE，需翻转）
  - `due_date` → `dueDate`
  - `start_date` → `hideUntil`
  - `create_time` → `created`
  - `update_time` → `modified`
  - `reminder_time` > 0 → 插入一条 `alarms`（type=TYPE_DATE_TIME）
  - `category` → 自动创建/关联到本地 `caldav_lists` 记录
  - `task_tags`（逗号分隔）→ 拆分为多条 `tags` + `tagdata` 记录
  - `parent_task_id` → `parent`

### 1.3 DAO 迁移
全部放入 `com.ldlywt.note.db.tasks.dao`：
- **TaskDao**（继承原版，含复杂 RawQuery、递归 CTE、批量操作）
- **AlarmDao**
- **TagDao** / **TagDataDao**
- **LocationDao**（Place + Geofence）
- **UserActivityDao**
- **TaskAttachmentDao**
- **NotificationDao**
- **DeletionDao**
- **CaldavDao** / **GoogleTaskDao**（仅保留本地 list 查询部分，移除同步方法）
- **FilterDao**
- **TaskListMetadataDao**

---

## 阶段二：核心业务逻辑迁移

### 2.1 包结构
创建 `com.ldlywt.note.tasks` 作为 tasks.org 代码的根包：
```
com.ldlywt.note.tasks/
├── data/                # org.tasks.data (DAO, entity 已在 db.tasks 下)
├── service/             # TaskCompleter, TaskDeleter, TaskMover
├── repeats/             # RecurrenceUtils, RepeatRuleToString, BasicRecurrenceDialog, CustomRecurrenceActivity
├── reminders/           # AlarmService, AlarmCalculator, Random
├── notifications/       # NotificationManager, Notifier, NotificationWork
├── jobs/                # NotificationSchedulerJob, WorkManager 配置
├── location/            # GeofenceApi, LocationPickerActivity, LocationService
├── filters/             # Filter, FilterProvider, CaldavFilter, SearchFilter
├── ui/                  # TaskListViewModel, TaskEditViewModel, DrawerViewModel, SortSettingsViewModel
├── compose/             # HomeScreen, TaskListDrawer, TaskEditScreen, 所有 ControlSet/*Row
├── preferences/         # Preferences, TasksPreferences (仅保留 task 相关)
├── dialogs/             # DatePickerDialog, ColorPickerDialog, Linkify 等
├── markdown/            # MarkdownProvider, markwon 集成
├── themes/              # 移除，使用 SaltTheme（MaterialTheme 兼容层）
├── time/                # DateTime, DateTimeUtils2
└── intents/             # TaskIntents
```

### 2.2 要移除的原版功能（与目标无关）
- `billing/*` (订阅、Google Play Billing)
- `caldav/*` 的网络同步部分（保留 Filter、Local list）
- `gtasks/*` (Google Tasks 同步)
- `microsoft/*` (Microsoft sync)
- `etebase/*`
- `opentasks/*`
- `auth/*`（SignInActivity）
- `analytics/Firebase.kt`（改为空实现）
- `feed/*`（博客通知）
- `widget/*`（小部件）
- `backup/*`（备份恢复）
- `tasker/*` / `locale/*` 插件
- `BeastMode` 相关（可保留简化版）

### 2.3 核心逻辑搬迁
- **AlarmService**：同步任务的 alarms，使用 AlarmManager 精确调度
- **Notifier + NotificationManager**：构造/发送通知，支持 `nonstop`, `five_times`, `voice`
- **NotificationWork**（WorkManager Job）：重启后重新调度
- **AlarmCalculator**：根据 `TYPE_REL_START/END` 和 `recurrence` 计算下次触发时间
- **RepeatRuleToString**：将 RRULE 转为中文（需要翻译 `R.string.*` 相关字符串）
- **TaskCompleter**：完成任务时处理 recurrence（如果有则生成下次，不删除原任务）
- **GeofenceApi** + **LocationService**：基于位置的提醒
- **TaskMover**：跨 list 移动任务

### 2.4 依赖新增（`app/build.gradle.kts`）
```kotlin
implementation("org.mnode.ical4j:ical4j:4.0.0-rc5") // RRULE 解析
implementation("com.google.android.gms:play-services-location:21.3.0") // Geofence
implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")
implementation("co.touchlab:kermit:2.0.4") // 原版用的日志
// markwon 已有
// dagger.hilt 已有
// room 已有
// WorkManager 已有
```

---

## 阶段三：UI 迁移

### 3.1 Tasks 列表页
- 复用 `org.tasks.compose.home.HomeScreen`
- 去掉 `ListDetailPaneScaffold`（QingNote 没有多窗格），改为单列
- 去掉 `AndroidFragment<TaskListFragment>`，改为直接用 `TaskListScreen` Composable + LazyColumn
- 保留 `TaskListDrawer` 完整功能：
  - 搜索 FAB
  - 分组的 Filter（我的任务、今天、标签、地点、清单）
  - Header 折叠
  - 添加新 list/tag/filter/place
- 排序菜单：复用 `SortSheetContent` 完整实现
- 3 点菜单：复用原版 `TaskListToolbar` 的 Dropdown

### 3.2 Tasks 编辑页
- 复用 `org.tasks.compose.edit.TaskEditScreen`
- 去掉 `AndroidFragment<*>` 对 control set fragments 的依赖，改为直接调用 Composable
- 每个 Row 完整迁移：
  - **TitleRow**：标题 + 复选框 + Linkify + Markdown
  - **DueDateRow**：截止日期+时间选择，支持 "今天/明天/下周/...快捷项"
  - **StartDateRow**：开始日期
  - **PriorityRow**：4 级优先级
  - **DescriptionRow**：Markdown 描述
  - **RepeatRow**：完整 RRULE 选择（BasicRecurrenceDialog + CustomRecurrenceActivity）
  - **ReminderRow**：多 Alarm，选项：到期时、开始时、截止前 X 分钟、随机、自定义时间、地点进入/离开
  - **TagsRow**：TagPickerActivity
  - **ListRow**：列表选择
  - **LocationRow**：地点 + 地理围栏半径
  - **SubtaskRow**：子任务列表
  - **TimerRow**：开始/停止计时
  - **CalendarRow**：Android 日历事件（可选，用户没明确要）
  - **FilesRow**：附件
  - **InfoRow**：已创建/已修改/已完成日期

### 3.3 沙盒化 Theme
- 原版用 `TasksTheme` (MaterialTheme)，QingNote 用 `SaltTheme`
- 方案：`TasksTheme` 直接用当前的 MaterialColorScheme（由 QingNote 动态/静态主题生成）
- 在 Tasks 入口 Composable 外层包装：
  ```kotlin
  MaterialTheme(colorScheme = SaltTheme.colors.toColorScheme()) {
      HomeScreen(...)
  }
  ```
- 避免在 QingNote 现有页面引入 Material 主题冲突

### 3.4 字符串资源
- 复制 `tasks.kmp.generated.resources` 中常用 `Res.string.*` 到 QingNote 的 `strings.xml`
- 同时迁移 `values-zh-rCN/strings.xml` 和 `values-zh-rTW`（原版已有中文）
- 或保留 kmp 资源系统：将 `kmp/src/commonMain/composeResources` 拷贝到 `app/src/main/composeResources`（若使用 Compose Multiplatform Resources 插件）

---

## 阶段四：导航与入口

### 4.1 替换当前 Tasks 实现
当前的 `com.ldlywt.note.ui.page.task.*`：
- `TaskListPage.kt` → 移除，替换为对 `org.tasks.compose.home.HomeScreen` 的封装
- `TaskEditPage.kt` → 移除，替换为 `org.tasks.compose.edit.TaskEditScreen`
- `TaskViewModel.kt` → 移除，使用原版 `TaskListViewModel` + `TaskEditViewModel` + `DrawerViewModel`

### 4.2 `AllNotePage.kt` 中的 Tasks tab
- `TaskListWithDrawer` 改为调用迁移后的 `HomeScreen`（或直接内嵌 `TaskListContent` + `ModalNavigationDrawer` + `TaskListDrawer`）
- FAB 点击打开 `TaskEditScreen`（全屏 Dialog 或 Navigate 到新路由）
- 顶栏的 drawer icon / search / sort / 3 点菜单改为调用 `TaskListViewModel` 的对应方法

### 4.3 `Screen.kt` 路由
- `Screen.TaskEdit(taskId)` 参数改为传 `Task` 的 UUID 或 `_id`
- 新增 `Screen.TaskLocationPicker`, `Screen.TaskTagPicker`, `Screen.TaskListPicker`, `Screen.TaskSettings`

### 4.4 设置页 (`HomeSettingsPage`)
在"任务设置"子页中集成原版的：
- **外观**（LookAndFeel）：markdown 开关、打开上次列表
- **任务默认值**（TaskDefaults）：默认优先级、默认列表、默认标签、默认提醒、默认 RRULE、默认位置提醒
- **任务列表**（TaskListPreferences）：完成任务归到底部、删除前确认、分组折叠记忆
- **任务编辑**（TaskEditPreferences）：返回即保存、多行标题、linkify
- **通知**（Notifications）：通知声音、振动、免打扰、语音提醒
- **日期和时间**（DateAndTime）：周开始、时间格式、默认开始/截止/提醒时间
- **导航抽屉**（NavigationDrawer）：显示隐藏哪些 subheader
- **小部件**：删除
- **备份**：删除
- **高级**：删除
- **帮助和反馈**：删除

---

## 阶段五：细节与清理

### 5.1 图标
- 去掉白色外框（已处理）
- 也替换 `TasksIcon.kt` 中用到的图标资源（ic_tasks_* 等），拷贝到 `res/drawable/`

### 5.2 权限
`AndroidManifest.xml` 添加：
- `android.permission.ACCESS_FINE_LOCATION`（Geofence）
- `android.permission.ACCESS_BACKGROUND_LOCATION`
- `android.permission.SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM`（已有）
- `android.permission.POST_NOTIFICATIONS`（已有）
- `android.permission.READ_CALENDAR`, `WRITE_CALENDAR`（可选，若保留 CalendarRow）

### 5.3 测试与验证
- Room 迁移测试：旧 v4 数据升级到 v5 后所有原任务仍可见
- 提醒：设置提醒 → 关机重启 → 到时间仍能触发
- Recurrence：设置"每周二重复" → 完成一次 → 自动生成下次
- Geofence：到达某位置 → 触发通知
- 子任务、标签、列表、优先级、搜索、排序、分组 全部工作

---

## 工作量估算
- **Entity + DAO + Migration**：~30 文件，约 3000 行
- **核心逻辑（Service/Notifier/Repeat）**：~40 文件，约 5000 行
- **UI 组件**：~60 文件，约 8000 行
- **ViewModel**：~15 文件，约 2500 行
- **清理与集成**：修改 ~20 个 QingNote 现有文件

**总计：~165 新文件，~18000 行代码搬迁 + 3000 行本地改动**

## 分批实施建议
建议按阶段逐步合并，每阶段独立可编译可测试：

1. **PR 1**：数据库 Entity/DAO/Migration（仅数据层，无 UI 变更）
2. **PR 2**：核心服务迁移（AlarmService、Notifier、TaskCompleter、RepeatRuleToString）
3. **PR 3**：ViewModel 迁移
4. **PR 4**：TaskEditScreen + 所有 Row Composable
5. **PR 5**：TaskListScreen + TaskListDrawer
6. **PR 6**：设置页与导航集成
7. **PR 7**：字符串翻译与图标资源清理

---

## 风险点
1. **Room Migration 复杂**：v4 Task 表结构差异大，需仔细测试数据保留
2. **ical4j**：原版用自定义 `RecurrenceUtils.newRecur`，要确保新版本兼容
3. **Theme 冲突**：SaltTheme 与 MaterialTheme 并存的颜色/排版兼容
4. **KMP 资源**：原版用 `tasks.kmp.generated.resources`，如果不迁移整个 kmp 模块，需要把字符串都改写为 Android `R.string.*`
5. **依赖冲突**：tasks.org 依赖 `kermit`, `ical4j`, `play-services-location` 等，与 QingNote 已有依赖可能版本冲突
6. **Fragment-based Control Sets**：原版 TaskEditScreen 内嵌 `AndroidFragment<*>` 调用多个 Fragment（ReminderControlSet、LocationControlSet 等），需全部改写为纯 Composable，否则要保留 Fragment 机制

## 决策结果

| 决策 | 选择 |
|------|------|
| 1. Theme | A — 混用，Tasks 内部用 MaterialTheme，外层包装适配 |
| 2. List 概念 | A — 保留完整 CaldavCalendar 本地版 |
| 3. 系统日历 | 不要（不调用 Android 系统日历 API） |
| 4. 字符串资源 | A — 接入 Compose MP Resources 插件 |
| 5. 旧数据 | B — 清空重建 |

### 额外需求：QingNote 日历页显示 Task
- QingNote 已有日历页（`CalenderPage.kt`），已经调用 `noteViewModel.getTasksOnSelectedDate(date)` 显示当天任务
- 迁移后需要保持此功能：新 `tasks` 表的数据也要在日历页按 `dueDate` 或 `hideUntil`（开始日期）显示
- 不需要写入系统日历，只在 QingNote 内部日历视图中展示
