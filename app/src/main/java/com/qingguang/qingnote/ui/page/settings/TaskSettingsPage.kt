package com.qingguang.qingnote.ui.page.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.qingguang.qingnote.component.ItemPopup
import com.qingguang.qingnote.tasks.TaskDefaultDate
import com.qingguang.qingnote.tasks.TaskDefaultPriority
import com.qingguang.qingnote.tasks.TaskDefaultRecurrence
import com.qingguang.qingnote.tasks.TaskDefaultReminder
import com.qingguang.qingnote.tasks.TaskSettings
import com.qingguang.qingnote.ui.page.router.debouncedPopBackStack
import com.qingguang.qingnote.utils.SettingsPreferences
import com.moriafly.salt.ui.Item
import com.moriafly.salt.ui.ItemSwitcher
import com.moriafly.salt.ui.ItemTitle
import com.moriafly.salt.ui.RoundedColumn
import com.moriafly.salt.ui.SaltTheme
import com.moriafly.salt.ui.TitleBar
import com.moriafly.salt.ui.UnstableSaltApi
import com.moriafly.salt.ui.popup.PopupMenuItem
import com.moriafly.salt.ui.popup.rememberPopupState
import kotlinx.coroutines.launch

@OptIn(UnstableSaltApi::class)
@Composable
fun TaskSettingsPage(navController: NavHostController) {
    val settings by SettingsPreferences.taskSettings.collectAsState(TaskSettings())
    val scope = rememberCoroutineScope()
    var showTagsDialog by remember { mutableStateOf(false) }
    var showDefaultListDialog by remember { mutableStateOf(false) }
    var defaultTags by remember(settings.defaultTags) { mutableStateOf(settings.defaultTags) }
    var defaultList by remember(settings.defaultListName) { mutableStateOf(settings.defaultListName) }
    val defaultTagsLabel = remember(settings.defaultTags) {
        if (settings.defaultTags.isBlank()) "默认标签：无" else "默认标签：${settings.defaultTags}"
    }
    val defaultListLabel = remember(settings.defaultListName) {
        if (settings.defaultListName.isBlank()) "默认清单：我的任务" else "默认清单：${settings.defaultListName}"
    }
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SaltTheme.colors.background)
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        TitleBar(
            onBack = { navController.debouncedPopBackStack() },
            text = "任务设置"
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            RoundedColumn {
                ItemTitle(text = "任务默认值")
                EnumPopupRow(
                    icon = Icons.Outlined.Flag,
                    text = "默认优先级",
                    selected = settings.defaultPriority,
                    options = TaskDefaultPriority.entries.map { it to it.label() },
                ) { scope.launch { SettingsPreferences.changeTaskDefaultPriority(it) } }
                EnumPopupRow(
                    icon = Icons.Outlined.EventAvailable,
                    text = "默认开始日期",
                    selected = settings.defaultStartDate,
                    options = TaskDefaultDate.entries.map { it to it.label() },
                ) { scope.launch { SettingsPreferences.changeTaskDefaultStartDate(it) } }
                EnumPopupRow(
                    icon = Icons.Outlined.AccessTime,
                    text = "默认截止日期",
                    selected = settings.defaultDueDate,
                    options = TaskDefaultDate.entries.map { it to it.label() },
                ) { scope.launch { SettingsPreferences.changeTaskDefaultDueDate(it) } }
                EnumPopupRow(
                    icon = Icons.Outlined.Notifications,
                    text = "默认提醒",
                    selected = settings.defaultReminder,
                    options = TaskDefaultReminder.entries.map { it to it.label() },
                ) { scope.launch { SettingsPreferences.changeTaskDefaultReminder(it) } }
                EnumPopupRow(
                    icon = Icons.Outlined.Repeat,
                    text = "默认重复",
                    selected = settings.defaultRecurrence,
                    options = TaskDefaultRecurrence.entries.map { it to it.label() },
                ) { scope.launch { SettingsPreferences.changeTaskDefaultRecurrence(it) } }
                Item(
                    onClick = {
                        defaultTags = settings.defaultTags
                        showTagsDialog = true
                    },
                    text = defaultTagsLabel,
                    iconPainter = rememberVectorPainter(Icons.AutoMirrored.Outlined.Label),
                )
                Item(
                    onClick = {
                        defaultList = settings.defaultListName
                        showDefaultListDialog = true
                    },
                    text = defaultListLabel,
                    iconPainter = rememberVectorPainter(Icons.Outlined.FilterList),
                )
                ItemSwitcher(
                    state = settings.defaultAddToCalendar,
                    onChange = { scope.launch { SettingsPreferences.changeTaskDefaultAddToCalendar(it) } },
                    text = "默认添加到日历",
                    iconPainter = rememberVectorPainter(Icons.Outlined.CalendarMonth),
                    iconColor = SaltTheme.colors.text,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            RoundedColumn {
                ItemTitle(text = "任务清单选项")
                ItemSwitcher(
                    state = settings.showFullTaskTitle,
                    onChange = { scope.launch { SettingsPreferences.changeTaskShowFullTitle(it) } },
                    text = "显示完整任务标题",
                    iconPainter = rememberVectorPainter(Icons.Outlined.CheckCircle),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.showDescription,
                    onChange = { scope.launch { SettingsPreferences.changeTaskShowDescription(it) } },
                    text = "显示描述",
                    iconPainter = rememberVectorPainter(Icons.AutoMirrored.Outlined.Notes),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.showFullDescription,
                    onChange = { scope.launch { SettingsPreferences.changeTaskShowFullDescription(it) } },
                    text = "显示完整描述",
                    iconPainter = rememberVectorPainter(Icons.AutoMirrored.Outlined.Notes),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.showStartDate,
                    onChange = { scope.launch { SettingsPreferences.changeTaskShowStartDate(it) } },
                    text = "显示开始日期",
                    iconPainter = rememberVectorPainter(Icons.Outlined.EventAvailable),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.showDueDate,
                    onChange = { scope.launch { SettingsPreferences.changeTaskShowDueDate(it) } },
                    text = "显示截止日期",
                    iconPainter = rememberVectorPainter(Icons.Outlined.AccessTime),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.showPriorityIndicator,
                    onChange = { scope.launch { SettingsPreferences.changeTaskShowPriority(it) } },
                    text = "显示优先级标记",
                    iconPainter = rememberVectorPainter(Icons.Outlined.Flag),
                    iconColor = SaltTheme.colors.text,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            RoundedColumn {
                ItemTitle(text = "编辑屏幕选项")
                ItemSwitcher(
                    state = settings.backButtonSavesTask,
                    onChange = { scope.launch { SettingsPreferences.changeTaskBackButtonSaves(it) } },
                    text = "返回键保存任务",
                    iconPainter = rememberVectorPainter(Icons.Outlined.CheckCircle),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.multilineTaskTitle,
                    onChange = { scope.launch { SettingsPreferences.changeTaskMultilineTitle(it) } },
                    text = "多行任务标题",
                    iconPainter = rememberVectorPainter(Icons.AutoMirrored.Outlined.Notes),
                    iconColor = SaltTheme.colors.text,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            RoundedColumn {
                ItemTitle(text = "日期与时间")
                ItemSwitcher(
                    state = settings.alwaysDisplayFullDate,
                    onChange = { scope.launch { SettingsPreferences.changeTaskAlwaysFullDate(it) } },
                    text = "始终显示完整日期",
                    iconPainter = rememberVectorPainter(Icons.Outlined.CalendarMonth),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.use24HourTime,
                    onChange = { scope.launch { SettingsPreferences.changeTaskUse24Hour(it) } },
                    text = "使用 24 小时制",
                    iconPainter = rememberVectorPainter(Icons.Outlined.AccessTime),
                    iconColor = SaltTheme.colors.text,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            RoundedColumn {
                ItemTitle(text = "导航抽屉")
                ItemSwitcher(
                    state = settings.drawerShowFilters,
                    onChange = { scope.launch { SettingsPreferences.changeTaskDrawerShowFilters(it) } },
                    text = "显示筛选器",
                    iconPainter = rememberVectorPainter(Icons.Outlined.FilterList),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.drawerShowDueFilters,
                    onChange = { scope.launch { SettingsPreferences.changeTaskDrawerShowDueFilters(it) } },
                    text = "显示截止日期筛选器",
                    iconPainter = rememberVectorPainter(Icons.Rounded.Menu),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.drawerHideEmptyTags,
                    onChange = { scope.launch { SettingsPreferences.changeTaskDrawerHideEmptyTags(it) } },
                    text = "隐藏空标签",
                    iconPainter = rememberVectorPainter(Icons.AutoMirrored.Outlined.Label),
                    iconColor = SaltTheme.colors.text,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            RoundedColumn {
                ItemTitle(text = "高级设置")
                ItemSwitcher(
                    state = settings.calendarEndAtDueTime,
                    onChange = { scope.launch { SettingsPreferences.changeTaskCalendarEndAtDue(it) } },
                    text = "日历事件结束于截止时间",
                    iconPainter = rememberVectorPainter(Icons.Outlined.CalendarMonth),
                    iconColor = SaltTheme.colors.text,
                )
                Item(
                    onClick = { showResetDialog = true },
                    text = "重置任务设置",
                    iconPainter = rememberVectorPainter(Icons.Outlined.Info),
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showTagsDialog) {
        AlertDialog(
            onDismissRequest = { showTagsDialog = false },
            title = { Text("默认标签") },
            text = {
                OutlinedTextField(
                    value = defaultTags,
                    onValueChange = { defaultTags = it },
                    singleLine = true,
                    placeholder = { Text("多个标签用空格或逗号分隔") },
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { SettingsPreferences.changeTaskDefaultTags(defaultTags.trim()) }
                    showTagsDialog = false
                }) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTagsDialog = false }) {
                    Text("取消")
                }
            },
        )
    }

    if (showDefaultListDialog) {
        AlertDialog(
            onDismissRequest = { showDefaultListDialog = false },
            title = { Text("默认清单") },
            text = {
                OutlinedTextField(
                    value = defaultList,
                    onValueChange = { defaultList = it.take(80) },
                    singleLine = true,
                    placeholder = { Text("留空则使用我的任务") },
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { SettingsPreferences.changeTaskDefaultList(defaultList.trim()) }
                    showDefaultListDialog = false
                }) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDefaultListDialog = false }) {
                    Text("取消")
                }
            },
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("重置任务设置") },
            text = { Text("将任务设置恢复为默认值。") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { SettingsPreferences.resetTaskSettings() }
                    showResetDialog = false
                }) {
                    Text("重置")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("取消")
                }
            },
        )
    }
}

@OptIn(UnstableSaltApi::class)
@Composable
private fun <T> EnumPopupRow(
    icon: ImageVector,
    text: String,
    selected: T,
    options: List<Pair<T, String>>,
    onSelected: (T) -> Unit,
) {
    val popupState = rememberPopupState()
    ItemPopup(
        state = popupState,
        iconPainter = rememberVectorPainter(icon),
        iconColor = SaltTheme.colors.text,
        text = text,
        selectedItem = options.firstOrNull { it.first == selected }?.second.orEmpty(),
        popupWidth = 180,
    ) {
        options.forEach { (value, label) ->
            PopupMenuItem(
                onClick = {
                    onSelected(value)
                    popupState.dismiss()
                },
                selected = selected == value,
                text = label,
                iconColor = SaltTheme.colors.text,
            )
        }
    }
}

private fun TaskDefaultPriority.label(): String =
    when (this) {
        TaskDefaultPriority.NONE -> "无"
        TaskDefaultPriority.LOW -> "低"
        TaskDefaultPriority.MEDIUM -> "中"
        TaskDefaultPriority.HIGH -> "高"
    }

private fun TaskDefaultDate.label(): String =
    when (this) {
        TaskDefaultDate.NONE -> "无"
        TaskDefaultDate.TODAY -> "今天"
        TaskDefaultDate.TOMORROW -> "明天"
        TaskDefaultDate.NEXT_WEEK -> "下周"
    }

private fun TaskDefaultReminder.label(): String =
    when (this) {
        TaskDefaultReminder.NONE -> "无"
        TaskDefaultReminder.AT_DUE_TIME -> "截止时间"
        TaskDefaultReminder.TEN_MINUTES_BEFORE -> "提前 10 分钟"
        TaskDefaultReminder.TOMORROW_MORNING -> "明天 9:00"
    }

private fun TaskDefaultRecurrence.label(): String =
    when (this) {
        TaskDefaultRecurrence.NONE -> "不重复"
        TaskDefaultRecurrence.DAILY -> "每天"
        TaskDefaultRecurrence.WEEKLY -> "每周"
        TaskDefaultRecurrence.MONTHLY -> "每月"
    }
