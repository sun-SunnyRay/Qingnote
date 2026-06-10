# Tasks.org 完全代码复用方案

## 目标
将原版 Tasks.org 的代码**完全复用**到 QingNote 中，不重写任何 UI/ViewModel/Service 逻辑，只做最小化的适配修改。

## 核心思路
把 Tasks.org 作为一个**子模块**引入 QingNote 项目，共享数据库，通过 Gradle 多模块构建。

---

## 方案：多模块集成

### 项目结构
```
QingNote/
├── app/                          # QingNote 主模块（Notes + 设置 + 日历 + 入口）
├── tasks-app/                    # 从 tasks/app 复制，去掉 MainActivity/Application
├── tasks-kmp/                    # 从 tasks/kmp 复制（共享逻辑）
├── tasks-data/                   # 从 tasks/data 复制（Entity/DAO/Database）
├── gradle/
└── settings.gradle.kts
```

### 步骤

#### 第一步：复制模块
1. 复制 `tasks/data/` → `QingNote/tasks-data/`
2. 复制 `tasks/kmp/` → `QingNote/tasks-kmp/`（只保留 `jvmCommonMain` 和 `commonMain`）
3. 复制 `tasks/app/src/main/java/` 中需要的包 → `QingNote/tasks-app/`

#### 第二步：去掉不需要的
从 tasks-app 中删除：
- `MainActivity` / `TasksApplication`（用 QingNote 的）
- `billing/*`（订阅）
- `caldav/*` 的网络同步部分（保留 Filter、Local list）
- `gtasks/*`、`microsoft/*`、`etebase/*`、`opentasks/*`
- `auth/*`（SignInActivity）
- `feed/*`（博客）
- `widget/*`（小部件）
- `backup/*`（备份恢复）
- `tasker/*` / `locale/*`
- `sync/*`（同步适配器）

保留：
- `compose/edit/*`（TaskEditScreen 及所有 Row）
- `compose/home/*`（HomeScreen）
- `compose/drawer/*`（TaskListDrawer）
- `compose/pickers/*`（DatePickerShortcuts、TimeShortcuts）
- `compose/sort/*`（SortSettingsContent）
- `ui/*`（TaskEditViewModel、TaskListViewModel、所有 ControlSet）
- `service/*`（TaskCompleter、TaskDeleter、TaskMover）
- `repeats/*`（RepeatTaskHelper、RepeatRuleToString、BasicRecurrenceDialog、CustomRecurrenceActivity）
- `notifications/*`（NotificationManager、Notifier、NotificationWork）
- `jobs/*`（WorkManager jobs）
- `dialogs/*`（StartDatePicker、DateTimePicker、SortSettingsActivity、ColorPicker）
- `tags/*`（TagPickerActivity、TagPickerViewModel）
- `location/*`（LocationPickerActivity、LocationService、GeofenceApi）
- `preferences/*`（Preferences、TasksPreferences — 仅 task 相关）
- `filters/*`（Filter、FilterProvider、CaldavFilter、SearchFilter）
- `data/*`（TaskSaver、TaskContainer、各种扩展函数）
- `themes/*`（TasksTheme、ColorProvider — 适配 SaltTheme）
- `markdown/*`（MarkdownProvider）
- `time/*`（DateTime、DateTimeUtils2）
- `intents/*`（TaskIntents）
- `files/*`（FileHelper）
- `calendars/*`（CalendarEventProvider、CalendarHelper）
- `activities/*`（DateAndTimePickerActivity、FilterSettingsActivity、NavigationDrawerCustomization）

#### 第三步：适配修改

##### 3.1 Database 共享
- `tasks-data` 模块的 `Database` 类改为提供 DAO 接口
- `app` 模块的 `AppDatabase` 继承/包含 tasks-data 的所有 Entity 和 DAO
- 或者：tasks-data 独立一个 `TasksDatabase`，通过 Hilt 提供，与 QingNote 的 `AppDatabase` 共存（两个 .db 文件）

##### 3.2 依赖注入
- tasks-app 中的 Hilt Module 搬到 QingNote 的 app 模块
- 提供所有 tasks 需要的依赖（Context、Preferences、DAO 等）
- 去掉 Firebase、Inventory、SyncAdapters 等，替换为空实现

