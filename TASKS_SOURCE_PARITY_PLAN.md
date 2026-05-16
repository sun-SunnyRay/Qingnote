# QingNote Tasks 原版源码对照融合方案

目标：不再按“感觉”补差距，而是以 `C:\Users\qingguang\dev\QingNote\reference\tasks` 的原版源码为对照，把 QingNote 当前 Tasks 融合版修到可实机验收的原版化版本。

不迁移范围：云同步、账号、付费、更新、完整备份导入导出、Wear、Widget、DAV/Google/Microsoft 同步生态。  
迁移范围：任务创建/编辑、日期时间、重复规则、提醒权限、子任务、附件、清单、列表筛选、批量操作、设置中与这些功能直接相关的项。

## 1. 源码对照表

| 功能 | 原版源码 | 当前落点 | 处理方式 |
|---|---|---|---|
| 任务编辑入口 | `reference/tasks/app/src/main/java/com/todoroo/astrid/activity/TaskEditFragment.kt` | `app/src/main/java/org/tasks/compose/TaskListScreen.kt` | 保留 QingNote Compose 全屏编辑器，但按原版补返回、保存、权限、弹窗流程 |
| 编辑状态和保存 | `reference/tasks/app/src/main/java/org/tasks/ui/TaskEditViewModel.kt` | `TaskListViewModel.kt`、`TasksRepository.kt` | 当前已有保存主链路，补脏状态、批量字段更新、附件删除一致性 |
| 日期时间选择 | `reference/tasks/app/src/main/java/org/tasks/activities/DateAndTimePickerActivity.kt` | `TaskListScreen.kt` 的 `DateTimeEditorDialog` | 复刻原版“日期 + 时间 + 无具体时间 + 快捷项”的交互，不再只用简单 Material DatePicker |
| 重复规则 | `reference/tasks/app/src/main/java/org/tasks/repeats/BasicRecurrenceDialog.kt` | `TaskListScreen.kt` 的 `RepeatEditorDialog` | 以原版 RRULE 能力为准，补摘要、结束条件、复杂月规则边界 |
| 提醒 | `reference/tasks/app/src/main/java/com/todoroo/astrid/ui/ReminderControlSet.kt` | `RemindersDialog`、`TasksRepository.syncReminders` | 保持原版 Alarm 类型，补 Android 12/13+ 权限兜底与提示文案 |
| 子任务编辑 | `reference/tasks/app/src/main/java/org/tasks/compose/edit/SubtaskRow.kt`、`org/tasks/ui/SubtaskControlSet.kt` | `SubtasksDialog`、`TaskInlineSubtasks` | 补排序、完成、删除、空行处理、列表展示密度 |
| 附件编辑 | `reference/tasks/app/src/main/java/org/tasks/compose/edit/AttachmentRow.kt`、`com/todoroo/astrid/files/FilesControlSet.kt` | `AttachmentsDialog`、附件 helper | 补打开、分享、删除确认、权限失效提示、文件名处理 |
| 批量移动 | `reference/tasks/app/src/main/java/com/todoroo/astrid/service/TaskMover.kt` | `TasksRepository.moveTasksToList` | 当前已接 tag/list 移动，补批量日期、优先级、标签 |
| 批量菜单 | `reference/tasks/app/src/main/java/com/todoroo/astrid/activity/TaskListFragment.kt` | `TaskSelectionToolbar` | 从完成/删除/移动扩展为日期、优先级、标签、清单 |
| 清单设置 | `reference/tasks/app/src/main/java/org/tasks/activities/TagSettingsActivity.kt` | `AllNotePage.kt` 清单创建/重命名弹窗 | 保留当前颜色/图标基础，补默认清单和排序 |
| 清单排序 | `reference/tasks/app/src/main/java/org/tasks/activities/NavigationDrawerCustomization.kt` | `TasksRepository.getAvailableTags`、`AllNotePage.kt` | 使用 `TagData.order` 排序，提供上移/下移基础操作 |
| Drawer 数据 | `reference/tasks/app/src/main/java/org/tasks/filters/FilterProvider.kt` | `TasksRepository.getAvailableTags/getFilterCounts` | 不搬完整 FilterProvider，只保留当前需要的智能清单和 tag 清单 |
| 列表项 | `reference/tasks/app/src/main/java/org/tasks/tasklist/TaskViewHolder.kt` | `TaskItem`、`TaskExtrasChips` | 补子任务展开、附件/提醒/标签芯片、密度和长按菜单 |
| 设置页 | `reference/tasks/app/src/main/java/org/tasks/compose/settings/*` | `TaskSettingsPage.kt`、`SettingsPreferences.kt` | 仅保留用户要求的任务相关设置，不搬无关项 |

## 2. 当前状态

已完成：
- Notes/Tasks 顶部切换。
- Tasks 右侧 drawer。
- 创建/编辑任务主字段：标题、开始、截止、重复、优先级、标签、子任务、提醒、描述、附件、日历、计时器。
- 后端接入原版 `Task`、`Alarm`、`TagData`、`TaskAttachment`、子任务 parent/order。
- 子任务列表展示。
- 批量完成、删除、移动清单。
- 清单创建、重命名、删除、颜色/图标基础。
- 日期选择器已升级为日期/时间面板基础。
- 离线构建 `:app:assembleDebug --offline` 已通过。

