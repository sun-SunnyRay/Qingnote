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
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
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
import com.qingguang.qingnote.R
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
        if (settings.defaultTags.isBlank()) null else settings.defaultTags
    }
    val defaultListLabel = remember(settings.defaultListName) {
        if (settings.defaultListName.isBlank()) null else settings.defaultListName
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
            text = stringResource(R.string.task_settings)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            RoundedColumn {
                ItemTitle(text = stringResource(R.string.task_defaults))
                EnumPopupRow(
                    icon = Icons.Outlined.Flag,
                    text = stringResource(R.string.default_priority),
                    selected = settings.defaultPriority,
                    options = TaskDefaultPriority.entries.map { it to it.label() },
                ) { scope.launch { SettingsPreferences.changeTaskDefaultPriority(it) } }
                EnumPopupRow(
                    icon = Icons.Outlined.EventAvailable,
                    text = stringResource(R.string.default_start_date),
                    selected = settings.defaultStartDate,
                    options = TaskDefaultDate.entries.map { it to it.label() },
                ) { scope.launch { SettingsPreferences.changeTaskDefaultStartDate(it) } }
                EnumPopupRow(
                    icon = Icons.Outlined.AccessTime,
                    text = stringResource(R.string.default_due_date),
                    selected = settings.defaultDueDate,
                    options = TaskDefaultDate.entries.map { it to it.label() },
                ) { scope.launch { SettingsPreferences.changeTaskDefaultDueDate(it) } }
                EnumPopupRow(
                    icon = Icons.Outlined.Notifications,
                    text = stringResource(R.string.default_reminder),
                    selected = settings.defaultReminder,
                    options = TaskDefaultReminder.entries.map { it to it.label() },
                ) { scope.launch { SettingsPreferences.changeTaskDefaultReminder(it) } }
                EnumPopupRow(
                    icon = Icons.Outlined.Repeat,
                    text = stringResource(R.string.default_recurrence),
                    selected = settings.defaultRecurrence,
                    options = TaskDefaultRecurrence.entries.map { it to it.label() },
                ) { scope.launch { SettingsPreferences.changeTaskDefaultRecurrence(it) } }
                Item(
                    onClick = {
                        defaultTags = settings.defaultTags
                        showTagsDialog = true
                    },
                    text = defaultTagsLabel?.let { stringResource(R.string.default_tags_label, it) } ?: stringResource(R.string.default_tags_label_none),
                    iconPainter = rememberVectorPainter(Icons.AutoMirrored.Outlined.Label),
                )
                Item(
                    onClick = {
                        defaultList = settings.defaultListName
                        showDefaultListDialog = true
                    },
                    text = defaultListLabel?.let { stringResource(R.string.default_list_label, it) } ?: stringResource(R.string.default_list_label_none),
                    iconPainter = rememberVectorPainter(Icons.Outlined.FilterList),
                )
                ItemSwitcher(
                    state = settings.defaultAddToCalendar,
                    onChange = { scope.launch { SettingsPreferences.changeTaskDefaultAddToCalendar(it) } },
                    text = stringResource(R.string.default_add_to_calendar),
                    iconPainter = rememberVectorPainter(Icons.Outlined.CalendarMonth),
                    iconColor = SaltTheme.colors.text,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            RoundedColumn {
                ItemTitle(text = stringResource(R.string.task_list_options))
                ItemSwitcher(
                    state = settings.showFullTaskTitle,
                    onChange = { scope.launch { SettingsPreferences.changeTaskShowFullTitle(it) } },
                    text = stringResource(R.string.show_full_task_title),
                    iconPainter = rememberVectorPainter(Icons.Outlined.CheckCircle),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.showDescription,
                    onChange = { scope.launch { SettingsPreferences.changeTaskShowDescription(it) } },
                    text = stringResource(R.string.show_description),
                    iconPainter = rememberVectorPainter(Icons.AutoMirrored.Outlined.Notes),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.showFullDescription,
                    onChange = { scope.launch { SettingsPreferences.changeTaskShowFullDescription(it) } },
                    text = stringResource(R.string.show_full_description),
                    iconPainter = rememberVectorPainter(Icons.AutoMirrored.Outlined.Notes),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.showStartDate,
                    onChange = { scope.launch { SettingsPreferences.changeTaskShowStartDate(it) } },
                    text = stringResource(R.string.show_start_date),
                    iconPainter = rememberVectorPainter(Icons.Outlined.EventAvailable),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.showDueDate,
                    onChange = { scope.launch { SettingsPreferences.changeTaskShowDueDate(it) } },
                    text = stringResource(R.string.show_due_date),
                    iconPainter = rememberVectorPainter(Icons.Outlined.AccessTime),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.showPriorityIndicator,
                    onChange = { scope.launch { SettingsPreferences.changeTaskShowPriority(it) } },
                    text = stringResource(R.string.show_priority_indicator),
                    iconPainter = rememberVectorPainter(Icons.Outlined.Flag),
                    iconColor = SaltTheme.colors.text,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            RoundedColumn {
                ItemTitle(text = stringResource(R.string.edit_screen_options))
                ItemSwitcher(
                    state = settings.backButtonSavesTask,
                    onChange = { scope.launch { SettingsPreferences.changeTaskBackButtonSaves(it) } },
                    text = stringResource(R.string.back_button_saves_task),
                    iconPainter = rememberVectorPainter(Icons.Outlined.CheckCircle),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.multilineTaskTitle,
                    onChange = { scope.launch { SettingsPreferences.changeTaskMultilineTitle(it) } },
                    text = stringResource(R.string.multiline_task_title),
                    iconPainter = rememberVectorPainter(Icons.AutoMirrored.Outlined.Notes),
                    iconColor = SaltTheme.colors.text,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            RoundedColumn {
                ItemTitle(text = stringResource(R.string.date_and_time))
                ItemSwitcher(
                    state = settings.alwaysDisplayFullDate,
                    onChange = { scope.launch { SettingsPreferences.changeTaskAlwaysFullDate(it) } },
                    text = stringResource(R.string.always_display_full_date),
                    iconPainter = rememberVectorPainter(Icons.Outlined.CalendarMonth),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.use24HourTime,
                    onChange = { scope.launch { SettingsPreferences.changeTaskUse24Hour(it) } },
                    text = stringResource(R.string.use_24_hour),
                    iconPainter = rememberVectorPainter(Icons.Outlined.AccessTime),
                    iconColor = SaltTheme.colors.text,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            RoundedColumn {
                ItemTitle(text = stringResource(R.string.navigation_drawer))
                ItemSwitcher(
                    state = settings.drawerShowFilters,
                    onChange = { scope.launch { SettingsPreferences.changeTaskDrawerShowFilters(it) } },
                    text = stringResource(R.string.show_filters),
                    iconPainter = rememberVectorPainter(Icons.Outlined.FilterList),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.drawerShowDueFilters,
                    onChange = { scope.launch { SettingsPreferences.changeTaskDrawerShowDueFilters(it) } },
                    text = stringResource(R.string.show_due_date_filters),
                    iconPainter = rememberVectorPainter(Icons.Rounded.Menu),
                    iconColor = SaltTheme.colors.text,
                )
                ItemSwitcher(
                    state = settings.drawerHideEmptyTags,
                    onChange = { scope.launch { SettingsPreferences.changeTaskDrawerHideEmptyTags(it) } },
                    text = stringResource(R.string.hide_empty_tags),
                    iconPainter = rememberVectorPainter(Icons.AutoMirrored.Outlined.Label),
                    iconColor = SaltTheme.colors.text,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            RoundedColumn {
                ItemTitle(text = stringResource(R.string.advanced_settings))
                ItemSwitcher(
                    state = settings.calendarEndAtDueTime,
                    onChange = { scope.launch { SettingsPreferences.changeTaskCalendarEndAtDue(it) } },
                    text = stringResource(R.string.calendar_end_at_due),
                    iconPainter = rememberVectorPainter(Icons.Outlined.CalendarMonth),
                    iconColor = SaltTheme.colors.text,
                )
                Item(
                    onClick = { showResetDialog = true },
                    text = stringResource(R.string.reset_task_settings),
                    iconPainter = rememberVectorPainter(Icons.Outlined.Info),
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showTagsDialog) {
        AlertDialog(
            onDismissRequest = { showTagsDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            title = { Text(stringResource(R.string.default_tags)) },
            text = {
                OutlinedTextField(
                    value = defaultTags,
                    onValueChange = { defaultTags = it },
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.multiple_tags_hint)) },
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { SettingsPreferences.changeTaskDefaultTags(defaultTags.trim()) }
                    showTagsDialog = false
                }) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTagsDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (showDefaultListDialog) {
        AlertDialog(
            onDismissRequest = { showDefaultListDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            title = { Text(stringResource(R.string.default_list)) },
            text = {
                OutlinedTextField(
                    value = defaultList,
                    onValueChange = { defaultList = it.take(80) },
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.empty_list_hint)) },
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { SettingsPreferences.changeTaskDefaultList(defaultList.trim()) }
                    showDefaultListDialog = false
                }) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDefaultListDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            title = { Text(stringResource(R.string.reset_task_settings)) },
            text = { Text(stringResource(R.string.reset_task_settings_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { SettingsPreferences.resetTaskSettings() }
                    showResetDialog = false
                }) {
                    Text(stringResource(R.string.reset))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(R.string.cancel))
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
    options: List<Pair<T, Int>>,
    onSelected: (T) -> Unit,
) {
    val popupState = rememberPopupState()
    ItemPopup(
        state = popupState,
        iconPainter = rememberVectorPainter(icon),
        iconColor = SaltTheme.colors.text,
        text = text,
        selectedItem = options.firstOrNull { it.first == selected }?.let { stringResource(it.second) }.orEmpty(),
        popupWidth = 180,
    ) {
        options.forEach { (value, labelRes) ->
            PopupMenuItem(
                onClick = {
                    onSelected(value)
                    popupState.dismiss()
                },
                selected = selected == value,
                text = stringResource(labelRes),
                iconColor = SaltTheme.colors.text,
            )
        }
    }
}

@StringRes
private fun TaskDefaultPriority.label(): Int =
    when (this) {
        TaskDefaultPriority.NONE -> R.string.priority_none
        TaskDefaultPriority.LOW -> R.string.priority_low
        TaskDefaultPriority.MEDIUM -> R.string.priority_medium
        TaskDefaultPriority.HIGH -> R.string.priority_high
    }

@StringRes
private fun TaskDefaultDate.label(): Int =
    when (this) {
        TaskDefaultDate.NONE -> R.string.date_none
        TaskDefaultDate.TODAY -> R.string.date_today
        TaskDefaultDate.TOMORROW -> R.string.date_tomorrow
        TaskDefaultDate.NEXT_WEEK -> R.string.date_next_week
    }

@StringRes
private fun TaskDefaultReminder.label(): Int =
    when (this) {
        TaskDefaultReminder.NONE -> R.string.reminder_none
        TaskDefaultReminder.AT_DUE_TIME -> R.string.reminder_at_due_time
        TaskDefaultReminder.TEN_MINUTES_BEFORE -> R.string.reminder_10_min_before
        TaskDefaultReminder.TOMORROW_MORNING -> R.string.reminder_tomorrow_9am
    }

@StringRes
private fun TaskDefaultRecurrence.label(): Int =
    when (this) {
        TaskDefaultRecurrence.NONE -> R.string.recurrence_none
        TaskDefaultRecurrence.DAILY -> R.string.recurrence_daily
        TaskDefaultRecurrence.WEEKLY -> R.string.recurrence_weekly
        TaskDefaultRecurrence.MONTHLY -> R.string.recurrence_monthly
    }
