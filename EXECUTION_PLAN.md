# 详细执行方案：多模块完全复用

## 总目标
将原版 Tasks.org 的代码完全复用到 QingNote 中，作为多模块项目构建。

---

## 阶段 1：包名更改 + 基础重构

### 1.1 settings.gradle.kts
```kotlin
rootProject.name = "QingNote"
include(":app")
include(":tasks-data")
include(":tasks-kmp")
```

### 1.2 app/build.gradle.kts
- `namespace = "com.qingguang.qingnote"`
- `applicationId = "com.qingguang.qingnote"`
- 输出文件名改为 `QingNote-${variant.baseName}-${variant.versionName}.apk`
- 添加依赖：`implementation(project(":tasks-data"))`, `implementation(project(":tasks-kmp"))`

### 1.3 包名批量替换
所有 `app/src/main/java/com/ldlywt/note/` 下的文件：
- `package com.ldlywt.note` → `package com.qingguang.qingnote`
- `import com.ldlywt.note` → `import com.qingguang.qingnote`
- 文件夹结构：`com/ldlywt/note/` → `com/qingguang/qingnote/`

### 1.4 AndroidManifest.xml
- `android:name=".App"` 保持（相对路径）
- Provider authorities：`${applicationId}.provider`

### 1.5 资源引用
- `com.ldlywt.note.R` → `com.qingguang.qingnote.R`（自动，因为 namespace 改了）

### 1.6 验证
- `./gradlew assembleRelease` 编译通过

---

## 阶段 2：创建 tasks-data 模块

### 2.1 目录结构
```
QingNote/tasks-data/
├── build.gradle.kts
└── src/main/
    ├── AndroidManifest.xml (空，只有 <manifest/>)
    └── kotlin/org/tasks/data/
        ├── entity/
        │   ├── Task.kt
        │   ├── Alarm.kt
        │   ├── Tag.kt
        │   ├── TagData.kt
        │   ├── Place.kt
        │   ├── Geofence.kt
        │   ├── CaldavAccount.kt
        │   ├── CaldavCalendar.kt
        │   ├── CaldavTask.kt
        │   ├── UserActivity.kt
        │   ├── TaskAttachment.kt
        │   ├── Attachment.kt
        │   ├── Notification.kt
        │   ├── Filter.kt
        │   ├── TaskListMetadata.kt
        │   └── Principal.kt (可选)
        ├── dao/
        │   ├── TaskDao.kt
        │   ├── AlarmDao.kt
        │   ├── TagDao.kt
        │   ├── TagDataDao.kt
        │   ├── LocationDao.kt
        │   ├── CaldavDao.kt
        │   ├── NotificationDao.kt
        │   ├── UserActivityDao.kt
        │   ├── TaskAttachmentDao.kt
        │   ├── DeletionDao.kt
        │   ├── FilterDao.kt
        │   ├── GoogleTaskDao.kt
        │   ├── CompletionDao.kt
        │   └── TaskListMetadataDao.kt
        ├── db/
        │   ├── Table.kt
        │   ├── Database.kt (Room @Database 定义)
        │   ├── DbUtils.kt
        │   └── SuspendDbUtils.kt
        └── *.kt (UUIDHelper, TaskContainer, Location, 扩展函数等)
```

### 2.2 从原版复制的文件
源：`C:\Users\qingguang\dev\QingNote\tasks\data\src\commonMain\kotlin\org\tasks\data\`
目标：`QingNote/tasks-data/src/main/kotlin/org/tasks/data/`

### 2.3 KMP → Android 转换规则
| 原版 | 替换为 |
|------|--------|
| `import org.tasks.CommonParcelable` | `import android.os.Parcelable` |
| `import org.tasks.CommonParcelize` | `import kotlinx.parcelize.Parcelize` |
| `import org.tasks.CommonRawValue` | `import kotlinx.parcelize.RawValue` |
| `@CommonParcelize` | `@Parcelize` |
| `: CommonParcelable` | `: Parcelable` |
| `@CommonRawValue` | `@RawValue` |
| `import co.touchlab.kermit.Logger` | 删除，Logger 调用改为 `android.util.Log` |
| `import org.tasks.time.DateTimeUtils2` | 保留（在 tasks-kmp 中实现） |
| `import org.tasks.time.ONE_DAY` | 内联为 `24 * 60 * 60 * 1000L` 或在 tasks-kmp 中定义 |

### 2.4 build.gradle.kts
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
}

dependencies {
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}
```

---

## 阶段 3：创建 tasks-kmp 模块

