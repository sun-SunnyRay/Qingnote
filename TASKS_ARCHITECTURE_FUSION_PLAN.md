# QingNote / Tasks 架构级融合方案

## 1. 结论

当前融合方式是：

```text
IdeaMemo/QingNote 主体页面
└─ 手工嵌入 Tasks 数据层、后端逻辑、Compose UI
```

这种方式可以做出可用版，也能通过不断修补接近原版体验，但它不是一个真正统一的架构。原因是 Notes 和 Tasks 的数据模型、状态管理、权限、设置、导航、服务层来源都不同。继续在同一个页面里补丁式堆功能，会不断出现“已经能用，但和原版还有差距”的问题。

新的方向应该是：

```text
QingNote App Shell
├─ Notes Feature
└─ Tasks Feature
```

也就是说，QingNote 不再是“笔记 App 里塞任务功能”，而是变成一个统一 App 壳。Notes 和 Tasks 都是壳下面的平级功能模块。

## 2. 新架构目标

目标不是把 `reference/tasks` 整个原版 App 原封不动塞进 QingNote，也不是继续在 IdeaMemo 页面里手搓复刻。

目标是：

- QingNote 统一导航、主题、权限、设置入口。
- Notes 保留现有 IdeaMemo 功能。
- Tasks 以 `reference/tasks` 的原版数据层和服务层为主体。
- UI 可以适配 QingNote 风格，但任务行为必须按原版源码对齐。
- 不迁移用户不需要的原版生态功能，例如云同步、付费、更新、账号、Wear、Widget。

## 3. 目标架构

```text
QingNote
├─ app-shell
│  ├─ navigation
│  │  ├─ Notes
│  │  ├─ Tasks
│  │  ├─ Settings
│  │  └─ TaskSettings
│  ├─ theme
│  │  ├─ SaltTheme bridge
│  │  └─ MaterialTheme bridge
│  ├─ permissions
│  │  ├─ notification
│  │  ├─ exact alarm
│  │  ├─ calendar
│  │  └─ attachment/file
│  └─ settings-registry
│
├─ feature-notes
│  ├─ IdeaMemo note database
│  ├─ Note pages
│  ├─ Note editor
│  └─ Note settings
│
└─ feature-tasks
   ├─ domain
   │  ├─ Task
   │  ├─ Alarm
   │  ├─ TagData
   │  ├─ TaskAttachment
   │  └─ recurrence
   ├─ data
   │  ├─ TaskDao
   │  ├─ TagDataDao
   │  ├─ TagDao
   │  ├─ TaskAttachmentDao
   │  └─ Room migrations
   ├─ services
   │  ├─ TaskSaver
   │  ├─ TaskCompleter
   │  ├─ TaskDeleter
   │  ├─ TaskMover
   │  ├─ AlarmService
   │  └─ Calendar writer
   ├─ ui
   │  ├─ TaskListScreen
   │  ├─ TaskEditorScreen
   │  ├─ DateTimePicker
   │  ├─ RecurrenceEditor
   │  ├─ AttachmentRow
   │  ├─ SubtaskRow
   │  └─ TaskDrawer
   └─ settings
      └─ TaskSettingsPage
```

## 4. 原版源码映射

| 功能 | 原版源码 | 新架构落点 |
|---|---|---|
| 任务编辑 Fragment | `reference/tasks/app/src/main/java/com/todoroo/astrid/activity/TaskEditFragment.kt` | `feature-tasks/ui/TaskEditorScreen` |
| 编辑状态和保存 | `reference/tasks/app/src/main/java/org/tasks/ui/TaskEditViewModel.kt` | `feature-tasks/ui/TaskEditViewModel` |
| 任务创建默认值 | `reference/tasks/app/src/main/java/com/todoroo/astrid/service/TaskCreator.kt` | `feature-tasks/services/TaskCreator` |
| 任务保存 | `TaskSaver` | `feature-tasks/services/TaskSaver` |
| 完成任务 | `TaskCompleter` | `feature-tasks/services/TaskCompleter` |
| 删除任务 | `TaskDeleter` | `feature-tasks/services/TaskDeleter` |
| 移动任务 | `reference/tasks/app/src/main/java/com/todoroo/astrid/service/TaskMover.kt` | `feature-tasks/services/TaskMover` |
| 日期时间选择 | `reference/tasks/app/src/main/java/org/tasks/activities/DateAndTimePickerActivity.kt` | `feature-tasks/ui/DateTimePicker` |
| 重复规则 | `reference/tasks/app/src/main/java/org/tasks/repeats/BasicRecurrenceDialog.kt` | `feature-tasks/ui/RecurrenceEditor` |
| 提醒控件 | `reference/tasks/app/src/main/java/com/todoroo/astrid/ui/ReminderControlSet.kt` | `feature-tasks/ui/ReminderEditor` |
| 附件 | `reference/tasks/app/src/main/java/org/tasks/compose/edit/AttachmentRow.kt`、`com/todoroo/astrid/files/FilesControlSet.kt` | `feature-tasks/ui/AttachmentRow`、`feature-tasks/services/AttachmentService` |
| 子任务 | `reference/tasks/app/src/main/java/org/tasks/compose/edit/SubtaskRow.kt`、`org/tasks/ui/SubtaskControlSet.kt` | `feature-tasks/ui/SubtaskRow` |
| 任务列表 | `reference/tasks/app/src/main/java/com/todoroo/astrid/activity/TaskListFragment.kt`、`org/tasks/tasklist/TaskViewHolder.kt` | `feature-tasks/ui/TaskListScreen` |
| 清单 drawer | `reference/tasks/app/src/main/java/org/tasks/filters/FilterProvider.kt` | `feature-tasks/ui/TaskDrawer` |
| 清单排序 | `reference/tasks/app/src/main/java/org/tasks/activities/NavigationDrawerCustomization.kt` | `feature-tasks/services/ListOrderService` |
| 清单设置 | `reference/tasks/app/src/main/java/org/tasks/activities/TagSettingsActivity.kt` | `feature-tasks/ui/ListSettingsDialog` |
| 任务设置 | `reference/tasks/app/src/main/java/org/tasks/compose/settings/*` | `feature-tasks/settings/TaskSettingsPage` |

