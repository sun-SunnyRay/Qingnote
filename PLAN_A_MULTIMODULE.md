# 方案 A：多模块完全复用计划

## 前置操作（你手动完成）
1. 将 `C:\Users\qingguang\dev\QingNote\IdeaMemo-master` 文件夹重命名为 `QingNote`
2. 在 IDE 中重新打开项目

## 阶段概览

| 阶段 | 内容 | 预计对话数 |
|------|------|-----------|
| 1 | 项目重构（包名、文件夹名） | 1 |
| 2 | 引入 tasks-data 模块 | 1 |
| 3 | 引入 tasks-kmp 模块 | 1 |
| 4 | 引入 tasks-app 核心代码 | 2 |
| 5 | 空实现 stub + Hilt 适配 | 1 |
| 6 | UI 入口集成 + Theme 适配 | 1 |
| 7 | 编译修错 + 测试 | 1-2 |

---

## 阶段 1：项目重构

### 1.1 文件夹重命名
- `IdeaMemo-master/` → `QingNote/`（你手动）

### 1.2 包名更改
- `com.ldlywt.note` → `com.qingguang.qingnote`
- 涉及：
  - `app/build.gradle.kts` 中的 `namespace` 和 `applicationId`
  - `AndroidManifest.xml` 中的包引用
  - 所有 .kt 文件的 `package` 声明和 `import`
  - `settings.gradle.kts` 中的 `rootProject.name`
  - Hilt 的 `@AndroidEntryPoint` 类
  - Room 的 `@Database` 类
  - `strings.xml` 中的 `app_name`

### 1.3 APK 输出名
- `build.gradle.kts` 中改为 `QingNote-${variant.baseName}-${variant.versionName}.apk`

---

## 阶段 2：引入 tasks-data 模块

### 2.1 创建模块
```
QingNote/
├── app/
├── tasks-data/
│   ├── build.gradle.kts
│   └── src/main/kotlin/org/tasks/data/
│       ├── entity/     (所有 Entity 原样复制)
│       ├── dao/        (所有 DAO 原样复制)
│       └── db/         (Database, Table, Field 等工具类)
```

### 2.2 build.gradle.kts
```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-parcelize")
}

android {
    namespace = "org.tasks.data"
    compileSdk = 35
    defaultConfig { minSdk = 26 }
}

dependencies {
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")
}
```

### 2.3 从原版复制
- `tasks/data/src/commonMain/kotlin/org/tasks/data/entity/*.kt` → 转为纯 Android（去掉 CommonParcelize）
- `tasks/data/src/commonMain/kotlin/org/tasks/data/dao/*.kt` → 原样
- `tasks/data/src/commonMain/kotlin/org/tasks/data/*.kt`（工具类：UUIDHelper, TaskContainer, 扩展函数等）

### 2.4 适配
- `CommonParcelize` → `@Parcelize`
- `CommonParcelable` → `Parcelable`
- `CommonRawValue` → `@RawValue`
- `co.touchlab.kermit.Logger` → `android.util.Log` 或空实现
- `org.tasks.data.db.Table` / `Field` → 保留（Room 不直接用，但 DAO 的 RawQuery 引用）

---

## 阶段 3：引入 tasks-kmp 模块

### 3.1 创建模块
```
QingNote/
├── tasks-kmp/
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│       ├── com/todoroo/astrid/
│       │   ├── alarms/AlarmService.kt, AlarmCalculator.kt
│       │   ├── repeats/RepeatTaskHelper.kt
│       │   └── core/SortHelper.kt
│       └── org/tasks/
│           ├── service/TaskCompleter.kt, TaskDeleter.kt, TaskMover.kt
│           ├── repeats/RecurrenceUtils.kt
│           ├── reminders/Random.kt
│           ├── time/DateTime.kt, DateTimeUtils2.kt
│           ├── filters/Filter.kt, CaldavFilter.kt, ...
│           ├── data/TaskSaver.kt, TaskContainer.kt
│           └── preferences/QueryPreferences.kt, AppPreferences.kt
```