### 3.1 目录结构
```
QingNote/tasks-kmp/
├── build.gradle.kts
└── src/main/kotlin/
    ├── com/todoroo/astrid/
    │   ├── alarms/
    │   │   ├── AlarmService.kt
    │   │   └── AlarmCalculator.kt
    │   ├── repeats/
    │   │   └── RepeatTaskHelper.kt
    │   ├── core/
    │   │   └── SortHelper.kt
    │   └── service/
    │       └── TaskCreator.kt
    └── org/tasks/
        ├── service/
        │   ├── TaskCompleter.kt
        │   ├── TaskDeleter.kt
        │   └── TaskMover.kt
        ├── data/
        │   ├── TaskSaver.kt
        │   ├── TaskContainer.kt
        │   └── 各种扩展函数.kt
        ├── repeats/
        │   └── RecurrenceUtils.kt
        ├── reminders/
        │   └── Random.kt
        ├── time/
        │   ├── DateTime.kt
        │   ├── DateTimeUtils2.kt
        │   └── 时间扩展函数.kt
        ├── filters/
        │   ├── Filter.kt
        │   ├── CaldavFilter.kt
        │   ├── TagFilter.kt
        │   ├── PlaceFilter.kt
        │   ├── SearchFilter.kt
        │   └── FilterProvider.kt
        ├── preferences/
        │   ├── QueryPreferences.kt
        │   └── AppPreferences.kt
        └── viewmodel/
            ├── TaskListViewModel.kt (base)
            ├── SortSettingsViewModel.kt
            └── DrawerViewModel.kt
```

### 3.2 从原版复制的文件
源：`C:\Users\qingguang\dev\QingNote\tasks\kmp\src\jvmCommonMain\kotlin\` 和 `commonMain\kotlin\`
目标：`QingNote/tasks-kmp/src/main/kotlin/`

### 3.3 build.gradle.kts
```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "org.tasks.kmp"
    compileSdk = 35
    defaultConfig { minSdk = 26 }
}

dependencies {
    implementation(project(":tasks-data"))
    implementation("org.mnode.ical4j:ical4j:3.2.19")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}
```

---

## 阶段 4：引入 tasks-app 核心代码到 app 模块

### 4.1 在 app/src/main/java/ 下创建 org/tasks/ 包
直接从 `C:\Users\qingguang\dev\QingNote\tasks\app\src\main\java\org\tasks\` 复制以下目录：

```
保留的目录（完整复制）：
├── compose/edit/          (~20 文件)
├── compose/home/          (~5 文件)
├── compose/pickers/       (~5 文件)
├── compose/sort/          (已在 tasks-kmp)
├── compose/settings/      (~10 文件)
├── compose/drawer/        (已在 tasks-kmp)
├── compose/components/    (~10 文件)
├── ui/                    (~15 文件：TaskEditViewModel, ControlSets)
├── tags/                  (TagPickerActivity, TagPickerViewModel)
├── dialogs/               (~10 文件：StartDatePicker, DateTimePicker, SortSettings, BasicRecurrence)
├── repeats/               (RepeatRuleToString, CustomRecurrenceActivity)
├── notifications/         (NotificationManager, Notifier, TelephonyManager, AudioManager)
├── jobs/                  (NotificationWork, AfterSaveWork)
├── preferences/           (Preferences + fragments/)
├── activities/            (DateAndTimePickerActivity)
├── files/                 (FileHelper)
├── calendars/             (CalendarEventProvider, CalendarHelper — 简化)
├── broadcast/             (RefreshBroadcaster — stub)
├── extensions/            (Context extensions)
├── themes/                (TasksTheme, ColorProvider, ThemeBase)
├── markdown/              (MarkdownProvider)
└── intents/               (TaskIntents)