## 5. 不再继续的旧路线

旧路线：

```text
AllNotePage.kt
├─ Notes UI
├─ Tasks toolbar
├─ Tasks drawer
├─ Tasks list
├─ Tasks editor
├─ Tasks dialogs
└─ Tasks settings hooks
```

这个路线的问题：

- `AllNotePage.kt` 会越来越大。
- Notes 与 Tasks 状态互相混杂。
- Tasks 原版服务层无法完整接入，只能靠手写桥接。
- 每补一个原版行为，都可能牵出新的桥接问题。
- 后续维护困难。

新路线要求：

- `AllNotePage.kt` 只保留 Notes/Tasks 切换壳。
- Tasks 业务全部沉到 `feature-tasks`。
- Notes 业务全部留在 `feature-notes`。
- App Shell 只协调导航、主题、权限、设置入口。

## 6. 迁移阶段

### 阶段 0：冻结当前可运行版本

目的：保留当前已经构建通过的融合版，作为回退点。

任务：

- 保留当前 `TaskListScreen.kt`、`TaskListViewModel.kt`、`TasksRepository.kt`。
- 不再继续无边界地在现有文件上堆新功能。
- 后续改造必须以可构建为阶段边界。

验收：

- `.\gradlew.bat :app:assembleDebug --offline` 通过。

### 阶段 1：建立 Feature 边界

目的：把 Tasks 从 Notes 页面里抽出来。

目标结构：

```text
app/src/main/java/com/qingguang/qingnote/tasks/
├─ data
├─ domain
├─ service
├─ ui
└─ settings
```

任务：

- 把 `com.qingguang.qingnote.tasks` 扩展为 Tasks feature 根包。
- 把 `org.tasks.compose.TaskListScreen` 逐步迁到 `com.qingguang.qingnote.tasks.ui`。
- 把 `TaskListViewModel` 迁到 `com.qingguang.qingnote.tasks.ui`.
- 把 `TasksRepository` 拆成更明确的服务：
  - `TaskQueryService`
  - `TaskEditService`
  - `TaskListService`
  - `TaskAttachmentService`
  - `TaskReminderService`

验收：

- `AllNotePage.kt` 不再承载 Tasks 业务细节，只调用 `TasksHomeRoute`。
- 构建通过。

### 阶段 2：服务层按原版替换

目的：不再手写一套近似 Tasks 后端，而是按原版源码接服务。

任务：

- 对齐 `TaskEditViewModel.kt` 中的保存逻辑。
- 对齐 `TaskMover.kt` 的移动逻辑。
- 对齐 `TaskSaver`、`TaskCompleter`、`TaskDeleter` 的调用边界。
- 对齐附件保存/删除逻辑。
- 对齐提醒 `AlarmService` 的同步逻辑。

保留：

- QingNote 自己的权限请求入口。
- QingNote 自己的设置存储。
- QingNote 当前 Room 集成方式。

验收：

- 新建、编辑、完成、删除、移动任务的数据库结果与原版逻辑一致。
- 构建通过。

### 阶段 3：编辑器原版化

目的：让创建/编辑任务的行为接近原版。

任务：