##### 3.3 导航入口
- QingNote 的 `AllNotePage` Tasks tab 直接嵌入 tasks-app 的 `HomeScreen` Composable
- FAB 点击调用 tasks-app 的 `TaskEditFragment`（或改为 Composable 入口）
- 设置页面嵌入 tasks-app 的 `MainSettingsComposeFragment`

##### 3.4 Theme 适配
- `TasksTheme` / `TasksSettingsTheme` 改为读取 QingNote 的 SaltTheme 颜色
- 或者在 tasks 入口外层包装 `MaterialTheme(colorScheme = saltToMaterial(SaltTheme.colors))`

##### 3.5 字符串资源
- 保留 tasks-kmp 的 `composeResources/`（Compose MP Resources）
- 在 app 的 `build.gradle.kts` 中添加 `org.jetbrains.compose` 插件
- 或者把所有 `Res.string.*` 批量转换为 `R.string.*`

##### 3.6 空实现替换
创建以下空实现/stub：
```kotlin
// Firebase stub
class Firebase { fun logEvent(vararg args: Any) {} fun reportException(e: Exception) {} }

// Inventory stub (no billing)
class Inventory { val hasPro = true; fun purchasedThemes() = true }

// SyncAdapters stub
class SyncAdapters { fun sync(source: Any) {} }

// RefreshBroadcaster stub
class RefreshBroadcaster { fun broadcastRefresh() {} fun broadcastTaskCompleted(ids: List<Long>, oldDueDate: Long? = null) {} }

// SoundPlayer stub
class SoundPlayer { fun playCompletionSound() {} }
```

---

## 工作量估算

| 步骤 | 文件数 | 工作量 |
|------|--------|--------|
| 复制模块 | ~300 | 机械操作 |
| 删除不需要的 | ~100 | 删文件 |
| 空实现 stub | ~15 | 简单 |
| Hilt Module 适配 | ~5 | 中等 |
| Database 共享 | ~3 | 复杂 |
| Theme 适配 | ~5 | 中等 |
| 导航入口 | ~3 | 中等 |
| 编译修错 | ~50 | 大量（最耗时） |

**总计：约 200 个文件需要搬迁，50+ 处编译错误需要修复**
**预计耗时：需要 3-5 次完整对话**

---

## 风险

1. **KMP 依赖**：tasks-kmp 依赖 `kotlinx-collections-immutable`、`kermit`、`ical4j`、`kotlinx-datetime` 等，需要全部加入 gradle
2. **Fragment 机制**：原版 TaskEditScreen 内嵌 `AndroidFragment<*>`，QingNote 需要支持 Fragment（添加 `androidx.fragment:fragment-compose` 依赖）
3. **Compose MP Resources**：需要 `org.jetbrains.compose` 插件，可能与现有 Compose 版本冲突
4. **编译时间**：多模块 + KMP 会显著增加编译时间
5. **APK 体积**：从 ~5MB 增加到 ~15MB+

---

## 替代方案（推荐）

如果不想引入多模块复杂度，可以：

**方案 B：单模块，逐文件复制 + 适配**

1. 把 tasks-app 中需要的 ~150 个 .kt 文件逐个复制到 `com.ldlywt.note.tasks.*` 包下
2. 批量替换包名：`org.tasks.*` → `com.ldlywt.note.tasks.*`，`com.todoroo.*` → `com.ldlywt.note.tasks.*`
3. 逐个解决编译错误（主要是删除对 Firebase/Billing/Sync 的引用）
4. 保留所有 UI Composable 原样

这个方案更简单但更繁琐，好处是不需要多模块构建。

---

## 我的建议

鉴于当前已经有了可工作的版本（Entity/DAO/ViewModel/UI 都已实现），建议：

1. **先打包测试当前版本**，记录所有"和原版不一样"的具体行为
2. **针对每个差异**，从原版复制对应的 Composable/逻辑替换当前实现
3. 这样可以**增量式**地接近原版，而不是一次性推翻重来

如果你坚持完全复用，告诉我选方案 A（多模块）还是方案 B（单模块逐文件），我开始执行。