同时复制 com/todoroo/astrid/ 下：
├── activity/              (TaskListFragment, TaskEditFragment, BeastModePreferences, MainActivityViewModel)
├── ui/                    (ReminderControlSet, StartDateControlSet)
├── tags/                  (TagsControlSet)
├── repeats/               (RepeatControlSet)
├── timers/                (TimerControlSet — 可删)
├── files/                 (FilesControlSet)
├── gcal/                  (GCalHelper — 简化)
└── service/               (TaskCreator — 已在 kmp)
```

### 4.2 删除的目录
```
不复制：
├── billing/
├── caldav/ (网络同步部分)
├── gtasks/
├── microsoft/
├── etebase/
├── opentasks/
├── auth/
├── feed/
├── widget/
├── backup/
├── tasker/
├── locale/
├── sync/
├── injection/ (用 QingNote 的 Hilt)
└── wear/
```

---

## 阶段 5：Stub + Hilt 适配

### 5.1 创建空实现
在 `app/src/main/java/org/tasks/` 下创建：
```kotlin
// analytics/Firebase.kt — 空实现
// billing/Inventory.kt — hasPro=true
// broadcast/ComposeRefreshBroadcaster.kt — 空 Flow
// audio/SoundPlayer.kt — 空
// scheduling/NotificationSchedulerIntentService.kt — 空
```

### 5.2 Hilt Module
创建 `org/tasks/injection/TasksModule.kt`：
- 提供 Preferences, TasksDatabase, 所有 DAO
- 提供 Firebase(stub), Inventory(stub), SoundPlayer(stub)
- 提供 RepeatRuleToString, TaskCompleter, TaskDeleter 等

### 5.3 Database
选项 A：合并为一个 Database（QingNote 的 Note 表 + Tasks 的所有表）
选项 B：两个独立 Database（推荐，互不干扰）

推荐选项 B：
- `QingNoteDatabase`：Note, Tag, NoteTagCrossRef, Comment, Reminder
- `TasksDatabase`：tasks 的所有表（原版的 Database 类直接复用）

---

## 阶段 6：UI 入口集成

### 6.1 AllNotePage Tasks tab
```kotlin
// Tasks tab 内容改为调用原版的 TaskListFragment 或 HomeScreen
@Composable
fun TasksTab() {
    val theme = TasksTheme(...)  // 桥接 SaltTheme → MaterialTheme
    AndroidFragment<TaskListFragment>(
        arguments = Bundle().apply { putParcelable(EXTRA_FILTER, MyTasksFilter()) }
    )
}
```

### 6.2 FAB 点击
```kotlin
// 创建新任务
navController.navigate(Screen.TaskEdit(taskId = 0))
// 路由到 TaskEditFragment 或 TaskEditScreen
```

### 6.3 日历集成
```kotlin
// CalenderPage 中查询 TasksDatabase 的 TaskDao
suspend fun getTasksOnDate(date: LocalDate): List<TaskContainer> {
    return tasksDatabase.taskDao().fetchTasksForDate(startOfDay, endOfDay)
}
```

---

## 阶段 7：编译修错

### 预计错误类型及解决方案
| 错误类型 | 数量 | 解决方案 |
|----------|------|----------|
| 缺少 billing/sync 类 | ~20 | 用 stub 替换 |
| KMP expect/actual | ~10 | 改为直接实现 |
| Compose MP Resources `Res.string.*` | ~100 | 添加插件或批量转 R.string |
| kermit Logger | ~30 | 替换为 Log.d |
| 版本冲突 | ~5 | 对齐 gradle 版本 |
| R 类引用 | ~20 | 确保 namespace 正确 |

### 字符串资源处理
原版的字符串在 `tasks/kmp/src/commonMain/composeResources/values/strings.xml` 和 `tasks/app/src/main/res/values/strings.xml`。

方案：将 tasks 的 strings.xml 合并到 QingNote 的 `app/src/main/res/values/strings.xml`（约 500 条），同时合并中文翻译。

---

## 执行命令参考

### 批量复制目录
```powershell
# 复制 tasks-data entity
Copy-Item -Recurse "C:\Users\qingguang\dev\QingNote\tasks\data\src\commonMain\kotlin\org\tasks\data\entity" "C:\Users\qingguang\dev\QingNote\QingNote\tasks-data\src\main\kotlin\org\tasks\data\entity"

# 复制 tasks-data dao
Copy-Item -Recurse "C:\Users\qingguang\dev\QingNote\tasks\data\src\commonMain\kotlin\org\tasks\data\dao" "C:\Users\qingguang\dev\QingNote\QingNote\tasks-data\src\main\kotlin\org\tasks\data\dao"
```

### 批量替换包名
```powershell
Get-ChildItem -Recurse -Filter "*.kt" | ForEach-Object {
    (Get-Content $_.FullName) -replace 'package com\.ldlywt\.note', 'package com.qingguang.qingnote' -replace 'import com\.ldlywt\.note', 'import com.qingguang.qingnote' | Set-Content $_.FullName
}
```

### 批量替换 KMP 注解
```powershell
Get-ChildItem -Recurse -Filter "*.kt" -Path "tasks-data" | ForEach-Object {
    (Get-Content $_.FullName) `
        -replace 'import org\.tasks\.CommonParcelable', 'import android.os.Parcelable' `
        -replace 'import org\.tasks\.CommonParcelize', 'import kotlinx.parcelize.Parcelize' `
        -replace '@CommonParcelize', '@Parcelize' `
        -replace ': CommonParcelable', ': Parcelable' `
    | Set-Content $_.FullName
}
```

---

## 注意事项
1. 每个阶段结束后必须 `./gradlew assembleRelease` 确认编译通过
2. 如果某个文件引入了太多不需要的依赖，先注释掉，后续再处理
3. 优先保证核心流程可用：创建任务 → 编辑 → 保存 → 列表显示 → 完成 → 提醒
4. 原版的 `R.string.*` 引用需要把对应字符串添加到 QingNote 的 strings.xml
5. Fragment 相关代码需要 `implementation("androidx.fragment:fragment-compose:1.8.5")`