- 用原版 `TaskEditFragment` / `TaskEditViewModel` 行为校验当前编辑器：
  - 未保存确认
  - 空标题提示
  - 返回键行为
  - 保存前权限检查
  - 附件和子任务保存
  - 日历写入
  - 提醒写入
- UI 可以继续 Compose，但字段行为必须对齐。

验收：

- 创建任务面板选项完整。
- 保存逻辑稳定。
- 返回不会误丢内容。
- 权限失败不会崩溃。

### 阶段 4：日期/重复原版化

目的：减少当前日期面板和原版的体验差距。

任务：

- 按 `DateAndTimePickerActivity` 重新整理日期/时间选择：
  - 无日期
  - 快捷日期
  - 自定义日期
  - 无具体时间
  - 具体时间
  - 清除时间
- 按 `BasicRecurrenceDialog` 对齐重复规则：
  - 每天
  - 每周指定星期
  - 每月指定日期
  - 每月第几个星期几
  - 每年
  - 结束次数
  - 结束日期

验收：

- RRULE 保存稳定。
- 日期-only 与 date-time 区分正确。
- 列表展示不误显示时间。

### 阶段 5：子任务和附件原版化

目的：对齐原版的子任务和附件交互。

任务：

- 子任务：
  - 添加
  - 完成
  - 删除
  - 排序
  - 保存 order
  - 列表展示
- 附件：
  - 添加文件
  - 打开
  - 分享
  - 删除确认
  - 权限失效提示
  - 文件名清洗
  - 本地副本保存

验收：

- 子任务重进后顺序不乱。
- 附件实机可打开、可分享、可删除。

### 阶段 6：清单和批量操作原版化

目的：对齐原版清单体系中和当前需求相关的部分。

任务：

- 清单：
  - 创建
  - 重命名
  - 删除
  - 移动任务到清单
  - 颜色
  - 图标
  - 排序
  - 默认清单
- 批量操作：
  - 完成
  - 删除
  - 移动清单
  - 设置日期
  - 设置优先级
  - 设置标签

验收：

- 多选批量操作可用。
- 清单排序持久化。
- 默认清单生效。

### 阶段 7：App Shell 统一

目的：从用户体验上统一 Notes 和 Tasks。

任务：

- 顶部 Notes/Tasks 切换只做导航，不承载业务。
- 右滑 Tasks -> Notes 保持。
- Tasks drawer 右侧弹出保持。
- Bottom nav 与 Tasks/Notes 状态不冲突。
- 任务设置接入 QingNote 设置。
- 权限提示统一由 App Shell 处理。

验收：

- Notes 和 Tasks 像同一个 App 的两个功能区。
- 互不污染状态。
- 返回栈合理。

## 7. 最终验收标准

最终不再用“是否完整复制原版 Tasks.org”作为标准。

标准改为：

- 用户需要的 Tasks 功能全部可用。
- 核心数据和服务逻辑以原版源码为准。
- QingNote 内 Notes/Tasks 是平级模块。
- 不迁移不需要的原版生态功能。
- 构建通过。
- 实机关键路径通过。

实机关键路径：

1. 新建任务。
2. 编辑任务。
3. 设置开始/截止日期。
4. 设置重复规则。
5. 添加提醒并触发权限申请。
6. 添加子任务并排序。
7. 添加附件、打开、分享、删除。
8. 创建清单、重命名、排序、设默认。
9. 多选任务批量完成、删除、移动、改日期、改优先级、改标签。
10. Notes 和 Tasks 来回切换。

## 8. 风险

### 风险 1：原版是多模块大 App

`reference/tasks` 不是单一页面，而是一整套成熟 App。直接整包搬入 QingNote 会引入大量无关依赖。

处理：

- 不整包搬。
- 按功能点迁移服务层和 UI 行为。

### 风险 2：当前 QingNote 与 Tasks 架构不同

IdeaMemo/QingNote 的 Notes 逻辑和 Tasks 原版逻辑不是同一套状态管理。

处理：

- 建立 App Shell。
- Notes 与 Tasks 做平级 feature。

### 风险 3：继续在 `AllNotePage.kt` 堆代码会失控

处理：

- 抽出 `TasksHomeRoute`。
- AllNotePage 只保留切换入口。

### 风险 4：完整原版生态不应迁移

处理：

- 明确不迁移云同步、账号、付费、更新、Wear、Widget。

## 9. 建议执行策略

不要再问“还差什么才完美”。  
按以下方式推进：

1. 先完成 Feature 边界拆分。
2. 再逐块替换服务层。
3. 再逐块对齐 UI 行为。
4. 每阶段构建。
5. 最后实机验收。
6. 实机发现具体 bug，再修具体 bug。

这条路线才能让 QingNote 和 Tasks 真正进入一个统一架构，而不是一直靠补丁接近原版。