### 3.2 依赖
```kotlin
dependencies {
    implementation(project(":tasks-data"))
    implementation("org.mnode.ical4j:ical4j:3.2.19")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}
```

### 3.3 适配
- 去掉 `expect/actual` 声明（KMP 特有），改为直接 Android 实现
- `co.touchlab.kermit.Logger` → `android.util.Log`
- `org.tasks.time.DateTimeUtils2.currentTimeMillis()` → `System.currentTimeMillis()`
- 保留所有业务逻辑原样

---

## 阶段 4：引入 tasks-app 核心代码

### 4.1 复制到 app 模块
在 `app/src/main/java/` 下创建 `org/tasks/` 包，复制：

```
org/tasks/
├── compose/
│   ├── edit/          (TaskEditScreen, TitleRow, DueDateRow, PriorityRow, DescriptionRow,
│   │                   RepeatRow, StartDateRow, AlarmRow, SubtaskRow, ListRow, TagsRow,
│   │                   FilesRow, InfoRow, LocationRow, CommentsRow)
│   ├── home/          (HomeScreen)
│   ├── drawer/        (TaskListDrawer, DrawerItem)
│   ├── pickers/       (DatePickerBottomSheet, DatePickerShortcuts, TimeShortcuts, TimePickerDialog)
│   ├── sort/          (SortSettingsContent, BottomSheetContent)
│   └── settings/      (TaskDefaultsScreen, TaskListScreen, TaskEditScreen, DateAndTimeScreen, NavigationDrawerScreen)
├── ui/
│   ├── TaskEditViewModel.kt
│   ├── TaskListViewModel.kt (base)
│   ├── TaskEditControlFragment.kt
│   ├── SubtaskControlSet.kt
│   ├── CalendarControlSet.kt
│   ├── LocationControlSet.kt
│   ├── StartDateControlSet.kt
│   ├── ReminderControlSet.kt
│   └── ...
├── tags/              (TagPickerActivity, TagPickerViewModel)
├── dialogs/           (StartDatePicker, DateTimePicker, SortSettingsActivity, BasicRecurrenceDialog)
├── repeats/           (RepeatRuleToString, CustomRecurrenceActivity)
├── notifications/     (NotificationManager, Notifier)
├── jobs/              (NotificationWork, AfterSaveWork)
├── location/          (LocationPickerActivity, LocationService)
├── preferences/       (Preferences, fragments/TaskDefaults, TaskListPreferences, etc.)
├── activities/        (DateAndTimePickerActivity, FilterSettingsActivity)
├── files/             (FileHelper)
├── calendars/         (CalendarEventProvider, CalendarHelper)
├── broadcast/         (RefreshBroadcaster)
└── extensions/        (Context extensions)
```

### 4.2 不复制的
- `billing/*`, `sync/*`, `auth/*`, `feed/*`, `widget/*`, `backup/*`, `tasker/*`
- `MainActivity.kt`（用 QingNote 的）
- `TasksApplication.kt`（用 QingNote 的 App.kt）

---

## 阶段 5：空实现 Stub + Hilt 适配

### 5.1 创建 stub
```kotlin
// org/tasks/analytics/Firebase.kt
class Firebase @Inject constructor() {
    fun logEvent(vararg args: Any) {}
    fun reportException(e: Exception) {}
    fun completeTask(source: String) {}
    val subscribeCooldown = false
}

// org/tasks/billing/Inventory.kt
class Inventory @Inject constructor() {
    val hasPro = true
    val hasTasksAccount = false
    fun purchasedThemes() = true
    val begForMoney = false
}

// org/tasks/broadcast/RefreshBroadcaster.kt (简化)
class RefreshBroadcaster @Inject constructor() {
    fun broadcastRefresh() {}
    fun broadcastTaskCompleted(ids: List<Long>, oldDueDate: Long? = null) {}
}

// org/tasks/audio/SoundPlayer.kt
class SoundPlayer @Inject constructor() {
    fun playCompletionSound() {}
}
```