仍需按源码补齐的验收项：
- 编辑器返回/关闭时未保存确认。
- 标题输入的 IME 行为，空标题保存提示。
- 日期时间面板继续贴近 `DateAndTimePickerActivity`。
- RRULE 边界和摘要对齐 `BasicRecurrenceDialog`。
- 批量日期、批量优先级、批量标签。
- 附件打开、分享、删除确认、权限失效提示。
- 子任务排序、删除确认和空行处理细节。
- 清单排序、默认清单基础。
- 权限提示文案和 Android 12/13+ 实机兜底。

## 3. 分阶段执行计划

### 阶段 A：编辑器原版行为

目标文件：
- 原版：`TaskEditFragment.kt`、`TaskEditViewModel.kt`
- 当前：`TaskListScreen.kt`、`TaskListViewModel.kt`、`TasksRepository.kt`

任务：
- 增加编辑器脏状态判断。
- 返回键或点击外部关闭时，如果有修改，弹出“放弃/保存/继续编辑”确认。
- 标题为空时显示提示，不静默失败。
- 标题键盘完成键触发保存或收起键盘。
- 保存前统一校验：开始日期不能晚于截止日期、提醒权限、日历权限。

验收：
- 新建任务输入内容后返回不会直接丢失。
- 编辑已有任务改字段后返回会确认。
- 空标题点击保存有可见提示。

### 阶段 B：日期时间与重复规则

目标文件：
- 原版：`DateAndTimePickerActivity.kt`、`BasicRecurrenceDialog.kt`
- 当前：`DateTimeEditorDialog`、`RepeatEditorDialog`

任务：
- 日期面板改为“快捷日期 + 日历 + 时间区 + 清除时间”的结构。
- 明确支持“无日期”和“无具体时间”。
- 保留 Tasks 原版 time marker 逻辑，避免日期任务误显示具体时间。
- 补重复规则摘要：每天、每周指定星期、每月日期、每月第几个星期几、结束次数/结束日期。
- RRULE 输入边界校验，不生成非法规则。

验收：
- 只选日期时列表显示日期，不误认为具体时间。
- 选具体时间时提醒/截止正确保存。
- 重复规则摘要和保存结果可读、稳定。

### 阶段 C：子任务和附件

目标文件：
- 原版：`SubtaskRow.kt`、`SubtaskControlSet.kt`、`AttachmentRow.kt`、`FilesControlSet.kt`
- 当前：`SubtasksDialog`、`AttachmentsDialog`、附件 helper、`TasksRepository.syncSubtasks/syncAttachments`

任务：
- 子任务支持上移/下移排序，保存到 `Task.order`。
- 子任务删除、完成、空标题过滤与原版一致。
- 附件行增加菜单：打开、分享、删除。
- 删除附件前确认。
- 打开失败时提示权限或文件不存在。
- 分享附件使用 `ACTION_SEND`，附带读权限。

验收：
- 多个子任务排序保存后重进仍正确。
- 附件可打开、可分享、可删除。
- 权限失效不崩溃。

### 阶段 D：批量操作

目标文件：
- 原版：`TaskListFragment.kt`、`TaskMover.kt`
- 当前：`TaskSelectionToolbar`、`TaskListViewModel`、`TasksRepository`

任务：
- 批量设置清单。
- 批量设置优先级。
- 批量设置开始/截止日期。
- 批量添加/替换标签。
- 批量操作后刷新列表，并保留 snackbar 反馈。

验收：
- 多选任务后可一次性改优先级、日期、标签、清单。
- 操作后列表数据立即更新。
- 已完成和未完成任务都能正确处理。

### 阶段 E：清单体系

目标文件：
- 原版：`TagSettingsActivity.kt`、`NavigationDrawerCustomization.kt`、`FilterProvider.kt`
- 当前：`AllNotePage.kt`、`TasksRepository.kt`、`TaskSettings.kt`、`SettingsPreferences.kt`

任务：
- `getAvailableTags()` 按 `TagData.order` 优先排序，再按名称排序。
- 清单菜单增加上移/下移。
- 增加默认清单设置，创建任务默认进入该清单。
- 删除默认清单时清空默认值。
- 清单颜色/图标继续使用 `TagData.color/icon`。

验收：
- 清单顺序可调整并持久化。
- 默认清单设置后，新建任务自动带该清单。
- 删除清单不会删除任务本身。

### 阶段 F：统一构建和实机验收

命令：

```powershell
cd C:\Users\qingguang\dev\QingNote\QingNote
.\gradlew.bat :app:assembleDebug --offline
```

验收清单：
- 构建通过。
- 安装到实机不崩溃。
- 新建任务、编辑任务、删除任务、完成任务可用。
- 日期、重复、提醒保存正确。
- 子任务、附件、清单、批量操作可用。
- 权限申请流程可走通。

## 4. 执行原则

- 以 `reference/tasks` 真实源码为准，不再凭印象补功能。
- 不搬原版不需要的生态功能。
- 保留 QingNote 当前 UI 入口和架构，避免整包替换导致 IdeaMemo 主体崩。
- 每一阶段完成后必须跑构建。
- 构建通过后再交给实机测试。

## 5. 最终交付定义

“完成”定义为：

- 用户要求保留的 Tasks 功能全部可用。
- 当前 QingNote app 内 Notes/Tasks 融合稳定。
- 与原版源码中对应功能的核心数据逻辑一致。
- 不再把云同步、账号、付费、更新、完整原版生态列为差距。
- 后续只根据实机测试反馈修具体 bug，不再无限扩展“还有什么优化”。