### 5.2 Hilt Module
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object TasksModule {
    @Provides @Singleton
    fun providePreferences(@ApplicationContext context: Context): Preferences = Preferences(context)
    
    @Provides @Singleton
    fun provideTasksDatabase(@ApplicationContext context: Context): TasksDatabase = ...
    
    // 提供所有 DAO
    @Provides fun provideTaskDao(db: TasksDatabase): TaskDao = db.taskDao()
    @Provides fun provideAlarmDao(db: TasksDatabase): AlarmDao = db.alarmDao()
    // ... 其他 DAO
}
```

---

## 阶段 6：UI 入口集成

### 6.1 QingNote 的 AllNotePage
```kotlin
// Tasks tab 内容
@Composable
fun TasksTabContent(navController: NavHostController) {
    // 直接使用原版的 HomeScreen 或 TaskListFragment
    MaterialTheme(colorScheme = saltToMaterialColorScheme()) {
        // 原版的任务列表 + drawer
    }
}
```

### 6.2 Theme 桥接
```kotlin
@Composable
fun saltToMaterialColorScheme(): ColorScheme {
    val salt = SaltTheme.colors
    return if (isSystemInDarkTheme()) darkColorScheme(
        primary = salt.highlight,
        onPrimary = Color.White,
        surface = salt.background,
        onSurface = salt.text,
        // ...
    ) else lightColorScheme(
        primary = salt.highlight,
        onPrimary = Color.White,
        surface = salt.background,
        onSurface = salt.text,
        // ...
    )
}
```

### 6.3 Fragment 支持
```kotlin
// build.gradle.kts
implementation("androidx.fragment:fragment-compose:1.8.5")
```

---

## 阶段 7：编译修错

预计的主要错误类型：
1. **缺少类**：被删除的 billing/sync 相关 → 用 stub 替换
2. **包名不匹配**：`org.tasks.R` → 需要在 tasks-app 代码中引用正确的 R 类
3. **KMP 特有 API**：`expect/actual`、`CommonParcelize` → 转为 Android 版本
4. **Compose MP Resources**：`Res.string.*` → 转为 `R.string.*` 或引入插件
5. **版本冲突**：依赖版本对齐

---

## Gradle 配置

### settings.gradle.kts
```kotlin
rootProject.name = "QingNote"
include(":app")
include(":tasks-data")
include(":tasks-kmp")
```

### app/build.gradle.kts 新增
```kotlin
dependencies {
    implementation(project(":tasks-data"))
    implementation(project(":tasks-kmp"))
    // tasks 依赖
    implementation("org.mnode.ical4j:ical4j:3.2.19")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")
    implementation("androidx.fragment:fragment-compose:1.8.5")
    implementation("com.google.android.gms:play-services-location:21.3.0")
}
```

---

## 执行顺序

1. **你先手动**：重命名文件夹 `IdeaMemo-master` → `QingNote`
2. **下次对话**：我执行阶段 1（包名更改）+ 阶段 2（tasks-data 模块）
3. **再下次**：阶段 3 + 4（tasks-kmp + tasks-app 核心代码）
4. **再下次**：阶段 5 + 6 + 7（stub + 集成 + 修错）

每个阶段结束后确保能编译通过。

---

## 与当前实现的关系

当前 `com.ldlywt.note.tasks.*` 下的代码（我之前写的简化版）将被**完全替换**为原版代码。保留的只有：
- QingNote 的 Notes 功能（`com.qingguang.qingnote.ui.page.home/input/search/tag/settings` 等）
- QingNote 的日历页面（`CalenderPage.kt`，但会改为调用原版 DAO 查询 tasks）
- QingNote 的数据库（`AppDatabase` 会被合并或替换为原版的 `Database`）
- QingNote 的主题/导航/底部栏
