package com.qingguang.qingnote.tasks.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CalendarViewWeek
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SubdirectoryArrowRight
import androidx.compose.material.icons.outlined.Today

import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.qingguang.qingnote.R
import com.qingguang.qingnote.tasks.TaskDefaultPriority
import com.qingguang.qingnote.tasks.TaskDefaultReminder
import com.qingguang.qingnote.tasks.TaskAttachmentDraft
import com.qingguang.qingnote.tasks.TaskDrawerTag
import com.qingguang.qingnote.tasks.TaskEditData
import com.qingguang.qingnote.tasks.TaskEditExtras
import com.qingguang.qingnote.tasks.TaskListExtras
import com.qingguang.qingnote.tasks.TaskReminderDraft
import com.qingguang.qingnote.tasks.TaskSettings
import com.qingguang.qingnote.tasks.TaskSortMode
import com.qingguang.qingnote.tasks.TaskSubtaskDraft
import com.qingguang.qingnote.tasks.TaskViewFilter
import com.qingguang.qingnote.tasks.toRRule
import com.qingguang.qingnote.tasks.toTaskMillis
import com.qingguang.qingnote.utils.SettingsPreferences
import org.tasks.data.entity.Alarm
import com.moriafly.salt.ui.SaltTheme
import org.tasks.data.entity.Task
import java.text.SimpleDateFormat
import java.io.File
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = hiltViewModel(),
    showFloatingActionButton: Boolean = true,
) {
    val uiState by viewModel.uiState.collectAsState()
    val taskSettings by SettingsPreferences.taskSettings.collectAsState(TaskSettings())
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedTaskIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    val selectedTasks = remember(uiState.tasks, selectedTaskIds) {
        uiState.tasks.filter { it.id in selectedTaskIds }
    }
    var showBatchMoveDialog by remember { mutableStateOf(false) }
    var showBatchDateDialog by remember { mutableStateOf(false) }
    var batchDatePicker by remember { mutableStateOf<EditorPicker?>(null) }
    var showBatchPriorityDialog by remember { mutableStateOf(false) }
    var showBatchTagsDialog by remember { mutableStateOf(false) }

    fun showInfoSnackbar(message: String) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message = message)
        }
    }

    fun showUndoSnackbar(message: String, onUndo: () -> Unit) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "撤销",
                withDismissAction = true,
            )
            if (result == SnackbarResult.ActionPerformed) {
                onUndo()
            }
        }
    }

    LaunchedEffect(uiState.tasks) {
        selectedTaskIds = selectedTaskIds.intersect(uiState.tasks.map { it.id }.toSet())
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            uiState.errorMessage != null -> {
                TaskErrorView(
                    message = uiState.errorMessage.orEmpty(),
                    onRetry = { viewModel.loadTasks() },
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            uiState.tasks.isEmpty() -> {
                EmptyTasksView(
                    query = uiState.searchQuery,
                    selectedTag = uiState.selectedTag,
                    filter = uiState.filter,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            else -> {
                TaskList(
                    tasks = uiState.tasks,
                    extras = uiState.extras,
                    settings = taskSettings,
                    groupMode = uiState.groupMode,
                    groupAscending = uiState.groupAscending,
                    subtaskMode = uiState.subtaskMode,
                    subtaskAscending = uiState.subtaskAscending,
                    completedAtBottom = uiState.completedAtBottom,
                    completedMode = uiState.completedMode,
                    completedAscending = uiState.completedAscending,
                    selectedTaskIds = selectedTaskIds,
                    onComplete = { task ->
                        val wasCompleted = task.isCompleted
                        viewModel.setTaskCompleted(task, !wasCompleted)
                        showUndoSnackbar(
                            message = if (wasCompleted) "已标记为未完成" else "已完成任务",
                            onUndo = { viewModel.setTaskCompleted(task, wasCompleted) },
                        )
                    },
                    onDelete = { task ->
                        viewModel.deleteTask(task) { deletedTasks ->
                            showUndoSnackbar(
                                message = if (deletedTasks.size > 1) "任务和子任务已删除" else "任务已删除",
                                onUndo = { viewModel.restoreTasks(deletedTasks) },
                            )
                        }
                    },
                    onEdit = { viewModel.startEditing(it) },
                    onToggleSelection = { task ->
                        selectedTaskIds = if (task.id in selectedTaskIds) {
                            selectedTaskIds - task.id
                        } else {
                            selectedTaskIds + task.id
                        }
                    },
                    onEnterSelection = { task ->
                        selectedTaskIds = selectedTaskIds + task.id
                    },
                )
            }
        }

        if (selectedTaskIds.isNotEmpty()) {
            TaskSelectionToolbar(
                selectedCount = selectedTaskIds.size,
                onMove = { showBatchMoveDialog = true },
                onDate = { showBatchDateDialog = true },
                onPriority = { showBatchPriorityDialog = true },
                onTags = { showBatchTagsDialog = true },
                onComplete = {
                    val originalStates = selectedTasks.map { it to it.isCompleted }
                    viewModel.setTasksCompleted(selectedTasks, completed = true) {
                        showUndoSnackbar(
                            message = "已完成 ${selectedTasks.size} 个任务",
                            onUndo = { viewModel.restoreTaskCompletionStates(originalStates) },
                        )
                    }
                    selectedTaskIds = emptySet()
                },
                onDelete = {
                    val count = selectedTasks.size
                    viewModel.deleteTasks(selectedTasks) { deletedTasks ->
                        showUndoSnackbar(
                            message = "已删除 $count 个任务",
                            onUndo = { viewModel.restoreTasks(deletedTasks) },
                        )
                    }
                    selectedTaskIds = emptySet()
                },
                onCancel = { selectedTaskIds = emptySet() },
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }

        if (showFloatingActionButton) {
            FloatingActionButton(
                onClick = { viewModel.showCreateDialog() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 32.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Add Task",
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = if (showFloatingActionButton) 96.dp else 16.dp),
        )
    }

    if (uiState.showCreateDialog) {
        TaskEditorDialog(
            task = null,
            extras = TaskEditExtras(),
            settings = taskSettings,
            defaultListName = uiState.selectedTag ?: taskSettings.defaultListName.takeIf { it.isNotBlank() },
            onDismiss = { viewModel.hideCreateDialog() },
            onSave = { viewModel.createTask(it) },
        )
    }

    uiState.editingTask?.let { task ->
        TaskEditorDialog(
            task = task,
            extras = uiState.editingExtras,
            settings = taskSettings,
            defaultListName = null,
            onDismiss = { viewModel.stopEditing() },
            onSave = { viewModel.saveTask(task, it) },
        )
    }

    if (showBatchMoveDialog) {
        TaskBatchMoveDialog(
            selectedCount = selectedTaskIds.size,
            currentListName = uiState.selectedTag,
            availableTags = uiState.availableTags,
            onDismiss = { showBatchMoveDialog = false },
            onMove = { destination ->
                val count = selectedTasks.size
                viewModel.moveTasksToList(selectedTasks, destination) {
                    showInfoSnackbar("已移动 $count 个任务")
                }
                selectedTaskIds = emptySet()
                showBatchMoveDialog = false
            },
        )
    }

    if (showBatchDateDialog) {
        BatchDateActionDialog(
            selectedCount = selectedTaskIds.size,
            onDismiss = { showBatchDateDialog = false },
            onSetStart = {
                showBatchDateDialog = false
                batchDatePicker = EditorPicker.START
            },
            onSetDue = {
                showBatchDateDialog = false
                batchDatePicker = EditorPicker.DUE
            },
            onClearStart = {
                val count = selectedTasks.size
                viewModel.updateTasksStartDate(selectedTasks, 0L) {
                    showInfoSnackbar("已清除 $count 个任务的开始日期")
                }
                selectedTaskIds = emptySet()
                showBatchDateDialog = false
            },
            onClearDue = {
                val count = selectedTasks.size
                viewModel.updateTasksDueDate(selectedTasks, 0L) {
                    showInfoSnackbar("已清除 $count 个任务的截止日期")
                }
                selectedTaskIds = emptySet()
                showBatchDateDialog = false
            },
        )
    }

    batchDatePicker?.let { activePicker ->
        DateTimeEditorDialog(
            picker = activePicker,
            currentValue = 0L,
            startDate = 0L,
            dueDate = 0L,
            settings = taskSettings,
            onDismiss = { batchDatePicker = null },
            onConfirm = { value ->
                val count = selectedTasks.size
                when (activePicker) {
                    EditorPicker.START -> viewModel.updateTasksStartDate(selectedTasks, value) {
                        showInfoSnackbar("已更新 $count 个任务的开始日期")
                    }
                    EditorPicker.DUE -> viewModel.updateTasksDueDate(selectedTasks, value) {
                        showInfoSnackbar("已更新 $count 个任务的截止日期")
                    }
                    EditorPicker.REPEAT,
                    EditorPicker.REMINDER -> Unit
                }
                selectedTaskIds = emptySet()
                batchDatePicker = null
            },
        )
    }

    if (showBatchPriorityDialog) {
        BatchPriorityDialog(
            selectedCount = selectedTaskIds.size,
            onDismiss = { showBatchPriorityDialog = false },
            onPriority = { priority ->
                val count = selectedTasks.size
                viewModel.updateTasksPriority(selectedTasks, priority) {
                    showInfoSnackbar("已更新 $count 个任务的优先级")
                }
                selectedTaskIds = emptySet()
                showBatchPriorityDialog = false
            },
        )
    }

    if (showBatchTagsDialog) {
        BatchTagsDialog(
            selectedCount = selectedTaskIds.size,
            onDismiss = { showBatchTagsDialog = false },
            onConfirm = { tags, replace ->
                val count = selectedTasks.size
                viewModel.updateTasksTags(selectedTasks, tags, replace) {
                    showInfoSnackbar("已更新 $count 个任务的标签")
                }
                selectedTaskIds = emptySet()
                showBatchTagsDialog = false
            },
        )
    }
}

@Composable
private fun TaskErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(42.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = SaltTheme.colors.subText,
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onRetry) {
            Text("重试")
        }
    }
}

@Composable
private fun EmptyTasksView(
    query: String,
    selectedTag: String?,
    filter: TaskViewFilter,
    modifier: Modifier = Modifier,
) {
    val message = when {
        query.isNotBlank() -> "没有匹配的任务。"
        !selectedTag.isNullOrBlank() -> "这个清单下没有任务。"
        filter == TaskViewFilter.COMPLETED -> "还没有已完成任务。"
        filter != TaskViewFilter.ALL -> "这个筛选下没有任务。"
        else -> "这里没有任务哦。"
    }
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_empty_tasks),
            contentDescription = null,
            modifier = Modifier.size(96.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = SaltTheme.colors.subText,
        )
    }
}

@Composable
private fun TaskSelectionToolbar(
    selectedCount: Int,
    onMove: () -> Unit,
    onDate: () -> Unit,
    onPriority: () -> Unit,
    onTags: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        tonalElevation = 6.dp,
        shadowElevation = 6.dp,
        color = SaltTheme.colors.background,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "已选择 $selectedCount",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Medium,
            )
            TextButton(onClick = onMove) {
                Text("移动")
            }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "批量操作",
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("设置日期") },
                        onClick = {
                            menuExpanded = false
                            onDate()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("设置优先级") },
                        onClick = {
                            menuExpanded = false
                            onPriority()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("设置标签") },
                        onClick = {
                            menuExpanded = false
                            onTags()
                        },
                    )
                }
            }
            TextButton(onClick = onComplete) {
                Text("完成")
            }
            TextButton(onClick = onDelete) {
                Text("删除")
            }
            TextButton(onClick = onCancel) {
                Text("取消")
            }
        }
    }
}

@Composable
private fun TaskBatchMoveDialog(
    selectedCount: Int,
    currentListName: String?,
    availableTags: List<TaskDrawerTag>,
    onDismiss: () -> Unit,
    onMove: (String?) -> Unit,
) {
    val currentName = currentListName?.trim()?.takeIf { it.isNotBlank() }
    val tags = availableTags.sortedBy { it.name.lowercase(Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("移动到清单") },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                item {
                    Text(
                        text = "已选择 $selectedCount 个任务",
                        color = SaltTheme.colors.subText,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                if (currentName != null) {
                    item {
                        BatchMoveListRow(
                            name = "移出“$currentName”",
                            selected = false,
                            count = null,
                            onClick = { onMove(null) },
                        )
                    }
                }
                item {
                    BatchMoveListRow(
                        name = "我的任务",
                        selected = currentName == null,
                        count = null,
                        onClick = { onMove(null) },
                    )
                }
                tags.forEach { tag ->
                    item(key = "batch_move_${tag.name}") {
                        BatchMoveListRow(
                            name = tag.name,
                            selected = currentName.equals(tag.name, ignoreCase = true),
                            count = tag.count,
                            accentColor = tag.color?.takeIf { it != 0 }?.let { Color(it) },
                            iconLabel = tag.icon,
                            onClick = { onMove(tag.name) },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )
}

@Composable
private fun BatchDateActionDialog(
    selectedCount: Int,
    onDismiss: () -> Unit,
    onSetStart: () -> Unit,
    onSetDue: () -> Unit,
    onClearStart: () -> Unit,
    onClearDue: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("批量日期") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "已选择 $selectedCount 个任务",
                    color = SaltTheme.colors.subText,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                DialogChoice("设置开始日期", onSetStart)
                DialogChoice("设置截止日期", onSetDue)
                DialogChoice("清除开始日期", onClearStart)
                DialogChoice("清除截止日期", onClearDue)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )
}

@Composable
private fun BatchPriorityDialog(
    selectedCount: Int,
    onDismiss: () -> Unit,
    onPriority: (Int) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("批量优先级") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "已选择 $selectedCount 个任务",
                    color = SaltTheme.colors.subText,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                DialogChoice("无优先级") { onPriority(Task.Priority.NONE) }
                DialogChoice("低优先级") { onPriority(Task.Priority.LOW) }
                DialogChoice("中优先级") { onPriority(Task.Priority.MEDIUM) }
                DialogChoice("高优先级") { onPriority(Task.Priority.HIGH) }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )
}

@Composable
private fun BatchTagsDialog(
    selectedCount: Int,
    onDismiss: () -> Unit,
    onConfirm: (List<String>, Boolean) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    var replace by remember { mutableStateOf(false) }
    val tags = remember(text) { parseTags(text) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("批量标签") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "已选择 $selectedCount 个任务",
                    color = SaltTheme.colors.subText,
                    fontSize = 13.sp,
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("标签，用空格或逗号分隔") },
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DateTimeChip(
                        text = "追加",
                        selected = !replace,
                        onClick = { replace = false },
                    )
                    DateTimeChip(
                        text = "替换",
                        selected = replace,
                        onClick = { replace = true },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = tags.isNotEmpty(),
                onClick = { onConfirm(tags, replace) },
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )
}

@Composable
private fun BatchMoveListRow(
    name: String,
    selected: Boolean,
    count: Int?,
    accentColor: Color? = null,
    iconLabel: String? = null,
    onClick: () -> Unit,
) {
    val contentColor = if (selected) SaltTheme.colors.highlight else SaltTheme.colors.text
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) SaltTheme.colors.highlight.copy(alpha = 0.1f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TaskListAppearanceMark(
            color = accentColor,
            iconLabel = iconLabel,
            fallbackColor = SaltTheme.colors.subText,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = name,
            color = contentColor,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (count != null) {
            Text(
                text = count.toString(),
                color = SaltTheme.colors.subText,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
        if (selected) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = SaltTheme.colors.highlight,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun TaskListAppearanceMark(
    color: Color?,
    iconLabel: String?,
    fallbackColor: Color,
) {
    val accent = color ?: fallbackColor
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(accent.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center,
    ) {
        val label = taskListIconLabel(iconLabel)
        if (label.isNotBlank()) {
            Text(
                text = label,
                color = accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        } else {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(accent),
            )
        }
    }
}

@Composable
private fun TaskEditorDialog(
    task: Task?,
    extras: TaskEditExtras,
    settings: TaskSettings,
    defaultListName: String?,
    onDismiss: () -> Unit,
    onSave: (TaskEditData) -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val titleFocusRequester = remember { FocusRequester() }
    val editorScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val newTaskStartDate = remember(settings.defaultStartDate) {
        settings.defaultStartDate.toTaskMillis(endOfDay = false)
    }
    val newTaskDueDate = remember(settings.defaultDueDate) {
        settings.defaultDueDate.toTaskMillis(endOfDay = true)
    }
    var title by remember(task?.id) { mutableStateOf(task?.title.orEmpty()) }
    var startDate by remember(task?.id, newTaskStartDate) { mutableLongStateOf(task?.hideUntil ?: newTaskStartDate) }
    var dueDate by remember(task?.id, newTaskDueDate) { mutableLongStateOf(task?.dueDate ?: newTaskDueDate) }
    var recurrence by remember(task?.id, settings.defaultRecurrence) { mutableStateOf(task?.recurrence ?: settings.defaultRecurrence.toRRule()) }
    var repeatFrom by remember(task?.id) { mutableIntStateOf(task?.repeatFrom ?: Task.RepeatFrom.DUE_DATE) }
    var priority by remember(task?.id, settings.defaultPriority) {
        mutableIntStateOf(task?.priority ?: settings.defaultPriority.toTaskPriority())
    }
    var tagText by remember(task?.id, extras.tagNames, settings.defaultTags) {
        mutableStateOf(
            if (task == null) {
                settings.defaultTags
            } else {
                extras.tagNames.joinToString(" ")
            }
        )
    }
    var tagsTouched by remember(task?.id) { mutableStateOf(false) }
    var subtasks by remember(task?.id, extras.subtasks) { mutableStateOf(extras.subtasks) }
    var subtasksTouched by remember(task?.id) { mutableStateOf(false) }
    val defaultReminders = remember(task?.id, settings.defaultReminder, newTaskDueDate) {
        if (task == null) settings.defaultReminder.toReminderDrafts(newTaskDueDate) else emptyList()
    }
    var reminders by remember(task?.id, extras.reminders, defaultReminders) {
        mutableStateOf(if (task == null) defaultReminders else extras.reminders)
    }
    var remindersTouched by remember(task?.id) { mutableStateOf(false) }
    var notes by remember(task?.id) { mutableStateOf(task?.notes.orEmpty()) }
    var attachments by remember(task?.id, extras.attachments) { mutableStateOf(extras.attachments) }
    var attachmentsTouched by remember(task?.id) { mutableStateOf(false) }
    var addToCalendar by remember(task?.id, settings.defaultAddToCalendar) {
        mutableStateOf(if (task == null) settings.defaultAddToCalendar else task.calendarURI?.isNotBlank() == true)
    }
    val initialTagText = remember(task?.id, extras.tagNames, settings.defaultTags) {
        if (task == null) settings.defaultTags else extras.tagNames.joinToString(" ")
    }
    val initialSubtasks = remember(task?.id, extras.subtasks) { extras.subtasks }
    val initialReminders = remember(task?.id, extras.reminders, defaultReminders) {
        if (task == null) defaultReminders else extras.reminders
    }
    val initialAttachments = remember(task?.id, extras.attachments) { extras.attachments }

    var picker by remember { mutableStateOf<EditorPicker?>(null) }
    var editorSheet by remember { mutableStateOf<EditorSheetTarget?>(null) }
    var textEditor by remember { mutableStateOf<TextEditorTarget?>(null) }
    var showAttachmentsDialog by remember { mutableStateOf(false) }
    var showExactAlarmDialog by remember { mutableStateOf(false) }
    var showUnsavedChangesDialog by remember { mutableStateOf(false) }
    var saveRetryToken by remember { mutableIntStateOf(0) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            saveRetryToken++
        } else {
            Toast.makeText(context, "未授予通知权限，无法保存提醒", Toast.LENGTH_SHORT).show()
        }
    }
    val attachmentPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        editorScope.launch {
            val imported = withContext(Dispatchers.IO) {
                context.importTaskAttachment(uri)
            }
            if (imported != null) {
                attachments = attachments + imported
                attachmentsTouched = true
                return@launch
            }

            val permissionPersisted = runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }.isSuccess
            if (!permissionPersisted) {
                Toast.makeText(context, "附件导入失败，且权限无法持久化", Toast.LENGTH_SHORT).show()
            }
            attachments = attachments + TaskAttachmentDraft(
                name = context.displayName(uri),
                uri = uri.toString(),
            )
            attachmentsTouched = true
        }
    }
    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.all { it }) {
            saveRetryToken++
        } else {
            Toast.makeText(context, "未授予日历权限，任务不会写入日历", Toast.LENGTH_SHORT).show()
        }
    }
    val exactAlarmSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        saveRetryToken++
    }

    LaunchedEffect(task?.id) {
        delay(220)
        titleFocusRequester.requestFocus()
        keyboardController?.show()
    }

    fun requestReminderPermissions(targetReminders: List<TaskReminderDraft> = reminders): Boolean {
        if (targetReminders.isEmpty()) return true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return false
        }
        if (!context.canScheduleExactTaskAlarms()) {
            showExactAlarmDialog = true
            return false
        }
        return true
    }

    fun hasCalendarPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED

    fun requestCalendarPermissions(): Boolean {
        if (!addToCalendar || hasCalendarPermission()) return true
        calendarPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
            )
        )
        return false
    }

    fun hasUnsavedChanges(): Boolean =
        title != task?.title.orEmpty() ||
                startDate != (task?.hideUntil ?: newTaskStartDate) ||
                dueDate != (task?.dueDate ?: newTaskDueDate) ||
                recurrence != (task?.recurrence ?: settings.defaultRecurrence.toRRule()) ||
                repeatFrom != (task?.repeatFrom ?: Task.RepeatFrom.DUE_DATE) ||
                priority != (task?.priority ?: settings.defaultPriority.toTaskPriority()) ||
                tagText != initialTagText ||
                subtasks != initialSubtasks ||
                reminders != initialReminders ||
                notes != task?.notes.orEmpty() ||
                attachments != initialAttachments ||
                addToCalendar != if (task == null) settings.defaultAddToCalendar else task.calendarURI?.isNotBlank() == true

    fun requestCloseEditor() {
        if (hasUnsavedChanges()) {
            showUnsavedChangesDialog = true
        } else {
            onDismiss()
        }
    }

    fun save() {
        if (title.isBlank()) {
            Toast.makeText(context, "请输入任务名称", Toast.LENGTH_SHORT).show()
            titleFocusRequester.requestFocus()
            keyboardController?.show()
            return
        }
        if (startDate > 0 && dueDate > 0 && startDate > dueDate) {
            Toast.makeText(context, "开始日期不能晚于截止日期", Toast.LENGTH_SHORT).show()
            return
        }
        if (!requestReminderPermissions()) return
        if (!requestCalendarPermissions()) return
        keyboardController?.hide()
        onSave(
            TaskEditData(
                title = title.trim(),
                notes = notes.trim().ifBlank { null },
                priority = priority,
                startDate = startDate,
                dueDate = dueDate,
                recurrence = recurrence,
                repeatFrom = repeatFrom,
                tagNames = if (tagsTouched || task == null) {
                    mergeTaskTagList(parseTags(tagText), if (task == null) defaultListName else null)
                } else {
                    null
                },
                subtasks = if (subtasksTouched || task == null) subtasks else null,
                reminders = if (remindersTouched || task == null) reminders else null,
                attachments = if (attachmentsTouched || task == null) attachments else null,
                addToCalendar = addToCalendar,
                calendarEndAtDueTime = settings.calendarEndAtDueTime,
            )
        )
    }

    LaunchedEffect(saveRetryToken) {
        if (saveRetryToken > 0) {
            save()
        }
    }

    BackHandler {
        if (settings.backButtonSavesTask) save() else requestCloseEditor()
    }

    Dialog(
        onDismissRequest = {
            if (settings.backButtonSavesTask) save() else requestCloseEditor()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(color = SaltTheme.colors.background) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(scrollState)
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = ::save) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = "保存任务",
                            tint = SaltTheme.colors.text,
                        )
                    }
                }

                TaskTitleRow(
                    title = title,
                    onTitleChange = { title = it },
                    completed = task?.isCompleted == true,
                    multilineTitle = settings.multilineTaskTitle,
                    focusRequester = titleFocusRequester,
                    onDone = { save() },
                )
                TaskEditorRow(
                    icon = Icons.Outlined.EventAvailable,
                    primary = if (startDate > 0) formatEditorTimestamp(startDate, settings) else "无开始日期",
                    secondary = "开始日期",
                    onClick = { picker = EditorPicker.START },
                )
                TaskEditorRow(
                    icon = Icons.Outlined.AccessTime,
                    primary = if (dueDate > 0) formatEditorTimestamp(dueDate, settings) else "无截止日期",
                    secondary = "截止日期",
                    onClick = { picker = EditorPicker.DUE },
                )
                TaskEditorRow(
                    icon = Icons.Outlined.Repeat,
                    primary = recurrenceLabel(recurrence),
                    secondary = "重复",
                    onClick = { editorSheet = EditorSheetTarget.REPEAT },
                )
                PriorityRow(
                    priority = priority,
                    onPriorityChange = { priority = it },
                )
                InlineSubtasksSection(
                    subtasks = subtasks,
                    onSubtasksChange = {
                        subtasks = it
                        subtasksTouched = true
                    },
                )
                InlineReminderSection(
                    reminders = reminders,
                    startDate = startDate,
                    dueDate = dueDate,
                    settings = settings,
                    isNew = task == null,
                    onRemindersChange = {
                        val next = it.dedupeReminders()
                        reminders = next
                        remindersTouched = true
                        if (next.isNotEmpty()) {
                            requestReminderPermissions(next)
                        }
                    },
                )
                TaskEditorRow(
                    icon = Icons.AutoMirrored.Outlined.Notes,
                    primary = "描述",
                    secondary = notes.ifBlank { "无描述" },
                    onClick = { editorSheet = EditorSheetTarget.NOTES },
                )
                TaskEditorRow(
                    icon = Icons.Outlined.AttachFile,
                    primary = attachmentSummary(attachments),
                    secondary = "附件",
                    onClick = { editorSheet = EditorSheetTarget.ATTACHMENT },
                )
                TaskEditorRow(
                    icon = Icons.Outlined.CalendarMonth,
                    primary = if (addToCalendar) "添加到日历" else "不添加到日历",
                    secondary = "日历",
                    onClick = { editorSheet = EditorSheetTarget.CALENDAR },
                )
                TaskInfoRow(task = task, settings = settings)
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    editorSheet?.let { target ->
        TaskEditorActionSheet(
            target = target,
            startDate = startDate,
            dueDate = dueDate,
            priority = priority,
            tagText = tagText,
            notes = notes,
            recurrence = recurrence,
            repeatFrom = repeatFrom,
            reminders = reminders,
            attachments = attachments,
            addToCalendar = addToCalendar,
            settings = settings,
            onDismiss = { editorSheet = null },
            onOpenDatePicker = {
                editorSheet = null
                picker = it
            },
            onStartDateSelected = {
                startDate = it
                editorSheet = null
            },
            onDueDateSelected = {
                dueDate = it
                if (task == null && !remindersTouched) {
                    reminders = settings.defaultReminder.toReminderDrafts(it)
                }
                editorSheet = null
            },
            onPrioritySelected = {
                priority = it
                editorSheet = null
            },
            onCalendarSelected = {
                addToCalendar = it
                if (it) requestCalendarPermissions()
                editorSheet = null
            },
            onOpenTextEditor = {
                editorSheet = null
                textEditor = it
            },
            onClearTags = {
                tagText = ""
                tagsTouched = true
                editorSheet = null
            },
            onClearNotes = {
                notes = ""
                editorSheet = null
            },
            onSetRepeat = { newRecurrence, newRepeatFrom ->
                recurrence = newRecurrence
                repeatFrom = newRepeatFrom
                editorSheet = null
            },
            onOpenRepeatEditor = {
                editorSheet = null
                editorScope.launch {
                    delay(300)
                    picker = EditorPicker.REPEAT
                }
            },
            onAddReminder = {
                reminders = (reminders + it).dedupeReminders()
                remindersTouched = true
                editorSheet = null
                requestReminderPermissions()
            },
            onClearReminders = {
                reminders = emptyList()
                remindersTouched = true
                editorSheet = null
            },
            onOpenReminders = {
                editorSheet = null
            },
            onOpenAttachments = {
                editorSheet = null
                showAttachmentsDialog = true
            },
            onAddAttachment = {
                editorSheet = null
                attachmentPicker.launch(arrayOf("*/*"))
            },
            onClearAttachments = {
                attachments = emptyList()
                attachmentsTouched = true
                editorSheet = null
            },
        )
    }

    picker?.let { activePicker ->
        if (activePicker == EditorPicker.REPEAT) {
            RepeatEditorDialog(
                recurrence = recurrence,
                repeatFrom = repeatFrom,
                onDismiss = { picker = null },
                onConfirm = { newRecurrence, newRepeatFrom ->
                    recurrence = newRecurrence
                    repeatFrom = newRepeatFrom
                    picker = null
                },
            )
        } else {
            DateTimeEditorDialog(
                picker = activePicker,
                currentValue = when (activePicker) {
                    EditorPicker.START -> startDate
                    EditorPicker.DUE -> dueDate
                    EditorPicker.REMINDER -> 0L
                    EditorPicker.REPEAT -> 0L
                },
                startDate = startDate,
                dueDate = dueDate,
                settings = settings,
                onDismiss = { picker = null },
                onConfirm = {
                    when (activePicker) {
                        EditorPicker.START -> startDate = it
                        EditorPicker.DUE -> {
                            dueDate = it
                            if (task == null && !remindersTouched) {
                                reminders = settings.defaultReminder.toReminderDrafts(it)
                            }
                        }
                        EditorPicker.REMINDER -> {
                            if (it > 0) {
                                reminders = (reminders + TaskReminderDraft(time = it, type = Alarm.TYPE_DATE_TIME))
                                    .dedupeReminders()
                                remindersTouched = true
                                requestReminderPermissions()
                            }
                        }
                        EditorPicker.REPEAT -> {}
                    }
                    picker = null
                },
            )
        }
    }



    textEditor?.let { target ->
        TextValueDialog(
            title = when (target) {
                TextEditorTarget.TAGS -> "添加标签"
                TextEditorTarget.NOTES -> "描述"
            },
            value = when (target) {
                TextEditorTarget.TAGS -> tagText
                TextEditorTarget.NOTES -> notes
            },
            singleLine = target != TextEditorTarget.NOTES,
            onDismiss = { textEditor = null },
            onConfirm = {
                when (target) {
                    TextEditorTarget.TAGS -> {
                        tagText = it
                        tagsTouched = true
                    }
                    TextEditorTarget.NOTES -> notes = it
                }
                textEditor = null
            },
        )
    }

    if (showAttachmentsDialog) {
        AttachmentsDialog(
            attachments = attachments,
            onAdd = { attachmentPicker.launch(arrayOf("*/*")) },
            onOpen = { context.openAttachment(it) },
            onRemove = { removed ->
                attachments = attachments.filterNot { it == removed }
                attachmentsTouched = true
            },
            onDismiss = { showAttachmentsDialog = false },
        )
    }

    if (showUnsavedChangesDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedChangesDialog = false },
            title = { Text("放弃更改？") },
            text = { Text("当前任务有未保存的更改。") },
            confirmButton = {
                TextButton(onClick = {
                    showUnsavedChangesDialog = false
                    onDismiss()
                }) {
                    Text("放弃")
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { showUnsavedChangesDialog = false }) {
                        Text("继续编辑")
                    }
                    TextButton(onClick = {
                        showUnsavedChangesDialog = false
                        save()
                    }) {
                        Text("保存")
                    }
                }
            },
        )
    }

    if (showExactAlarmDialog) {
        AlertDialog(
            onDismissRequest = { showExactAlarmDialog = false },
            title = { Text("需要提醒权限") },
            text = { Text("系统需要允许精确闹钟后，任务提醒才能准时触发。") },
            confirmButton = {
                TextButton(onClick = {
                    showExactAlarmDialog = false
                    exactAlarmSettingsLauncher.launch(context.exactAlarmSettingsIntent())
                }) {
                    Text("去设置")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExactAlarmDialog = false }) {
                    Text("稍后")
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskEditorActionSheet(
    target: EditorSheetTarget,
    startDate: Long,
    dueDate: Long,
    priority: Int,
    tagText: String,
    notes: String,
    recurrence: String?,
    repeatFrom: Int,
    reminders: List<TaskReminderDraft>,
    attachments: List<TaskAttachmentDraft>,
    addToCalendar: Boolean,
    settings: TaskSettings,
    onDismiss: () -> Unit,
    onOpenDatePicker: (EditorPicker) -> Unit,
    onStartDateSelected: (Long) -> Unit,
    onDueDateSelected: (Long) -> Unit,
    onPrioritySelected: (Int) -> Unit,
    onCalendarSelected: (Boolean) -> Unit,
    onOpenTextEditor: (TextEditorTarget) -> Unit,
    onClearTags: () -> Unit,
    onClearNotes: () -> Unit,
    onSetRepeat: (String?, Int) -> Unit,
    onOpenRepeatEditor: () -> Unit,
    onAddReminder: (TaskReminderDraft) -> Unit,
    onClearReminders: () -> Unit,
    onOpenReminders: () -> Unit,
    onOpenAttachments: () -> Unit,
    onAddAttachment: () -> Unit,
    onClearAttachments: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val today = remember { LocalDate.now() }
    val dueDateTime = remember(dueDate) {
        dueDate.takeIf { it > 0 }
            ?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 12.dp),
        ) {
            when (target) {
                EditorSheetTarget.DUE_DATE -> {
                    TaskEditorSheetHeader(
                        title = "截止日期",
                        subtitle = if (dueDate > 0) formatEditorTimestamp(dueDate, settings) else "无截止日期",
                    )
                    TaskEditorSheetRow(Icons.Outlined.CalendarToday, "今天", "今晚 23:59") {
                        onDueDateSelected(today.quickDateMillis(EditorPicker.DUE))
                    }
                    TaskEditorSheetRow(Icons.Outlined.CalendarToday, "明天", "明晚 23:59") {
                        onDueDateSelected(today.plusDays(1).quickDateMillis(EditorPicker.DUE))
                    }
                    TaskEditorSheetRow(Icons.Outlined.CalendarToday, "下周", "7 天后 23:59") {
                        onDueDateSelected(today.plusWeeks(1).quickDateMillis(EditorPicker.DUE))
                    }
                    TaskEditorSheetRow(Icons.Outlined.AccessTime, "选择日期和时间", "打开完整日期/时间面板") {
                        onOpenDatePicker(EditorPicker.DUE)
                    }
                    TaskEditorSheetRow(Icons.Outlined.CheckCircle, "无截止日期", null, selected = dueDate == 0L) {
                        onDueDateSelected(0L)
                    }
                }

                EditorSheetTarget.START_DATE -> {
                    TaskEditorSheetHeader(
                        title = "开始日期",
                        subtitle = if (startDate > 0) formatEditorTimestamp(startDate, settings) else "无开始日期",
                    )
                    TaskEditorSheetRow(Icons.Outlined.EventAvailable, "今天", "上午 9:00") {
                        onStartDateSelected(today.quickDateMillis(EditorPicker.START))
                    }
                    TaskEditorSheetRow(Icons.Outlined.EventAvailable, "明天", "上午 9:00") {
                        onStartDateSelected(today.plusDays(1).quickDateMillis(EditorPicker.START))
                    }
                    if (dueDateTime != null) {
                        TaskEditorSheetRow(Icons.Outlined.AccessTime, "按截止日期", formatEditorTimestamp(dueDate, settings)) {
                            onStartDateSelected(dueDate)
                        }
                        TaskEditorSheetRow(Icons.Outlined.AccessTime, "截止前一天", null) {
                            onStartDateSelected(dueDateTime.minusDays(1).toMillis())
                        }
                        TaskEditorSheetRow(Icons.Outlined.AccessTime, "截止前一周", null) {
                            onStartDateSelected(dueDateTime.minusWeeks(1).toMillis())
                        }
                    }
                    TaskEditorSheetRow(Icons.Outlined.AccessTime, "选择日期和时间", "打开完整日期/时间面板") {
                        onOpenDatePicker(EditorPicker.START)
                    }
                    TaskEditorSheetRow(Icons.Outlined.CheckCircle, "无开始日期", null, selected = startDate == 0L) {
                        onStartDateSelected(0L)
                    }
                }

                EditorSheetTarget.PRIORITY -> {
                    TaskEditorSheetHeader(title = "优先级", subtitle = priorityLabel(priority))
                    priorityOptions().forEach { option ->
                        TaskEditorSheetRow(
                            icon = Icons.Outlined.Flag,
                            title = option.label,
                            subtitle = option.subtitle,
                            selected = priority == option.value,
                            accent = option.color,
                        ) {
                            onPrioritySelected(option.value)
                        }
                    }
                }

                EditorSheetTarget.CALENDAR -> {
                    TaskEditorSheetHeader(
                        title = "日历",
                        subtitle = if (addToCalendar) "添加到日历" else "不添加到日历",
                    )
                    TaskEditorSheetRow(Icons.Outlined.CalendarMonth, "添加到日历", "保存时写入系统日历", selected = addToCalendar) {
                        onCalendarSelected(true)
                    }
                    TaskEditorSheetRow(Icons.Outlined.CalendarMonth, "不添加到日历", null, selected = !addToCalendar) {
                        onCalendarSelected(false)
                    }
                }

                EditorSheetTarget.NOTES -> {
                    TaskEditorSheetHeader(
                        title = "描述",
                        subtitle = notes.ifBlank { "无描述" },
                    )
                    TaskEditorSheetRow(Icons.AutoMirrored.Outlined.Notes, "编辑描述", "进入文本编辑") {
                        onOpenTextEditor(TextEditorTarget.NOTES)
                    }
                    if (notes.isNotBlank()) {
                        TaskEditorSheetRow(Icons.Filled.Delete, "清空描述") {
                            onClearNotes()
                        }
                    }
                }

                EditorSheetTarget.TAGS -> {
                    TaskEditorSheetHeader(
                        title = "标签",
                        subtitle = tagText.ifBlank { "未添加标签" },
                    )
                    TaskEditorSheetRow(Icons.AutoMirrored.Outlined.Label, "编辑标签", "多个标签可用空格、逗号或 # 分隔") {
                        onOpenTextEditor(TextEditorTarget.TAGS)
                    }
                    if (tagText.isNotBlank()) {
                        TaskEditorSheetRow(Icons.Filled.Delete, "清空标签") {
                            onClearTags()
                        }
                    }
                }

                EditorSheetTarget.REPEAT -> {
                    TaskEditorSheetHeader(title = "重复", subtitle = recurrenceLabel(recurrence))
                    TaskEditorSheetRow(Icons.Outlined.CheckCircle, "不重复", null, selected = recurrence == null) {
                        onSetRepeat(null, repeatFrom)
                    }
                    TaskEditorSheetRow(Icons.Outlined.Repeat, "每天", null, selected = recurrence == "RRULE:FREQ=DAILY") {
                        onSetRepeat("RRULE:FREQ=DAILY", Task.RepeatFrom.DUE_DATE)
                    }
                    TaskEditorSheetRow(Icons.Outlined.Repeat, "每周", null, selected = recurrence == "RRULE:FREQ=WEEKLY") {
                        onSetRepeat("RRULE:FREQ=WEEKLY", Task.RepeatFrom.DUE_DATE)
                    }
                    TaskEditorSheetRow(Icons.Outlined.Repeat, "每月", null, selected = recurrence == "RRULE:FREQ=MONTHLY") {
                        onSetRepeat("RRULE:FREQ=MONTHLY", Task.RepeatFrom.DUE_DATE)
                    }
                    TaskEditorSheetRow(Icons.Outlined.Repeat, "自定义重复周期", "频率、间隔、周几、结束条件") {
                        onOpenRepeatEditor()
                    }
                }

                EditorSheetTarget.REMINDER -> {
                    TaskEditorSheetHeader(
                        title = "提醒",
                        subtitle = reminderSummary(reminders, startDate, dueDate, settings),
                    )
                    TaskEditorSheetRow(Icons.Outlined.Notifications, "管理提醒", "查看、编辑、自定义、随机提醒") {
                        onOpenReminders()
                    }
                    if (startDate > 0) {
                        TaskEditorSheetRow(Icons.Outlined.Notifications, "开始时", formatEditorTimestamp(startDate, settings)) {
                            onAddReminder(TaskReminderDraft(time = 0L, type = Alarm.TYPE_REL_START))
                        }
                        TaskEditorSheetRow(Icons.Outlined.Notifications, "开始前 10 分钟") {
                            onAddReminder(TaskReminderDraft(time = -TEN_MINUTES_MILLIS, type = Alarm.TYPE_REL_START))
                        }
                    }
                    if (dueDate > 0) {
                        TaskEditorSheetRow(Icons.Outlined.Notifications, "到期时", formatEditorTimestamp(dueDate, settings)) {
                            onAddReminder(TaskReminderDraft(time = 0L, type = Alarm.TYPE_REL_END))
                        }
                        TaskEditorSheetRow(Icons.Outlined.Notifications, "提前 10 分钟") {
                            onAddReminder(TaskReminderDraft(time = -TEN_MINUTES_MILLIS, type = Alarm.TYPE_REL_END))
                        }
                    }
                    TaskEditorSheetRow(Icons.Outlined.AccessTime, "选择具体时间", "打开日期/时间面板") {
                        onOpenDatePicker(EditorPicker.REMINDER)
                    }
                    if (reminders.isNotEmpty()) {
                        TaskEditorSheetRow(Icons.Filled.Delete, "清空提醒") {
                            onClearReminders()
                        }
                    }
                }

                EditorSheetTarget.ATTACHMENT -> {
                    TaskEditorSheetHeader(title = "附件", subtitle = attachmentSummary(attachments))
                    TaskEditorSheetRow(Icons.Outlined.AttachFile, "管理附件", "打开、删除已添加附件") {
                        onOpenAttachments()
                    }
                    TaskEditorSheetRow(Icons.Outlined.AttachFile, "添加附件") {
                        onAddAttachment()
                    }
                    if (attachments.isNotEmpty()) {
                        TaskEditorSheetRow(Icons.Filled.Delete, "清空附件") {
                            onClearAttachments()
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun TaskEditorSheetHeader(
    title: String,
    subtitle: String? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = SaltTheme.colors.text,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = SaltTheme.colors.subText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun TaskEditorSheetRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    selected: Boolean = false,
    accent: Color = SaltTheme.colors.subText,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) SaltTheme.colors.highlight else accent,
            modifier = Modifier.size(26.dp),
        )
        Spacer(modifier = Modifier.width(22.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (selected) SaltTheme.colors.highlight else SaltTheme.colors.text,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = SaltTheme.colors.subText,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (selected) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = SaltTheme.colors.highlight,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

private data class PriorityOption(
    val value: Int,
    val label: String,
    val subtitle: String?,
    val color: Color,
)

private fun priorityOptions(): List<PriorityOption> =
    listOf(
        PriorityOption(Task.Priority.NONE, "无优先级", null, Color(0xFF8E8E8E)),
        PriorityOption(Task.Priority.LOW, "低优先级", null, Color(0xFF4F9DD6)),
        PriorityOption(Task.Priority.MEDIUM, "中优先级", null, Color(0xFFE5C94D)),
        PriorityOption(Task.Priority.HIGH, "高优先级", null, Color(0xFFD56060)),
    )

private fun priorityLabel(priority: Int): String =
    priorityOptions().firstOrNull { it.value == priority }?.label ?: "无优先级"

@Composable
private fun TaskInfoRow(task: Task?, settings: TaskSettings) {
    val now = remember { System.currentTimeMillis() }
    val created = task?.creationDate?.takeIf { it > 0 } ?: now
    val modified = task?.modificationDate?.takeIf { it > 0 } ?: now

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 20.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = SaltTheme.colors.subText,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(32.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "已创建的 ${formatInfoTimestamp(created, settings)}",
                color = SaltTheme.colors.text,
                fontSize = 14.sp,
            )
            Text(
                text = "已修改的 ${formatInfoTimestamp(modified, settings)}",
                color = SaltTheme.colors.text,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun TaskTitleRow(
    title: String,
    completed: Boolean,
    multilineTitle: Boolean,
    focusRequester: FocusRequester,
    onTitleChange: (String) -> Unit,
    onDone: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (completed) Icons.Outlined.CheckCircle else Icons.Outlined.CheckBoxOutlineBlank,
            contentDescription = null,
            tint = SaltTheme.colors.subText,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(32.dp))
        BasicTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            singleLine = !multilineTitle,
            maxLines = if (multilineTitle) 3 else 1,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                textDecoration = if (completed) TextDecoration.LineThrough else TextDecoration.None,
            ),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (title.isBlank()) {
                        Text(
                            text = "任务名称",
                            style = MaterialTheme.typography.bodyLarge,
                            color = SaltTheme.colors.text.copy(alpha = 0.38f),
                        )
                    }
                    innerTextField()
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = if (multilineTitle) ImeAction.Default else ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(start = 76.dp),
        color = SaltTheme.colors.stroke.copy(alpha = 0.16f),
    )
}

@Composable
private fun TaskEditorRow(
    icon: ImageVector,
    primary: String,
    modifier: Modifier = Modifier,
    secondary: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SaltTheme.colors.subText,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(32.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = primary,
                color = SaltTheme.colors.text.copy(alpha = if (primary.startsWith("添加") || primary.startsWith("无") || primary == "描述") 0.48f else 1f),
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (secondary != null) {
                Text(
                    text = secondary,
                    color = SaltTheme.colors.text.copy(alpha = 0.45f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        trailing?.invoke()
    }
    HorizontalDivider(
        modifier = Modifier.padding(start = 76.dp),
        color = SaltTheme.colors.stroke.copy(alpha = 0.16f),
    )
}

@Composable
private fun PriorityRow(
    priority: Int,
    onPriorityChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Flag,
            contentDescription = null,
            tint = SaltTheme.colors.subText,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(32.dp))
        Text(
            text = "优先级",
            color = SaltTheme.colors.text,
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            for (i in Task.Priority.NONE downTo Task.Priority.HIGH) {
                PriorityButton(
                    priority = i,
                    selected = priority,
                    onClick = onPriorityChange,
                )
            }
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(start = 76.dp),
        color = SaltTheme.colors.stroke.copy(alpha = 0.16f),
    )
}

@Composable
private fun PriorityButton(
    priority: Int,
    selected: Int,
    onClick: (Int) -> Unit,
) {
    val color = when (priority) {
        Task.Priority.HIGH   -> Color(0xFFD56060)
        Task.Priority.MEDIUM -> Color(0xFFE5C94D)
        Task.Priority.LOW    -> Color(0xFF4F9DD6)
        else                 -> Color(0xFF8E8E8E)
    }
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentSize provides Dp.Unspecified,
    ) {
        RadioButton(
            selected = priority == selected,
            onClick = { onClick(priority) },
            colors = RadioButtonDefaults.colors(
                selectedColor = color,
                unselectedColor = color,
            ),
            modifier = Modifier.padding(vertical = 20.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimeEditorDialog(
    picker: EditorPicker,
    currentValue: Long,
    startDate: Long,
    dueDate: Long,
    settings: TaskSettings,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
) {
    val now = remember { LocalDateTime.now() }
    val initial = remember(currentValue, dueDate, picker) {
        currentValue.takeIf { it > 0 }
            ?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime() }
            ?: dueDate.takeIf { picker == EditorPicker.START && it > 0 }
                ?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime() }
            ?: now
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initial.toLocalDate().toDatePickerMillis()
    )
    val initialDaySelection = remember(currentValue, dueDate, picker) {
        when {
            picker == EditorPicker.START && currentValue > 0 && dueDate > 0 ->
                currentValue.toStartDateSelection(dueDate)
            currentValue > 0 -> initial.toLocalDate().toStartOfDayMillis()
            else -> initial.toLocalDate().toStartOfDayMillis()
        }
    }
    var selectedDay by remember(initialDaySelection) {
        mutableLongStateOf(initialDaySelection)
    }
    var selectedTime by remember(currentValue, picker, initialDaySelection) {
        mutableIntStateOf(
            when {
                initialDaySelection == START_DUE_TIME -> EDITOR_NO_TIME
                picker == EditorPicker.REMINDER -> initial.toMillisOfDay()
                currentValue.hasTaskTimeMarker() -> initial.toMillisOfDay()
                else -> EDITOR_NO_TIME
            }
        )
    }
    val today = LocalDate.now()
    var showTimePicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val title = when (picker) {
        EditorPicker.START -> "开始日期"
        EditorPicker.DUE -> "截止日期"
        EditorPicker.REMINDER -> "提醒"
        EditorPicker.REPEAT -> ""
    }
    val selectedDateFromCalendar = datePickerState.selectedDateMillis
        ?.toDatePickerLocalDate()
        ?: initial.toLocalDate()

    LaunchedEffect(datePickerState.selectedDateMillis) {
        if (selectedDay >= EDITOR_NO_DAY) {
            selectedDay = selectedDateFromCalendar.toStartOfDayMillis()
        }
    }

    fun selectCalendarDate(date: LocalDate) {
        selectedDay = date.toStartOfDayMillis()
        datePickerState.selectedDateMillis = date.toDatePickerMillis()
    }

    fun finishSelection() {
        val customDay = if (selectedDay > 0) {
            selectedDateFromCalendar.toStartOfDayMillis()
        } else {
            selectedDay
        }
        val value = when (picker) {
            EditorPicker.DUE -> duePickerSelectionToMillis(customDay, selectedTime)
            EditorPicker.START -> startPickerSelectionToMillis(customDay, selectedTime, dueDate)
            EditorPicker.REMINDER -> duePickerSelectionToMillis(customDay, selectedTime.takeIf { it > 0 } ?: NINE_AM_MILLIS_OF_DAY)
            EditorPicker.REPEAT -> 0L
        }
        onConfirm(value)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            DateTimeShortcutGrid(
                dateShortcuts = {
                    when (picker) {
                        EditorPicker.START -> {
                            if (dueDate > 0) {
                                DateTimeShortcutButton(
                                    icon = Icons.Outlined.Today,
                                    text = "截止日期",
                                    selected = selectedDay == START_DUE_DATE,
                                ) {
                                    selectedDay = START_DUE_DATE
                                }
                                DateTimeShortcutButton(
                                    icon = Icons.Outlined.Schedule,
                                    text = "截止时间",
                                    selected = selectedDay == START_DUE_TIME,
                                ) {
                                    selectedDay = START_DUE_TIME
                                    selectedTime = EDITOR_NO_TIME
                                }
                                DateTimeShortcutButton(
                                    icon = Icons.Outlined.WbSunny,
                                    text = "截止日期前一天",
                                    selected = selectedDay == START_DAY_BEFORE_DUE,
                                ) {
                                    selectedDay = START_DAY_BEFORE_DUE
                                }
                                DateTimeShortcutButton(
                                    icon = Icons.Outlined.CalendarViewWeek,
                                    text = "截止日期前一周",
                                    selected = selectedDay == START_WEEK_BEFORE_DUE,
                                ) {
                                    selectedDay = START_WEEK_BEFORE_DUE
                                }
                            } else {
                                DateTimeShortcutButton(Icons.Outlined.Today, "今天", selectedDay == today.toStartOfDayMillis()) {
                                    selectCalendarDate(today)
                                }
                                DateTimeShortcutButton(Icons.Outlined.WbSunny, "明天", selectedDay == today.plusDays(1).toStartOfDayMillis()) {
                                    selectCalendarDate(today.plusDays(1))
                                }
                                DateTimeShortcutButton(Icons.Outlined.CalendarViewWeek, "下周", selectedDay == today.plusWeeks(1).toStartOfDayMillis()) {
                                    selectCalendarDate(today.plusWeeks(1))
                                }
                            }
                            DateTimeShortcutButton(
                                icon = Icons.Outlined.Block,
                                text = "无日期",
                                selected = selectedDay == EDITOR_NO_DAY,
                            ) {
                                selectedDay = EDITOR_NO_DAY
                                selectedTime = EDITOR_NO_TIME
                            }
                        }

                        EditorPicker.DUE, EditorPicker.REMINDER -> {
                            DateTimeShortcutButton(Icons.Outlined.Today, "今天", selectedDay == today.toStartOfDayMillis()) {
                                selectCalendarDate(today)
                            }
                            DateTimeShortcutButton(Icons.Outlined.WbSunny, "明天", selectedDay == today.plusDays(1).toStartOfDayMillis()) {
                                selectCalendarDate(today.plusDays(1))
                            }
                            DateTimeShortcutButton(Icons.Outlined.CalendarViewWeek, "下周", selectedDay == today.plusWeeks(1).toStartOfDayMillis()) {
                                selectCalendarDate(today.plusWeeks(1))
                            }
                            DateTimeShortcutButton(
                                icon = Icons.Outlined.Block,
                                text = if (picker == EditorPicker.REMINDER) "无提醒" else "无日期",
                                selected = selectedDay == EDITOR_NO_DAY,
                            ) {
                                selectedDay = EDITOR_NO_DAY
                                selectedTime = EDITOR_NO_TIME
                            }
                        }

                        EditorPicker.REPEAT -> Unit
                    }
                },
                timeShortcuts = {
                    DateTimeShortcutButton(
                        icon = Icons.Outlined.Coffee,
                        text = "上午 9",
                        selected = selectedTime == NINE_AM_MILLIS_OF_DAY,
                    ) {
                        selectedTime = NINE_AM_MILLIS_OF_DAY
                    }
                    DateTimeShortcutButton(
                        icon = Icons.Outlined.WbSunny,
                        text = "下午 1",
                        selected = selectedTime == ONE_PM_MILLIS_OF_DAY,
                    ) {
                        selectedTime = ONE_PM_MILLIS_OF_DAY
                    }
                    DateTimeShortcutButton(
                        icon = Icons.Outlined.WbTwilight,
                        text = "下午 5",
                        selected = selectedTime == FIVE_PM_MILLIS_OF_DAY,
                    ) {
                        selectedTime = FIVE_PM_MILLIS_OF_DAY
                    }
                    DateTimeShortcutButton(
                        icon = Icons.Outlined.NightsStay,
                        text = "下午 8",
                        selected = selectedTime == EIGHT_PM_MILLIS_OF_DAY,
                    ) {
                        selectedTime = EIGHT_PM_MILLIS_OF_DAY
                    }
                    DateTimeShortcutButton(Icons.Outlined.AccessTime, "挑选时间", selected = false) {
                        showTimePicker = true
                    }
                    if (picker != EditorPicker.REMINDER) {
                        DateTimeShortcutButton(
                            icon = Icons.Outlined.Block,
                            text = "无时间",
                            selected = selectedTime == EDITOR_NO_TIME && selectedDay != START_DUE_TIME,
                        ) {
                            selectedTime = EDITOR_NO_TIME
                        }
                    }
                },
            )
            HorizontalDivider(color = SaltTheme.colors.stroke.copy(alpha = 0.25f))
            Column(
                modifier = Modifier
                    .heightIn(max = 470.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                DatePicker(
                    state = datePickerState,
                    title = {},
                    showModeToggle = false,
                    colors = DatePickerDefaults.colors(
                        containerColor = SaltTheme.colors.background,
                    ),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val displayMode = datePickerState.displayMode
                IconButton(
                    onClick = {
                        datePickerState.displayMode = if (displayMode == DisplayMode.Input) {
                            DisplayMode.Picker
                        } else {
                            DisplayMode.Input
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (displayMode == DisplayMode.Input) {
                            Icons.Outlined.CalendarToday
                        } else {
                            Icons.Rounded.Edit
                        },
                        contentDescription = null,
                        tint = SaltTheme.colors.highlight,
                        modifier = Modifier.size(26.dp),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    TextButton(onClick = ::finishSelection) {
                        Text("确定")
                    }
                }
            }
        }
    }

    if (showTimePicker) {
        EditorTimePickerDialog(
            initialMillisOfDay = selectedTime.takeIf { it > 0 } ?: NINE_AM_MILLIS_OF_DAY,
            is24Hour = settings.use24HourTime,
            onDismiss = { showTimePicker = false },
            onConfirm = {
                selectedTime = it
                showTimePicker = false
            },
        )
    }
}

@Composable
private fun DateTimeShortcutGrid(
    dateShortcuts: @Composable ColumnScope.() -> Unit,
    timeShortcuts: @Composable ColumnScope.() -> Unit,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.widthIn(max = screenWidth / 2),
        ) {
            dateShortcuts()
        }
        Spacer(modifier = Modifier.weight(1f))
        Column {
            timeShortcuts()
        }
    }
}

@Composable
private fun DateTimeShortcutButton(
    icon: ImageVector,
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val color = if (selected) SaltTheme.colors.highlight else SaltTheme.colors.text
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = color),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
            )
            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorTimePickerDialog(
    initialMillisOfDay: Int,
    is24Hour: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    val initialHour = (initialMillisOfDay / HOUR_MILLIS_OF_DAY).coerceIn(0, 23)
    val initialMinute = ((initialMillisOfDay % HOUR_MILLIS_OF_DAY) / MINUTE_MILLIS_OF_DAY).coerceIn(0, 59)
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            TimePicker(state = state)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(state.hour * HOUR_MILLIS_OF_DAY + state.minute * MINUTE_MILLIS_OF_DAY)
                },
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RepeatEditorDialog(
    recurrence: String?,
    repeatFrom: Int,
    onDismiss: () -> Unit,
    onConfirm: (String?, Int) -> Unit,
) {
    val initial = remember(recurrence) { parseRepeatDraft(recurrence) }
    var frequency by remember(recurrence) { mutableStateOf(if (initial.frequency == RepeatFrequency.NONE) RepeatFrequency.WEEKLY else initial.frequency) }
    var intervalText by remember(recurrence) { mutableStateOf(initial.interval.toString()) }
    var weekDays by remember(recurrence) { mutableStateOf(initial.weekDays) }
    var monthlyMode by remember(recurrence) { mutableStateOf(initial.monthlyMode) }
    var monthDayText by remember(recurrence) {
        mutableStateOf((initial.monthDay ?: LocalDate.now().dayOfMonth).toString())
    }
    var monthOrdinalText by remember(recurrence) { mutableStateOf(initial.monthOrdinal.toString()) }
    var monthWeekday by remember(recurrence) { mutableStateOf(initial.monthWeekday) }
    var selectedRepeatFrom by remember(recurrence, repeatFrom) { mutableIntStateOf(repeatFrom) }
    var endMode by remember(recurrence) { mutableStateOf(initial.endMode) }
    var countText by remember(recurrence) { mutableStateOf(initial.count?.toString().orEmpty()) }
    var untilText by remember(recurrence) { mutableStateOf(initial.untilText.orEmpty()) }
    val validationError = repeatValidationError(
        frequency = frequency,
        intervalText = intervalText,
        monthlyMode = monthlyMode,
        monthDayText = monthDayText,
        monthOrdinalText = monthOrdinalText,
        endMode = endMode,
        countText = countText,
        untilText = untilText,
    )

    fun saveRepeat() {
        val newRecurrence = buildRRule(
            frequency = frequency,
            interval = intervalText.toIntOrNull()?.coerceAtLeast(1) ?: 1,
            weekDays = weekDays,
            monthlyMode = monthlyMode,
            monthDay = monthDayText.toIntOrNull()?.coerceIn(1, 31),
            monthOrdinal = monthOrdinalText.toIntOrNull()?.coerceMonthlyOrdinal() ?: 1,
            monthWeekday = monthWeekday,
            endMode = endMode,
            count = countText.toIntOrNull()?.coerceAtLeast(1),
            untilText = untilText,
        )
        onConfirm(
            newRecurrence,
            if (newRecurrence == null) Task.RepeatFrom.DUE_DATE else selectedRepeatFrom,
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        Surface(color = SaltTheme.colors.background) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = SaltTheme.colors.background,
                            titleContentColor = SaltTheme.colors.text,
                            navigationIconContentColor = SaltTheme.colors.text,
                            actionIconContentColor = SaltTheme.colors.highlight,
                        ),
                        title = {
                            Text(text = "自定义重复周期")
                        },
                        navigationIcon = {
                            IconButton(
                                enabled = validationError == null,
                                onClick = ::saveRepeat,
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = "保存重复规则",
                                )
                            }
                        },
                        actions = {
                            TextButton(onClick = onDismiss) {
                                Text("取消", style = MaterialTheme.typography.bodyLarge)
                            }
                        },
                    )
                },
            ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 28.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                RepeatSectionHeader("重复频率")
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = intervalText,
                        onValueChange = { intervalText = it.filter(Char::isDigit).take(3) },
                        modifier = Modifier.width(72.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            textAlign = TextAlign.Center
                        ),
                        isError = validationError != null && intervalText.toIntOrNull() == null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    RepeatFrequencySpinner(
                        frequency = frequency,
                        onSelected = { frequency = it },
                    )
                }

                if (frequency != RepeatFrequency.NONE) {
                    if (frequency == RepeatFrequency.WEEKLY) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))
                        RepeatSectionHeader("重复于")
                        Spacer(modifier = Modifier.height(16.dp))
                        WeekdaySelector(
                            selected = weekDays,
                            onChange = { weekDays = it },
                        )
                    }
                    if (frequency == RepeatFrequency.MONTHLY) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))
                        RepeatSectionHeader("重复于")
                        Spacer(modifier = Modifier.height(12.dp))
                        RepeatOptionRow("按每月日期", monthlyMode == RepeatMonthlyMode.DAY_OF_MONTH) {
                            monthlyMode = RepeatMonthlyMode.DAY_OF_MONTH
                        }
                        if (monthlyMode == RepeatMonthlyMode.DAY_OF_MONTH) {
                            OutlinedTextField(
                                value = monthDayText,
                                onValueChange = { monthDayText = it.filter(Char::isDigit).take(2) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                label = { Text("每月第几天") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            )
                        }
                        RepeatOptionRow("按星期位置", monthlyMode == RepeatMonthlyMode.DAY_OF_WEEK) {
                            monthlyMode = RepeatMonthlyMode.DAY_OF_WEEK
                        }
                        if (monthlyMode == RepeatMonthlyMode.DAY_OF_WEEK) {
                            OutlinedTextField(
                                value = monthOrdinalText,
                                onValueChange = { value ->
                                    monthOrdinalText = value
                                        .filter { it.isDigit() || it == '-' }
                                        .take(2)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                label = { Text("第几个星期，-1 为最后") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            WeekdaySelector(
                                selected = setOf(monthWeekday),
                                onChange = { selected ->
                                    selected.firstOrNull()?.let { monthWeekday = it }
                                },
                            )
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))
                    RepeatSectionHeader("结束")
                    Spacer(modifier = Modifier.height(12.dp))
                    RepeatEndRow(
                        selected = endMode == RepeatEndMode.NEVER,
                        onClick = { endMode = RepeatEndMode.NEVER },
                    ) {
                        Text("永不", fontSize = 16.sp)
                    }
                    var showUntilDatePicker by remember { mutableStateOf(false) }
                    if (showUntilDatePicker) {
                        val untilPickerState = rememberDatePickerState()
                        AlertDialog(
                            onDismissRequest = { showUntilDatePicker = false },
                            text = {
                                DatePicker(
                                    state = untilPickerState,
                                    title = {},
                                    showModeToggle = false,
                                )
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    untilPickerState.selectedDateMillis?.let { ms ->
                                        val d = java.time.Instant.ofEpochMilli(ms)
                                            .atZone(java.time.ZoneOffset.UTC).toLocalDate()
                                        untilText = "%04d-%02d-%02d".format(d.year, d.monthValue, d.dayOfMonth)
                                    }
                                    showUntilDatePicker = false
                                }) { Text("确定") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showUntilDatePicker = false }) { Text("取消") }
                            },
                        )
                    }
                    RepeatEndRow(
                        selected = endMode == RepeatEndMode.UNTIL,
                        onClick = { endMode = RepeatEndMode.UNTIL },
                    ) {
                        Text("于", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .border(1.dp, SaltTheme.colors.text.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 16.dp)
                                .clickable {
                                    endMode = RepeatEndMode.UNTIL
                                    showUntilDatePicker = true
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = untilText.ifEmpty { "选择日期" },
                                fontSize = 16.sp,
                                color = if (untilText.isEmpty()) SaltTheme.colors.text.copy(alpha = 0.4f)
                                        else SaltTheme.colors.text,
                            )
                        }
                    }
                    RepeatEndRow(
                        selected = endMode == RepeatEndMode.COUNT,
                        onClick = { endMode = RepeatEndMode.COUNT },
                    ) {
                        Text("前有", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = countText,
                            onValueChange = {
                                endMode = RepeatEndMode.COUNT
                                countText = it.filter(Char::isDigit).take(4)
                            },
                            modifier = Modifier.width(80.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                textAlign = TextAlign.Center
                            ),
                            isError = validationError != null && endMode == RepeatEndMode.COUNT,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("次发生", fontSize = 16.sp)
                    }
                    validationError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                        )
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun RepeatSectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = SaltTheme.colors.text,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
}

@Composable
private fun RepeatEndRow(
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, content = content)
    }
}

@Composable
private fun RepeatOptionRow(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, modifier = Modifier.weight(1f), fontSize = 16.sp)
        if (selected) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = SaltTheme.colors.highlight,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun RepeatFrequencySpinner(
    frequency: RepeatFrequency,
    onSelected: (RepeatFrequency) -> Unit,
) {
    val labels = listOf("天", "周", "月", "年")
    val values = listOf(RepeatFrequency.DAILY, RepeatFrequency.WEEKLY, RepeatFrequency.MONTHLY, RepeatFrequency.YEARLY)
    val currentLabel = when (frequency) {
        RepeatFrequency.DAILY -> "天"
        RepeatFrequency.MONTHLY -> "月"
        RepeatFrequency.YEARLY -> "年"
        else -> "周"
    }
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .height(45.dp)
            .border(1.dp, SaltTheme.colors.text.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp)
            .clickable { expanded = true },
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = currentLabel, color = SaltTheme.colors.text)
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = SaltTheme.colors.text,
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            labels.forEachIndexed { i, label ->
                DropdownMenuItem(
                    onClick = { expanded = false; onSelected(values[i]) },
                    text = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SaltTheme.colors.text,
                        )
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WeekdaySelector(
    selected: Set<DayOfWeek>,
    onChange: (Set<DayOfWeek>) -> Unit,
) {
    val days = listOf(
        DayOfWeek.MONDAY to "一",
        DayOfWeek.TUESDAY to "二",
        DayOfWeek.WEDNESDAY to "三",
        DayOfWeek.THURSDAY to "四",
        DayOfWeek.FRIDAY to "五",
        DayOfWeek.SATURDAY to "六",
        DayOfWeek.SUNDAY to "日",
    )
    
    // 使用固定间距，确保7个按钮在一行显示
    val buttonSize = 40.dp
    val spacing = 8.dp
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        days.forEach { (day, label) ->
            val checked = day in selected
            val scale by animateFloatAsState(
                targetValue = if (checked) 1.1f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "day_scale"
            )
            val backgroundColor by animateColorAsState(
                targetValue = if (checked) SaltTheme.colors.highlight 
                    else SaltTheme.colors.background,
                animationSpec = tween(200),
                label = "day_background"
            )
            val contentColor by animateColorAsState(
                targetValue = if (checked) SaltTheme.colors.background 
                    else SaltTheme.colors.text,
                animationSpec = tween(200),
                label = "day_content"
            )
            val borderColor by animateColorAsState(
                targetValue = if (checked) SaltTheme.colors.highlight 
                    else SaltTheme.colors.text.copy(alpha = 0.3f),
                animationSpec = tween(200),
                label = "day_border"
            )
            
            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .scale(scale)
                    .background(backgroundColor, CircleShape)
                    .border(
                        width = if (checked) 0.dp else 1.5.dp,
                        color = borderColor,
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, radius = 22.dp),
                    ) { onChange(if (checked) selected - day else selected + day) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (checked) FontWeight.Bold else FontWeight.Normal,
                    color = contentColor,
                )
            }
        }
    }
}

@Composable
private fun DialogChoice(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        fontSize = 17.sp,
    )
}

@Composable
private fun DateTimeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        color = if (selected) SaltTheme.colors.highlight else SaltTheme.colors.text,
        fontSize = 14.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (selected) {
                    SaltTheme.colors.highlight.copy(alpha = 0.14f)
                } else {
                    SaltTheme.colors.subBackground
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RemindersDialog(
    reminders: List<TaskReminderDraft>,
    startDate: Long,
    dueDate: Long,
    settings: TaskSettings,
    onDismiss: () -> Unit,
    onConfirm: (List<TaskReminderDraft>) -> Unit,
) {
    var drafts by remember(reminders) { mutableStateOf(reminders.dedupeReminders()) }
    var showCustomTime by remember { mutableStateOf(false) }
    val initialCustomTime = remember { LocalDate.now().plusDays(1).atTime(9, 0) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialCustomTime.toLocalDate().toDatePickerMillis()
    )
    var hourText by remember { mutableStateOf(initialCustomTime.hour.toString().padStart(2, '0')) }
    var minuteText by remember { mutableStateOf(initialCustomTime.minute.toString().padStart(2, '0')) }
    val hour = hourText.toIntOrNull()
    val minute = minuteText.toIntOrNull()
    val timeError = hour == null || hour !in 0..23 || minute == null || minute !in 0..59
    var customReminder by remember { mutableStateOf<TaskReminderDraft?>(null) }
    var randomReminder by remember { mutableStateOf<TaskReminderDraft?>(null) }
    var absoluteReminder by remember { mutableStateOf<TaskReminderDraft?>(null) }
    var editingReminder by remember { mutableStateOf<TaskReminderDraft?>(null) }

    fun addReminder(reminder: TaskReminderDraft) {
        editingReminder = null
        drafts = (drafts + reminder).dedupeReminders()
    }

    fun upsertReminder(reminder: TaskReminderDraft) {
        val original = editingReminder
        drafts = if (original == null) {
            (drafts + reminder).dedupeReminders()
        } else {
            drafts.map { if (it == original) reminder else it }.dedupeReminders()
        }
        editingReminder = null
    }

    fun editReminder(reminder: TaskReminderDraft) {
        editingReminder = reminder
        when (reminder.type) {
            Alarm.TYPE_DATE_TIME -> absoluteReminder = reminder
            Alarm.TYPE_RANDOM -> randomReminder = reminder
            Alarm.TYPE_REL_START,
            Alarm.TYPE_REL_END -> customReminder = reminder
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("提醒") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 560.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (drafts.isEmpty()) {
                    Text(
                        text = "没有提醒",
                        color = SaltTheme.colors.subText,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                } else {
                    Text(
                        text = "已添加提醒",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    drafts.forEach { reminder ->
                        ReminderDraftRow(
                            label = reminderLabel(reminder, startDate, dueDate, settings),
                            onEdit = { editReminder(reminder) },
                            onRemove = { drafts = drafts.filterNot { it == reminder } },
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }

                if (startDate > 0) {
                    DialogChoice("开始时") {
                        addReminder(TaskReminderDraft(time = 0L, type = Alarm.TYPE_REL_START))
                    }
                    DialogChoice("开始前 10 分钟") {
                        addReminder(TaskReminderDraft(time = -TEN_MINUTES_MILLIS, type = Alarm.TYPE_REL_START))
                    }
                }
                if (dueDate > 0) {
                    DialogChoice("到期时") {
                        addReminder(TaskReminderDraft(time = 0L, type = Alarm.TYPE_REL_END))
                    }
                    DialogChoice("提前 10 分钟") {
                        addReminder(TaskReminderDraft(time = -TEN_MINUTES_MILLIS, type = Alarm.TYPE_REL_END))
                    }
                    DialogChoice("逾期后每天提醒") {
                        addReminder(
                            TaskReminderDraft(
                                time = ONE_DAY_MILLIS,
                                type = Alarm.TYPE_REL_END,
                                repeat = 6,
                                interval = ONE_DAY_MILLIS,
                            )
                        )
                    }
                }
                DialogChoice("明天 9:00") {
                    addReminder(
                        TaskReminderDraft(
                            time = LocalDate.now().plusDays(1).atTime(9, 0).toMillis(),
                            type = Alarm.TYPE_DATE_TIME,
                        )
                    )
                }
                DialogChoice("自定义提醒") {
                    editingReminder = null
                    customReminder = TaskReminderDraft(
                        time = -TEN_MINUTES_MILLIS,
                        type = if (dueDate > 0 || startDate <= 0) Alarm.TYPE_REL_END else Alarm.TYPE_REL_START,
                    )
                }
                DialogChoice(if (showCustomTime) "收起具体时间" else "选择具体时间") {
                    showCustomTime = !showCustomTime
                }
                if (showCustomTime) {
                    DatePicker(state = datePickerState)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = hourText,
                            onValueChange = { hourText = it.filter(Char::isDigit).take(2) },
                            modifier = Modifier.width(88.dp),
                            singleLine = true,
                            isError = hour == null || hour !in 0..23,
                            label = { Text(if (settings.use24HourTime) "小时" else "Hour") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                        OutlinedTextField(
                            value = minuteText,
                            onValueChange = { minuteText = it.filter(Char::isDigit).take(2) },
                            modifier = Modifier.width(88.dp),
                            singleLine = true,
                            isError = minute == null || minute !in 0..59,
                            label = { Text("分钟") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                    }
                    TextButton(
                        enabled = !timeError,
                        onClick = {
                            val date = datePickerState.selectedDateMillis
                                ?.toDatePickerLocalDate()
                                ?: initialCustomTime.toLocalDate()
                            addReminder(
                                TaskReminderDraft(
                                    time = date.atTime(hour ?: 9, minute ?: 0).toMillis(),
                                    type = Alarm.TYPE_DATE_TIME,
                                )
                            )
                            showCustomTime = false
                        },
                    ) {
                        Text("添加具体时间")
                    }
                }
                DialogChoice("随机提醒") {
                    editingReminder = null
                    randomReminder = TaskReminderDraft(time = ONE_DAY_MILLIS, type = Alarm.TYPE_RANDOM)
                }
                if (drafts.isNotEmpty()) {
                    DialogChoice("清空提醒") { drafts = emptyList() }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(drafts.dedupeReminders()) }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )

    customReminder?.let { reminder ->
        CustomRelativeReminderDialog(
            reminder = reminder,
            onDismiss = {
                customReminder = null
                editingReminder = null
            },
            onConfirm = {
                upsertReminder(it)
                customReminder = null
            },
        )
    }

    randomReminder?.let { reminder ->
        RandomReminderDialog(
            reminder = reminder,
            onDismiss = {
                randomReminder = null
                editingReminder = null
            },
            onConfirm = {
                upsertReminder(it)
                randomReminder = null
            },
        )
    }

    absoluteReminder?.let { reminder ->
        AbsoluteReminderDialog(
            reminder = reminder,
            settings = settings,
            onDismiss = {
                absoluteReminder = null
                editingReminder = null
            },
            onConfirm = {
                upsertReminder(it)
                absoluteReminder = null
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AbsoluteReminderDialog(
    reminder: TaskReminderDraft,
    settings: TaskSettings,
    onDismiss: () -> Unit,
    onConfirm: (TaskReminderDraft) -> Unit,
) {
    val initial = remember(reminder) {
        reminder.time.takeIf { it > 0L }
            ?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime() }
            ?: LocalDate.now().plusDays(1).atTime(9, 0)
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initial.toLocalDate().toDatePickerMillis()
    )
    var hourText by remember(reminder) { mutableStateOf(initial.hour.toString().padStart(2, '0')) }
    var minuteText by remember(reminder) { mutableStateOf(initial.minute.toString().padStart(2, '0')) }
    val hour = hourText.toIntOrNull()
    val minute = minuteText.toIntOrNull()
    val timeError = hour == null || hour !in 0..23 || minute == null || minute !in 0..59

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("具体时间") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 560.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                DatePicker(state = datePickerState)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = hourText,
                        onValueChange = { hourText = it.filter(Char::isDigit).take(2) },
                        modifier = Modifier.width(88.dp),
                        singleLine = true,
                        isError = hour == null || hour !in 0..23,
                        label = { Text(if (settings.use24HourTime) "小时" else "Hour") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    OutlinedTextField(
                        value = minuteText,
                        onValueChange = { minuteText = it.filter(Char::isDigit).take(2) },
                        modifier = Modifier.width(88.dp),
                        singleLine = true,
                        isError = minute == null || minute !in 0..59,
                        label = { Text("分钟") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
                if (timeError) {
                    Text(
                        text = "请输入有效时间",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !timeError,
                onClick = {
                    val date = datePickerState.selectedDateMillis
                        ?.toDatePickerLocalDate()
                        ?: initial.toLocalDate()
                    onConfirm(
                        TaskReminderDraft(
                            time = date.atTime(hour ?: initial.hour, minute ?: initial.minute).toMillis(),
                            type = Alarm.TYPE_DATE_TIME,
                        )
                    )
                },
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )
}

@Composable
private fun CustomRelativeReminderDialog(
    reminder: TaskReminderDraft,
    onDismiss: () -> Unit,
    onConfirm: (TaskReminderDraft) -> Unit,
) {
    val initialUnit = remember(reminder) { bestReminderUnit(reminder.time) }
    var amountText by remember(reminder) {
        mutableStateOf((abs(reminder.time) / initialUnit.millis).toString())
    }
    var selectedUnit by remember(reminder) { mutableStateOf(initialUnit) }
    var relation by remember(reminder) {
        mutableStateOf(if (reminder.time <= 0L) ReminderRelation.BEFORE else ReminderRelation.AFTER)
    }
    var anchor by remember(reminder) {
        mutableStateOf(if (reminder.type == Alarm.TYPE_REL_START) ReminderAnchor.START else ReminderAnchor.DUE)
    }
    var repeatEnabled by remember(reminder) { mutableStateOf(reminder.repeat > 0 && reminder.interval > 0L) }
    val initialIntervalUnit = remember(reminder) { bestReminderUnit(reminder.interval) }
    var intervalText by remember(reminder) {
        mutableStateOf(
            if (reminder.interval > 0L) {
                (reminder.interval / initialIntervalUnit.millis).toString()
            } else {
                "15"
            }
        )
    }
    var intervalUnit by remember(reminder) {
        mutableStateOf(if (reminder.interval > 0L) initialIntervalUnit else ReminderUnit.MINUTES)
    }
    var repeatText by remember(reminder) { mutableStateOf((reminder.repeat.takeIf { it > 0 } ?: 4).toString()) }

    val amount = amountText.toIntOrNull()
    val intervalAmount = intervalText.toIntOrNull()
    val repeatCount = repeatText.toIntOrNull()
    val validationError = when {
        amount == null || amount < 0 -> "提醒时间不能小于 0"
        repeatEnabled && (intervalAmount == null || intervalAmount <= 0) -> "重复间隔必须大于 0"
        repeatEnabled && (repeatCount == null || repeatCount <= 0) -> "重复次数必须大于 0"
        else -> null
    }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(180)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("自定义提醒") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 560.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter(Char::isDigit).take(4) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    isError = amount == null || amount < 0,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                // Unit selection with inline before/due buttons
                ReminderUnit.values().forEach { unit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selectedUnit == unit,
                            onClick = { selectedUnit = unit }
                        )
                        Text(
                            text = unit.label(amount ?: 0),
                            modifier = Modifier.weight(1f)
                        )
                        // Show before/due buttons only for selected unit
                        if (selectedUnit == unit) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TextButton(
                                    onClick = { relation = ReminderRelation.BEFORE },
                                    modifier = Modifier
                                        .border(
                                            1.dp,
                                            if (relation == ReminderRelation.BEFORE) SaltTheme.colors.highlight else SaltTheme.colors.text.copy(alpha = 0.3f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 4.dp),
                                ) {
                                    Text("前")
                                }
                                TextButton(
                                    onClick = { relation = ReminderRelation.AFTER },
                                    modifier = Modifier
                                        .border(
                                            1.dp,
                                            if (relation == ReminderRelation.AFTER) SaltTheme.colors.highlight else SaltTheme.colors.text.copy(alpha = 0.3f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 4.dp),
                                ) {
                                    Text("到期")
                                }
                            }
                        }
                    }
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                
                // Anchor selection (start/due date)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = anchor == ReminderAnchor.START,
                        onClick = { anchor = ReminderAnchor.START }
                    )
                    Text("开始日期", modifier = Modifier.weight(1f))
                    RadioButton(
                        selected = anchor == ReminderAnchor.DUE,
                        onClick = { anchor = ReminderAnchor.DUE }
                    )
                    Text("截止日期")
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                
                // Repeat option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = !repeatEnabled,
                        onClick = { repeatEnabled = false }
                    )
                    Text("不重复", modifier = Modifier.weight(1f))
                }
                
                if (repeatEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = intervalText,
                        onValueChange = { intervalText = it.filter(Char::isDigit).take(4) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = intervalAmount == null || intervalAmount <= 0,
                        label = { Text("每隔") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    ReminderUnit.values().forEach { unit ->
                        RepeatOptionRow(unit.label(intervalAmount ?: 0), intervalUnit == unit) {
                            intervalUnit = unit
                        }
                    }
                    OutlinedTextField(
                        value = repeatText,
                        onValueChange = { repeatText = it.filter(Char::isDigit).take(3) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = repeatCount == null || repeatCount <= 0,
                        label = { Text("重复次数") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
                
                if (validationError != null) {
                    Text(
                        text = validationError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = validationError == null,
                onClick = {
                    val signedOffset = (amount ?: 0) * selectedUnit.millis *
                            if (relation == ReminderRelation.BEFORE) -1 else 1
                    onConfirm(
                        TaskReminderDraft(
                            time = signedOffset,
                            type = anchor.alarmType,
                            repeat = if (repeatEnabled) repeatCount ?: 0 else 0,
                            interval = if (repeatEnabled) (intervalAmount ?: 0) * intervalUnit.millis else 0L,
                        )
                    )
                },
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )
}

@Composable
private fun RandomReminderDialog(
    reminder: TaskReminderDraft,
    onDismiss: () -> Unit,
    onConfirm: (TaskReminderDraft) -> Unit,
) {
    val initialUnit = remember(reminder) { bestReminderUnit(reminder.time) }
    var amountText by remember(reminder) {
        mutableStateOf(((reminder.time.takeIf { it > 0L } ?: ONE_DAY_MILLIS) / initialUnit.millis).toString())
    }
    var selectedUnit by remember(reminder) { mutableStateOf(initialUnit) }
    val amount = amountText.toIntOrNull()
    val validationError = if (amount == null || amount <= 0) "随机提醒间隔必须大于 0" else null
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(180)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("随机提醒") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "随机每",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter(Char::isDigit).take(4) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    isError = validationError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Spacer(modifier = Modifier.height(8.dp))
                ReminderUnit.values().forEach { unit ->
                    RepeatOptionRow(unit.label(amount ?: 0), selectedUnit == unit) {
                        selectedUnit = unit
                    }
                }
                if (validationError != null) {
                    Text(
                        text = validationError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = validationError == null,
                onClick = {
                    onConfirm(
                        TaskReminderDraft(
                            time = (amount ?: 1) * selectedUnit.millis,
                            type = Alarm.TYPE_RANDOM,
                        )
                    )
                },
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )
}

@Composable
private fun ReminderDraftRow(
    label: String,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(onClick = onEdit),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "删除提醒",
            )
        }
    }
}

// ── 对齐原版 AlarmRow：提醒条目直接内嵌在编辑器里 ──
@Composable
private fun InlineReminderSection(
    reminders: List<TaskReminderDraft>,
    startDate: Long,
    dueDate: Long,
    settings: TaskSettings,
    isNew: Boolean,
    onRemindersChange: (List<TaskReminderDraft>) -> Unit,
) {
    var showAddPicker     by remember { mutableStateOf(false) }
    var editingReminder   by remember { mutableStateOf<TaskReminderDraft?>(null) }
    var customDialog      by remember { mutableStateOf<TaskReminderDraft?>(null) }
    var randomDialog      by remember { mutableStateOf<TaskReminderDraft?>(null) }
    var absoluteDialog    by remember { mutableStateOf<TaskReminderDraft?>(null) }
    var showDateTimePicker by remember { mutableStateOf(false) }

    fun upsert(new: TaskReminderDraft) {
        val orig = editingReminder
        onRemindersChange(
            if (orig == null) (reminders + new).dedupeReminders()
            else reminders.map { if (it == orig) new else it }.dedupeReminders()
        )
        editingReminder = null
    }

    fun edit(r: TaskReminderDraft) {
        editingReminder = r
        when (r.type) {
            Alarm.TYPE_DATE_TIME -> absoluteDialog = r
            Alarm.TYPE_RANDOM    -> randomDialog   = r
            else                 -> customDialog    = r
        }
    }

    if (showAddPicker) {
        AlertDialog(
            onDismissRequest = { showAddPicker = false },
            title = { 
                Text(
                    text = "添加提醒",
                    style = MaterialTheme.typography.titleLarge,
                    color = SaltTheme.colors.text,
                ) 
            },
            containerColor = SaltTheme.colors.background,
            text = {
                Column {
                    // 开始时提醒
                    TextButton(
                        onClick = {
                            onRemindersChange(
                                (reminders + TaskReminderDraft(time = 0L, type = Alarm.TYPE_REL_START))
                                    .dedupeReminders()
                            )
                            showAddPicker = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "开始时提醒",
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = SaltTheme.colors.text,
                        )
                    }
                    // 到期时提醒
                    TextButton(
                        onClick = {
                            onRemindersChange(
                                (reminders + TaskReminderDraft(time = 0L, type = Alarm.TYPE_REL_END))
                                    .dedupeReminders()
                            )
                            showAddPicker = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "到期时提醒",
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = SaltTheme.colors.text,
                        )
                    }
                    // 逾期后每天提醒
                    TextButton(
                        onClick = {
                            onRemindersChange(
                                (reminders + TaskReminderDraft(
                                    time = ONE_DAY_MILLIS,
                                    type = Alarm.TYPE_REL_END,
                                    repeat = 6,
                                    interval = ONE_DAY_MILLIS
                                )).dedupeReminders()
                            )
                            showAddPicker = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "逾期后每天提醒",
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = SaltTheme.colors.text,
                        )
                    }
                    // 选择日期和时间
                    TextButton(
                        onClick = {
                            showDateTimePicker = true
                            showAddPicker = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "选择日期和时间",
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = SaltTheme.colors.text,
                        )
                    }
                    // 自定义
                    TextButton(
                        onClick = {
                            customDialog = TaskReminderDraft(time = -15 * ONE_MINUTE_MILLIS, type = Alarm.TYPE_REL_END)
                            showAddPicker = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "自定义",
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = SaltTheme.colors.text,
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAddPicker = false }) {
                    Text(
                        text = "取消",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SaltTheme.colors.highlight,
                    )
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(start = 16.dp, end = 16.dp)
                .clickable { showAddPicker = true },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = null,
                tint = SaltTheme.colors.subText,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(32.dp))
            Text(
                text  = "添加提醒",
                color = SaltTheme.colors.text.copy(alpha = 0.48f),
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )
        }
        Column(modifier = Modifier.padding(start = 76.dp, end = 4.dp)) {
            reminders.forEach { r ->
                ReminderDraftRow(
                    label  = reminderLabel(r, startDate, dueDate, settings),
                    onEdit = { edit(r) },
                    onRemove = {
                        onRemindersChange(reminders.filterNot { it == r })
                    },
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 76.dp),
            color = SaltTheme.colors.stroke.copy(alpha = 0.16f),
        )
    }

    // 显示自定义提醒对话框
    customDialog?.let { reminder ->
        CustomRelativeReminderDialog(
            reminder = reminder,
            onDismiss = {
                customDialog = null
                editingReminder = null
            },
            onConfirm = {
                upsert(it)
                customDialog = null
            },
        )
    }

    // 显示随机提醒对话框
    randomDialog?.let { reminder ->
        RandomReminderDialog(
            reminder = reminder,
            onDismiss = {
                randomDialog = null
                editingReminder = null
            },
            onConfirm = {
                upsert(it)
                randomDialog = null
            },
        )
    }

    // 显示绝对时间提醒对话框（选择日期和时间）
    absoluteDialog?.let { reminder ->
        AbsoluteReminderDialog(
            reminder = reminder,
            settings = settings,
            onDismiss = {
                absoluteDialog = null
                editingReminder = null
            },
            onConfirm = {
                upsert(it)
                absoluteDialog = null
            },
        )
    }

    // 显示日期时间选择器（从"选择日期和时间"选项进入）
    if (showDateTimePicker) {
        AbsoluteReminderDialog(
            reminder = TaskReminderDraft(time = 0L, type = Alarm.TYPE_DATE_TIME),
            settings = settings,
            onDismiss = { showDateTimePicker = false },
            onConfirm = {
                upsert(it)
                showDateTimePicker = false
            },
        )
    }
}

@Composable
private fun InlineSubtasksSection(
    subtasks: List<TaskSubtaskDraft>,
    onSubtasksChange: (List<TaskSubtaskDraft>) -> Unit,
) {
    var focusIndex by remember { mutableIntStateOf(-1) }

    fun addSubtask() {
        val next = subtasks + TaskSubtaskDraft(title = "")
        focusIndex = next.lastIndex
        onSubtasksChange(next)
    }

    fun updateSubtask(index: Int, draft: TaskSubtaskDraft) {
        if (index !in subtasks.indices) return
        onSubtasksChange(
            subtasks.toMutableList().also {
                it[index] = draft
            }
        )
    }

    fun removeSubtask(index: Int) {
        if (index !in subtasks.indices) return
        val next = subtasks.toMutableList().also { it.removeAt(index) }
        focusIndex = focusIndex.coerceAtMost(next.lastIndex)
        onSubtasksChange(next)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.SubdirectoryArrowRight,
                contentDescription = null,
                tint = SaltTheme.colors.subText,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(32.dp))
            Text(
                text = "添加子任务",
                color = SaltTheme.colors.text.copy(alpha = 0.38f),
                fontSize = 16.sp,
                modifier = Modifier
                    .weight(1f)
                    .clickable { addSubtask() },
            )
        }
        Column(modifier = Modifier.padding(start = 76.dp, end = 4.dp)) {
            subtasks.forEachIndexed { index, draft ->
                InlineSubtaskDraftRow(
                    draft = draft,
                    requestFocus = focusIndex == index,
                    onTitleChange = { updateSubtask(index, draft.copy(title = it)) },
                    onToggle = { updateSubtask(index, draft.copy(completed = !draft.completed)) },
                    onDelete = { removeSubtask(index) },
                    onDone = {
                        if (draft.title.isNotBlank()) {
                            addSubtask()
                        }
                    },
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 76.dp),
            color = SaltTheme.colors.stroke.copy(alpha = 0.16f),
        )
    }
}

@Composable
private fun InlineSubtaskDraftRow(
    draft: TaskSubtaskDraft,
    requestFocus: Boolean,
    onTitleChange: (String) -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onDone: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.Top,
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
            Checkbox(
                checked = draft.completed,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF5AA7DE),
                    uncheckedColor = Color(0xFF5AA7DE),
                    checkmarkColor = Color.White,
                ),
                modifier = Modifier
                    .padding(top = 7.dp, end = 12.dp)
                    .size(32.dp),
            )
        }
        BasicTextField(
            value = draft.title,
            onValueChange = onTitleChange,
            cursorBrush = SolidColor(SaltTheme.colors.text),
            singleLine = false,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = SaltTheme.colors.text.copy(alpha = if (draft.completed) 0.45f else 1f),
                textDecoration = if (draft.completed) TextDecoration.LineThrough else TextDecoration.None,
                textDirection = TextDirection.Content,
                fontSize = 16.sp,
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .padding(top = 13.dp, bottom = 9.dp),
        )
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "删除子任务",
                tint = Color(0xFF6E6E6E),
                modifier = Modifier.size(28.dp),
            )
        }
    }

    LaunchedEffect(requestFocus) {
        if (requestFocus) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
private fun AttachmentsDialog(
    attachments: List<TaskAttachmentDraft>,
    onAdd: () -> Unit,
    onOpen: (TaskAttachmentDraft) -> Unit,
    onRemove: (TaskAttachmentDraft) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var pendingDelete by remember { mutableStateOf<TaskAttachmentDraft?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("附件") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (attachments.isEmpty()) {
                    Text(
                        text = "还没有附件",
                        color = SaltTheme.colors.subText,
                    )
                } else {
                    attachments.forEach { attachment ->
                        AttachmentItemRow(
                            attachment = attachment,
                            permissionValid = context.hasPersistedReadPermission(attachment.uri),
                            onOpen = { onOpen(attachment) },
                            onShare = { context.shareAttachment(attachment) },
                            onDelete = { pendingDelete = attachment },
                        )
                    }
                }
                TextButton(onClick = onAdd) {
                    Text("添加附件")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("完成")
            }
        },
    )

    pendingDelete?.let { attachment ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("删除附件") },
            text = { Text("删除“${attachment.name}”？") },
            confirmButton = {
                TextButton(onClick = {
                    pendingDelete = null
                    onRemove(attachment)
                }) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("取消")
                }
            },
        )
    }
}

@Composable
private fun AttachmentItemRow(
    attachment: TaskAttachmentDraft,
    permissionValid: Boolean,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onOpen)
                .padding(vertical = 8.dp),
        ) {
            Text(
                text = attachment.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = if (permissionValid) "点击打开" else "权限可能已失效",
                color = if (permissionValid) {
                    SaltTheme.colors.subText
                } else {
                    MaterialTheme.colorScheme.error
                },
                fontSize = 12.sp,
            )
        }
        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "附件操作",
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text("打开") },
                    onClick = {
                        menuExpanded = false
                        onOpen()
                    },
                )
                DropdownMenuItem(
                    text = { Text("分享") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onShare()
                    },
                )
                DropdownMenuItem(
                    text = { Text("删除") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onDelete()
                    },
                )
            }
        }
    }
}

@Composable
private fun TextValueDialog(
    title: String,
    value: String,
    singleLine: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var text by remember(value) { mutableStateOf(value) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(180)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = singleLine,
                minLines = if (singleLine) 1 else 4,
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )
}

private enum class EditorPicker {
    START,
    DUE,
    REPEAT,
    REMINDER,
}

private enum class EditorSheetTarget {
    START_DATE,
    DUE_DATE,
    PRIORITY,
    CALENDAR,
    NOTES,
    TAGS,
    REPEAT,
    REMINDER,
    ATTACHMENT,
}

private enum class TextEditorTarget {
    TAGS,
    NOTES,
}

private enum class ReminderUnit(val millis: Long, val label: String) {
    MINUTES(ONE_MINUTE_MILLIS, "分钟"),
    HOURS(ONE_HOUR_MILLIS, "小时"),
    DAYS(ONE_DAY_MILLIS, "天"),
    WEEKS(ONE_WEEK_MILLIS, "周"),
}

private enum class ReminderRelation {
    BEFORE,
    AFTER,
}

private enum class ReminderAnchor(val alarmType: Int) {
    START(Alarm.TYPE_REL_START),
    DUE(Alarm.TYPE_REL_END),
}

private data class TaskDisplayGroup(
    val title: String,
    val tasks: List<Task>,
)

@Composable
private fun TaskList(
    tasks: List<Task>,
    extras: Map<Long, TaskListExtras>,
    settings: TaskSettings,
    groupMode: TaskSortMode,
    groupAscending: Boolean,
    subtaskMode: TaskSortMode,
    subtaskAscending: Boolean,
    completedAtBottom: Boolean,
    completedMode: TaskSortMode,
    completedAscending: Boolean,
    selectedTaskIds: Set<Long>,
    onComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onEdit: (Task) -> Unit,
    onToggleSelection: (Task) -> Unit,
    onEnterSelection: (Task) -> Unit,
) {
    val displayTasks = remember(tasks, completedAtBottom, completedMode, completedAscending) {
        tasks.placeCompleted(completedAtBottom, completedMode, completedAscending)
    }
    val groups = remember(displayTasks, extras, groupMode, groupAscending) {
        displayTasks.groupForDisplay(groupMode, groupAscending, extras)
    }
    val selectionMode = selectedTaskIds.isNotEmpty()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        groups.forEach { group ->
            if (group.title.isNotBlank()) {
                item(key = "task_group_${group.title}") {
                    TaskGroupHeader(group.title)
                }
            }
            items(group.tasks, key = { it.id }) { task ->
                SwipeableTaskItem(
                    task = task,
                    extras = extras[task.id] ?: TaskListExtras(),
                    settings = settings,
                    subtaskMode = subtaskMode,
                    subtaskAscending = subtaskAscending,
                    selected = task.id in selectedTaskIds,
                    selectionMode = selectionMode,
                    onComplete = { onComplete(task) },
                    onDelete = { onDelete(task) },
                    onEdit = { onEdit(task) },
                    onToggleSelection = { onToggleSelection(task) },
                    onEnterSelection = { onEnterSelection(task) },
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun TaskGroupHeader(title: String) {
    Text(
        text = title,
        color = SaltTheme.colors.subText,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(start = 6.dp, top = 8.dp, bottom = 2.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableTaskItem(
    task: Task,
    extras: TaskListExtras,
    settings: TaskSettings,
    subtaskMode: TaskSortMode,
    subtaskAscending: Boolean,
    selected: Boolean,
    selectionMode: Boolean,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onToggleSelection: () -> Unit,
    onEnterSelection: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> onComplete()
                SwipeToDismissBoxValue.EndToStart -> onDelete()
                SwipeToDismissBoxValue.Settled -> Unit
            }
            false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { TaskSwipeBackground() },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = !selectionMode,
    ) {
        TaskItem(
            task = task,
            extras = extras,
            settings = settings,
            subtaskMode = subtaskMode,
            subtaskAscending = subtaskAscending,
            selected = selected,
            selectionMode = selectionMode,
            onComplete = onComplete,
            onDelete = onDelete,
            onEdit = onEdit,
            onToggleSelection = onToggleSelection,
            onEnterSelection = onEnterSelection,
        )
    }
}

@Composable
private fun TaskSwipeBackground() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .clip(RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(Color(0xFF43A047).copy(alpha = 0.16f))
                .padding(start = 20.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Complete",
                tint = Color(0xFF2E7D32),
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(Color(0xFFE53935).copy(alpha = 0.14f))
                .padding(end = 20.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFC62828),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskItem(
    task: Task,
    extras: TaskListExtras,
    settings: TaskSettings,
    subtaskMode: TaskSortMode,
    subtaskAscending: Boolean,
    selected: Boolean,
    selectionMode: Boolean,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onToggleSelection: () -> Unit,
    onEnterSelection: () -> Unit,
) {
    val priorityColor = when (task.priority) {
        Task.Priority.HIGH -> Color(0xFFE53935)
        Task.Priority.MEDIUM -> Color(0xFFFB8C00)
        Task.Priority.LOW -> Color(0xFF43A047)
        else -> SaltTheme.colors.subText
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = {
                    if (selectionMode) onToggleSelection() else onEdit()
                },
                onLongClick = onEnterSelection,
            ),
        color = if (selected) {
            SaltTheme.colors.highlight.copy(alpha = 0.13f)
        } else {
            SaltTheme.colors.subBackground.copy(alpha = 0.5f)
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { if (selectionMode) onToggleSelection() else onComplete() },
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = if (selected || task.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = if (selectionMode) "Select task" else if (task.isCompleted) "Mark incomplete" else "Mark complete",
                    tint = when {
                        selected -> SaltTheme.colors.highlight
                        task.isCompleted -> Color(0xFF43A047)
                        else -> priorityColor
                    },
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = if (task.isCompleted)
                        SaltTheme.colors.text.copy(alpha = 0.5f)
                    else
                        SaltTheme.colors.text,
                    maxLines = if (settings.showFullTaskTitle) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (settings.showDescription && !task.notes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = task.notes.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = SaltTheme.colors.subText,
                        maxLines = if (settings.showFullDescription) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (settings.showStartDate && task.hideUntil > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "开始 ${formatShortDate(task.hideUntil, settings)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SaltTheme.colors.subText,
                    )
                }
                if (settings.showDueDate && task.hasDueDate()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    val isOverdue = task.dueDate < System.currentTimeMillis() && !task.isCompleted
                    Text(
                        text = "截止 ${formatShortDate(task.dueDate, settings)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isOverdue) Color(0xFFE53935) else SaltTheme.colors.subText,
                    )
                }
                TaskInlineSubtasks(
                    subtasks = extras.subtasks,
                    parentCompleted = task.isCompleted,
                    subtaskMode = subtaskMode,
                    subtaskAscending = subtaskAscending,
                )
                TaskExtrasChips(extras = extras)
            }

            if (settings.showPriorityIndicator && task.priority != Task.Priority.NONE) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(priorityColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (!selectionMode) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = SaltTheme.colors.subText.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskInlineSubtasks(
    subtasks: List<TaskSubtaskDraft>,
    parentCompleted: Boolean,
    subtaskMode: TaskSortMode,
    subtaskAscending: Boolean,
) {
    val sortedSubtasks = remember(subtasks, subtaskMode, subtaskAscending) {
        subtasks.sortedSubtasksForDisplay(subtaskMode, subtaskAscending)
    }
    val visibleSubtasks = sortedSubtasks
        .filter { it.title.isNotBlank() }
        .take(3)
    if (visibleSubtasks.isEmpty()) return

    Spacer(modifier = Modifier.height(6.dp))
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        visibleSubtasks.forEach { subtask ->
            val completed = parentCompleted || subtask.completed
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = if (completed) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (completed) Color(0xFF43A047) else SaltTheme.colors.subText,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = subtask.title,
                    color = if (completed) {
                        SaltTheme.colors.subText.copy(alpha = 0.7f)
                    } else {
                        SaltTheme.colors.subText
                    },
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (completed) TextDecoration.LineThrough else TextDecoration.None,
                )
            }
        }
        val remaining = subtasks.count { it.title.isNotBlank() } - visibleSubtasks.size
        if (remaining > 0) {
            Text(
                text = "还有 $remaining 个子任务",
                color = SaltTheme.colors.subText.copy(alpha = 0.72f),
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun TaskExtrasChips(extras: TaskListExtras) {
    val chips = buildList {
        if (extras.subtaskCount > 0) {
            add("${extras.completedSubtaskCount}/${extras.subtaskCount} 子任务")
        }
        if (extras.attachmentCount > 0) {
            add("${extras.attachmentCount} 附件")
        }
        if (extras.reminderCount > 0) {
            add("${extras.reminderCount} 提醒")
        }
        extras.tagNames.take(3).forEach { add("#$it") }
    }
    if (chips.isEmpty()) return

    Spacer(modifier = Modifier.height(6.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        chips.forEach { chip ->
            Text(
                text = chip,
                color = SaltTheme.colors.subText,
                fontSize = 12.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(SaltTheme.colors.background.copy(alpha = 0.9f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}

private fun List<Task>.groupForDisplay(): List<TaskDisplayGroup> {
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    return groupBy { task -> task.displayGroupTitle(today, zone) }
        .map { (title, tasks) -> TaskDisplayGroup(title = title, tasks = tasks) }
        .sortedBy { it.title.displayGroupSortIndex() }
}

private fun List<Task>.groupForDisplay(
    groupMode: TaskSortMode,
    ascending: Boolean,
    extras: Map<Long, TaskListExtras>,
): List<TaskDisplayGroup> {
    if (groupMode == TaskSortMode.NONE) {
        return listOf(TaskDisplayGroup(title = "", tasks = this))
    }
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    val groups = groupBy { task ->
        when (groupMode) {
            TaskSortMode.DUE_DATE -> task.dueDateGroupTitle(today, zone)
            TaskSortMode.START_DATE -> task.startDateGroupTitle(today, zone)
            TaskSortMode.PRIORITY -> task.priorityGroupTitle()
            TaskSortMode.MODIFIED -> task.timestampGroupTitle(task.modificationDate, "未修改", today, zone)
            TaskSortMode.CREATED -> task.timestampGroupTitle(task.creationDate, "未创建", today, zone)
            TaskSortMode.LIST -> extras[task.id]?.tagNames?.firstOrNull()?.takeIf { it.isNotBlank() } ?: "无清单"
            TaskSortMode.COMPLETED -> task.timestampGroupTitle(task.completionDate, "未完成", today, zone)
            TaskSortMode.TITLE -> task.title.orEmpty().firstOrNull()?.uppercaseChar()?.toString() ?: "#"
            TaskSortMode.MY_ORDER,
            TaskSortMode.AUTO,
            TaskSortMode.NONE -> ""
        }
    }
        .map { (title, tasks) -> TaskDisplayGroup(title = title, tasks = tasks) }
        .sortedBy { it.groupSortIndex(groupMode) }
    return if (ascending) groups else groups.reversed()
}

private fun List<Task>.placeCompleted(
    completedAtBottom: Boolean,
    completedMode: TaskSortMode,
    completedAscending: Boolean,
): List<Task> {
    if (!completedAtBottom || none { it.isCompleted }) return this
    val activeTasks = filterNot { it.isCompleted }
    val completedTasks = filter { it.isCompleted }.sortedTasksForDisplay(completedMode, completedAscending)
    return activeTasks + completedTasks
}

private fun List<Task>.sortedTasksForDisplay(mode: TaskSortMode, ascending: Boolean): List<Task> {
    val sorted = when (mode) {
        TaskSortMode.DUE_DATE -> sortedWith(
            compareBy<Task> { if (it.dueDate > 0) 0 else 1 }
                .thenBy { it.dueDate }
                .thenByDescending { it.modificationDate }
        )
        TaskSortMode.START_DATE -> sortedWith(
            compareBy<Task> { if (it.hideUntil > 0) 0 else 1 }
                .thenBy { it.hideUntil }
                .thenByDescending { it.modificationDate }
        )
        TaskSortMode.PRIORITY -> sortedWith(
            compareBy<Task> { it.priority }
                .thenBy { if (it.dueDate > 0) 0 else 1 }
                .thenBy { it.dueDate }
        )
        TaskSortMode.TITLE -> sortedBy { it.title.orEmpty().lowercase(Locale.getDefault()) }
        TaskSortMode.MODIFIED -> sortedBy { it.modificationDate }
        TaskSortMode.CREATED -> sortedBy { it.creationDate }
        TaskSortMode.COMPLETED -> sortedBy { it.completionDate }
        TaskSortMode.MY_ORDER -> sortedBy { it.order ?: Long.MAX_VALUE }
        TaskSortMode.AUTO -> sortedWith(
            compareBy<Task> { it.priority }
                .thenBy { if (it.dueDate > 0) 0 else 1 }
                .thenBy { it.dueDate }
        )
        TaskSortMode.LIST,
        TaskSortMode.NONE -> this
    }
    return if (ascending) sorted else sorted.reversed()
}

private fun List<TaskSubtaskDraft>.sortedSubtasksForDisplay(
    mode: TaskSortMode,
    ascending: Boolean,
): List<TaskSubtaskDraft> {
    val sorted = when (mode) {
        TaskSortMode.TITLE -> sortedBy { it.title.lowercase(Locale.getDefault()) }
        TaskSortMode.COMPLETED -> sortedBy { if (it.completed) 1 else 0 }
        TaskSortMode.MY_ORDER,
        TaskSortMode.NONE,
        TaskSortMode.DUE_DATE,
        TaskSortMode.START_DATE,
        TaskSortMode.PRIORITY,
        TaskSortMode.MODIFIED,
        TaskSortMode.CREATED,
        TaskSortMode.LIST,
        TaskSortMode.AUTO -> this
    }
    return if (ascending) sorted else sorted.reversed()
}

private fun Task.dueDateGroupTitle(today: LocalDate, zone: ZoneId): String {
    if (isCompleted) return "已完成"
    if (dueDate <= 0) return "无截止日期"
    val date = Instant.ofEpochMilli(dueDate).atZone(zone).toLocalDate()
    return when {
        date.isBefore(today) -> "逾期"
        date == today -> "今天"
        date == today.plusDays(1) -> "明天"
        date.isBefore(today.plusWeeks(1)) -> "未来 7 天"
        else -> "以后"
    }
}

private fun Task.startDateGroupTitle(today: LocalDate, zone: ZoneId): String {
    if (hideUntil <= 0) return "无开始日期"
    val date = Instant.ofEpochMilli(hideUntil).atZone(zone).toLocalDate()
    return when {
        date.isBefore(today) -> "已开始"
        date == today -> "今天"
        date == today.plusDays(1) -> "明天"
        date.isBefore(today.plusWeeks(1)) -> "未来 7 天"
        else -> "以后"
    }
}

private fun Task.timestampGroupTitle(
    timestamp: Long,
    emptyLabel: String,
    today: LocalDate,
    zone: ZoneId,
): String {
    if (timestamp <= 0) return emptyLabel
    val date = Instant.ofEpochMilli(timestamp).atZone(zone).toLocalDate()
    return when {
        date == today -> "今天"
        date == today.minusDays(1) -> "昨天"
        date.isAfter(today.minusWeeks(1)) -> "过去 7 天"
        else -> "更早"
    }
}

private fun Task.priorityGroupTitle(): String =
    when (priority) {
        Task.Priority.HIGH -> "高优先级"
        Task.Priority.MEDIUM -> "中优先级"
        Task.Priority.LOW -> "低优先级"
        else -> "无优先级"
    }

private fun TaskDisplayGroup.groupSortIndex(mode: TaskSortMode): Int =
    when (mode) {
        TaskSortMode.DUE_DATE -> when (title) {
            "逾期" -> 0
            "今天" -> 1
            "明天" -> 2
            "未来 7 天" -> 3
            "以后" -> 4
            "无截止日期" -> 5
            "已完成" -> 6
            else -> 7
        }
        TaskSortMode.START_DATE -> when (title) {
            "已开始" -> 0
            "今天" -> 1
            "明天" -> 2
            "未来 7 天" -> 3
            "以后" -> 4
            "无开始日期" -> 5
            else -> 6
        }
        TaskSortMode.PRIORITY -> when (title) {
            "高优先级" -> 0
            "中优先级" -> 1
            "低优先级" -> 2
            "无优先级" -> 3
            else -> 4
        }
        TaskSortMode.MODIFIED,
        TaskSortMode.CREATED,
        TaskSortMode.COMPLETED -> when (title) {
            "今天" -> 0
            "昨天" -> 1
            "过去 7 天" -> 2
            "更早" -> 3
            else -> 4
        }
        TaskSortMode.LIST,
        TaskSortMode.TITLE -> title.firstOrNull()?.code ?: Int.MAX_VALUE
        TaskSortMode.NONE,
        TaskSortMode.MY_ORDER,
        TaskSortMode.AUTO -> 0
    }

private fun Task.displayGroupTitle(today: LocalDate, zone: ZoneId): String {
    if (isCompleted) return "已完成"
    if (dueDate <= 0) return "无截止日期"
    val date = Instant.ofEpochMilli(dueDate).atZone(zone).toLocalDate()
    return when {
        date.isBefore(today) -> "逾期"
        date == today -> "今天"
        date == today.plusDays(1) -> "明天"
        date.isBefore(today.plusWeeks(1)) -> "未来 7 天"
        else -> "以后"
    }
}

private fun String.displayGroupSortIndex(): Int =
    when (this) {
        "逾期" -> 0
        "今天" -> 1
        "明天" -> 2
        "未来 7 天" -> 3
        "以后" -> 4
        "无截止日期" -> 5
        "已完成" -> 6
        else -> 7
    }

private fun parseTags(text: String): List<String> =
    text.split(',', '，', '#', ' ')
        .map { it.trim() }
        .filter { it.isNotBlank() }

private fun mergeTaskTagList(tags: List<String>, defaultListName: String?): List<String> =
    (tags + listOfNotNull(defaultListName?.trim()?.takeIf { it.isNotBlank() }))
        .distinctBy { it.lowercase(Locale.getDefault()) }

private fun subtaskSummary(subtasks: List<TaskSubtaskDraft>): String =
    when (val count = subtasks.count { it.title.isNotBlank() }) {
        0 -> "添加子任务"
        1 -> subtasks.first { it.title.isNotBlank() }.title
        else -> {
            val completed = subtasks.count { it.title.isNotBlank() && it.completed }
            "$count 个子任务 · $completed 已完成"
        }
    }

private fun attachmentSummary(attachments: List<TaskAttachmentDraft>): String =
    when (attachments.size) {
        0 -> "添加附件"
        1 -> attachments.first().name
        else -> "${attachments.size} 个附件"
    }

private fun reminderSummary(
    reminders: List<TaskReminderDraft>,
    startDate: Long,
    dueDate: Long,
    settings: TaskSettings,
): String =
    when (reminders.size) {
        0 -> "添加提醒"
        1 -> reminderLabel(reminders.first(), startDate, dueDate, settings)
        else -> "${reminders.size} 个提醒"
    }

private fun reminderLabel(
    reminder: TaskReminderDraft,
    startDate: Long,
    dueDate: Long,
    settings: TaskSettings,
): String {
    val base = when (reminder.type) {
        Alarm.TYPE_DATE_TIME -> {
            if (reminder.time > 0) formatEditorTimestamp(reminder.time, settings) else "具体时间"
        }
        Alarm.TYPE_REL_START -> relativeReminderLabel(
            anchor = if (startDate > 0) "开始" else "开始日期",
            offset = reminder.time,
        )
        Alarm.TYPE_REL_END -> when {
            reminder.time == ONE_DAY_MILLIS &&
                    reminder.repeat == 6 &&
                    reminder.interval == ONE_DAY_MILLIS ->
                if (dueDate > 0) "逾期后每天提醒" else "逾期后每天提醒"
            else -> relativeReminderLabel(
                anchor = if (dueDate > 0) "到期" else "截止日期",
                offset = reminder.time,
            )
        }
        Alarm.TYPE_RANDOM -> "随机提醒 · 每 ${durationLabel(reminder.time)}"
        else -> "提醒"
    }
    return if (reminder.repeat > 0 && reminder.interval > 0L) {
        "$base · 重复 ${reminder.repeat} 次，每 ${durationLabel(reminder.interval)}"
    } else {
        base
    }
}

private fun relativeReminderLabel(anchor: String, offset: Long): String =
    when {
        offset == 0L -> "${anchor}时"
        offset < 0L -> "${anchor}前 ${durationLabel(-offset)}"
        else -> "${anchor}后 ${durationLabel(offset)}"
    }

private fun durationLabel(millis: Long): String =
    when {
        millis <= 0L -> "0 分钟"
        millis % ONE_WEEK_MILLIS == 0L -> "${millis / ONE_WEEK_MILLIS} 周"
        millis % ONE_DAY_MILLIS == 0L -> "${millis / ONE_DAY_MILLIS} 天"
        millis % ONE_HOUR_MILLIS == 0L -> "${millis / ONE_HOUR_MILLIS} 小时"
        else -> "${millis / ONE_MINUTE_MILLIS} 分钟"
    }

private fun ReminderUnit.label(amount: Int): String =
    if (amount <= 1) label else label

private fun bestReminderUnit(millis: Long): ReminderUnit {
    val value = abs(millis)
    return when {
        value > 0L && value % ONE_WEEK_MILLIS == 0L -> ReminderUnit.WEEKS
        value > 0L && value % ONE_DAY_MILLIS == 0L -> ReminderUnit.DAYS
        value > 0L && value % ONE_HOUR_MILLIS == 0L -> ReminderUnit.HOURS
        else -> ReminderUnit.MINUTES
    }
}

private fun List<TaskReminderDraft>.dedupeReminders(): List<TaskReminderDraft> =
    distinctBy { listOf(it.type, it.time, it.repeat, it.interval) }

private fun recurrenceLabel(value: String?): String {
    val draft = parseRepeatDraft(value)
    return when (draft.frequency) {
        RepeatFrequency.NONE -> "不重复"
        RepeatFrequency.DAILY -> draft.intervalLabel("天")
        RepeatFrequency.WEEKLY -> buildString {
            append(draft.intervalLabel("周"))
            if (draft.weekDays.isNotEmpty()) {
                append(" · ")
                append(draft.weekDays.sorted().joinToString("") { it.chineseShortName() })
            }
        }
        RepeatFrequency.MONTHLY -> buildString {
            append(draft.intervalLabel("月"))
            when (draft.monthlyMode) {
                RepeatMonthlyMode.DAY_OF_MONTH -> draft.monthDay?.let {
                    append(" · $it 日")
                }
                RepeatMonthlyMode.DAY_OF_WEEK -> {
                    append(" · ")
                    append(draft.monthOrdinal.ordinalLabel())
                    append("周")
                    append(draft.monthWeekday.chineseShortName())
                }
            }
        }
        RepeatFrequency.YEARLY -> draft.intervalLabel("年")
    }
}

private fun RepeatDraft.intervalLabel(unit: String): String =
    if (interval <= 1) "每$unit" else "每 $interval $unit"

private data class RepeatDraft(
    val frequency: RepeatFrequency = RepeatFrequency.NONE,
    val interval: Int = 1,
    val weekDays: Set<DayOfWeek> = emptySet(),
    val monthlyMode: RepeatMonthlyMode = RepeatMonthlyMode.DAY_OF_MONTH,
    val monthDay: Int? = null,
    val monthOrdinal: Int = 1,
    val monthWeekday: DayOfWeek = DayOfWeek.MONDAY,
    val endMode: RepeatEndMode = RepeatEndMode.NEVER,
    val count: Int? = null,
    val untilText: String? = null,
)

private enum class RepeatFrequency {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
}

private enum class RepeatEndMode {
    NEVER,
    COUNT,
    UNTIL,
}

private enum class RepeatMonthlyMode {
    DAY_OF_MONTH,
    DAY_OF_WEEK,
}

private fun repeatValidationError(
    frequency: RepeatFrequency,
    intervalText: String,
    monthlyMode: RepeatMonthlyMode,
    monthDayText: String,
    monthOrdinalText: String,
    endMode: RepeatEndMode,
    countText: String,
    untilText: String,
): String? {
    if (frequency == RepeatFrequency.NONE) return null
    val interval = intervalText.toIntOrNull()
    if (interval == null || interval <= 0) return "重复间隔必须大于 0"
    if (frequency == RepeatFrequency.MONTHLY) {
        when (monthlyMode) {
            RepeatMonthlyMode.DAY_OF_MONTH -> {
                val day = monthDayText.toIntOrNull()
                if (day == null || day !in 1..31) return "每月日期必须在 1 到 31 之间"
            }
            RepeatMonthlyMode.DAY_OF_WEEK -> {
                val ordinal = monthOrdinalText.toIntOrNull()
                if (ordinal == null || ordinal == 0 || ordinal < -1 || ordinal > 5) {
                    return "星期位置必须为 -1 或 1 到 5"
                }
            }
        }
    }
    if (endMode == RepeatEndMode.COUNT && (countText.toIntOrNull() ?: 0) <= 0) {
        return "重复次数必须大于 0"
    }
    if (endMode == RepeatEndMode.UNTIL && parseUntilDate(untilText) == null) {
        return "截止日期格式应为 yyyy-MM-dd"
    }
    return null
}

private fun parseRepeatDraft(value: String?): RepeatDraft {
    if (value.isNullOrBlank()) return RepeatDraft()
    val params = value.removePrefix("RRULE:")
        .split(';')
        .mapNotNull {
            val parts = it.split('=', limit = 2)
            if (parts.size == 2) parts[0].uppercase(Locale.US) to parts[1] else null
        }
        .toMap()
    val frequency = when (params["FREQ"]?.uppercase(Locale.US)) {
        "DAILY" -> RepeatFrequency.DAILY
        "WEEKLY" -> RepeatFrequency.WEEKLY
        "MONTHLY" -> RepeatFrequency.MONTHLY
        "YEARLY" -> RepeatFrequency.YEARLY
        else -> RepeatFrequency.NONE
    }
    val byDayParts = params["BYDAY"]?.split(',').orEmpty()
    val weekDays = if (frequency == RepeatFrequency.WEEKLY) {
        byDayParts
            .mapNotNull { parseWeekday(it) }
            .toSet()
    } else {
        emptySet()
    }
    val monthDay = params["BYMONTHDAY"]
        ?.split(',')
        ?.firstNotNullOfOrNull { it.toIntOrNull()?.coerceIn(1, 31) }
    val monthByDay = if (frequency == RepeatFrequency.MONTHLY) {
        byDayParts.firstNotNullOfOrNull { parseOrdinalWeekday(it) }
    } else {
        null
    }
    val count = params["COUNT"]?.toIntOrNull()
    val until = params["UNTIL"]?.take(8)?.let {
        runCatching {
            LocalDate.parse(it, DateTimeFormatter.BASIC_ISO_DATE).toString()
        }.getOrNull()
    }
    return RepeatDraft(
        frequency = frequency,
        interval = params["INTERVAL"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1,
        weekDays = weekDays,
        monthlyMode = if (monthByDay != null) RepeatMonthlyMode.DAY_OF_WEEK else RepeatMonthlyMode.DAY_OF_MONTH,
        monthDay = monthDay,
        monthOrdinal = monthByDay?.ordinal ?: 1,
        monthWeekday = monthByDay?.day ?: DayOfWeek.MONDAY,
        endMode = when {
            count != null -> RepeatEndMode.COUNT
            until != null -> RepeatEndMode.UNTIL
            else -> RepeatEndMode.NEVER
        },
        count = count,
        untilText = until,
    )
}

private fun buildRRule(
    frequency: RepeatFrequency,
    interval: Int,
    weekDays: Set<DayOfWeek>,
    monthlyMode: RepeatMonthlyMode,
    monthDay: Int?,
    monthOrdinal: Int,
    monthWeekday: DayOfWeek,
    endMode: RepeatEndMode,
    count: Int?,
    untilText: String,
): String? {
    if (frequency == RepeatFrequency.NONE) return null
    val parts = mutableListOf("FREQ=${frequency.name}")
    if (interval > 1) parts += "INTERVAL=$interval"
    if (frequency == RepeatFrequency.WEEKLY && weekDays.isNotEmpty()) {
        parts += "BYDAY=${weekDays.sorted().joinToString(",") { it.rruleName() }}"
    }
    if (frequency == RepeatFrequency.MONTHLY) {
        when (monthlyMode) {
            RepeatMonthlyMode.DAY_OF_MONTH -> {
                monthDay?.let { parts += "BYMONTHDAY=${it.coerceIn(1, 31)}" }
            }
            RepeatMonthlyMode.DAY_OF_WEEK -> {
                parts += "BYDAY=${monthOrdinal.coerceMonthlyOrdinal()}${monthWeekday.rruleName()}"
            }
        }
    }
    when (endMode) {
        RepeatEndMode.NEVER -> {}
        RepeatEndMode.COUNT -> count?.let { parts += "COUNT=$it" }
        RepeatEndMode.UNTIL -> parseUntilDate(untilText)?.let { parts += "UNTIL=$it" }
    }
    return "RRULE:${parts.joinToString(";")}"
}

private fun parseUntilDate(value: String): String? =
    runCatching {
        val date = LocalDate.parse(value.trim())
        val instant = date.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()
        DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
            .withZone(ZoneOffset.UTC)
            .format(instant)
    }.getOrNull()

private data class MonthlyByDay(
    val ordinal: Int,
    val day: DayOfWeek,
)

private fun parseOrdinalWeekday(value: String): MonthlyByDay? {
    val day = parseWeekday(value.takeLast(2)) ?: return null
    val ordinal = value.dropLast(2).toIntOrNull()?.coerceMonthlyOrdinal() ?: return null
    return MonthlyByDay(ordinal = ordinal, day = day)
}

private fun Int.coerceMonthlyOrdinal(): Int =
    when {
        this < 0 -> -1
        this == 0 -> 1
        this > 5 -> 5
        else -> this
    }

private fun Int.ordinalLabel(): String =
    if (this < 0) "最后一个" else "第 ${coerceMonthlyOrdinal()} 个"

private fun parseWeekday(value: String): DayOfWeek? =
    when (value.uppercase(Locale.US)) {
        "MO" -> DayOfWeek.MONDAY
        "TU" -> DayOfWeek.TUESDAY
        "WE" -> DayOfWeek.WEDNESDAY
        "TH" -> DayOfWeek.THURSDAY
        "FR" -> DayOfWeek.FRIDAY
        "SA" -> DayOfWeek.SATURDAY
        "SU" -> DayOfWeek.SUNDAY
        else -> null
    }

private fun DayOfWeek.rruleName(): String =
    when (this) {
        DayOfWeek.MONDAY -> "MO"
        DayOfWeek.TUESDAY -> "TU"
        DayOfWeek.WEDNESDAY -> "WE"
        DayOfWeek.THURSDAY -> "TH"
        DayOfWeek.FRIDAY -> "FR"
        DayOfWeek.SATURDAY -> "SA"
        DayOfWeek.SUNDAY -> "SU"
    }

private fun DayOfWeek.chineseShortName(): String =
    when (this) {
        DayOfWeek.MONDAY -> "一"
        DayOfWeek.TUESDAY -> "二"
        DayOfWeek.WEDNESDAY -> "三"
        DayOfWeek.THURSDAY -> "四"
        DayOfWeek.FRIDAY -> "五"
        DayOfWeek.SATURDAY -> "六"
        DayOfWeek.SUNDAY -> "日"
    }

private fun LocalDate.endOfDayMillis(): Long =
    atTime(LocalTime.of(12, 0)).toMillis()

private fun LocalDate.toStartOfDayMillis(): Long =
    atStartOfDay().toMillis()

private fun LocalDateTime.toMillisOfDay(): Int =
    hour * HOUR_MILLIS_OF_DAY + minute * MINUTE_MILLIS_OF_DAY

private fun Long.toEditorLocalDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()

private fun Long.toStartDateSelection(dueDate: Long): Long {
    if (this <= 0 || dueDate <= 0) return if (this > 0) toEditorLocalDateTime().toLocalDate().toStartOfDayMillis() else EDITOR_NO_DAY
    val start = toEditorLocalDateTime()
    val due = dueDate.toEditorLocalDateTime()
    return when {
        this == dueDate -> START_DUE_TIME
        start.toLocalDate() == due.toLocalDate().minusDays(1) -> START_DAY_BEFORE_DUE
        start.toLocalDate() == due.toLocalDate().minusWeeks(1) -> START_WEEK_BEFORE_DUE
        start.toLocalDate() == due.toLocalDate() -> START_DUE_DATE
        else -> start.toLocalDate().toStartOfDayMillis()
    }
}

private fun duePickerSelectionToMillis(day: Long, timeMillisOfDay: Int): Long {
    if (day <= 0) return 0L
    val date = day.toEditorLocalDateTime().toLocalDate()
    return if (timeMillisOfDay == EDITOR_NO_TIME) {
        date.atTime(12, 0).toMillis()
    } else {
        date.atMillisOfDay(timeMillisOfDay).withTaskTimeMarker()
    }
}

private fun startPickerSelectionToMillis(day: Long, timeMillisOfDay: Int, dueDate: Long): Long =
    when (day) {
        EDITOR_NO_DAY -> 0L
        START_DUE_TIME -> dueDate
        START_DUE_DATE -> dueDate.takeIf { it > 0 }
            ?.toEditorLocalDateTime()
            ?.toLocalDate()
            ?.toStartMillis(timeMillisOfDay)
            ?: 0L
        START_DAY_BEFORE_DUE -> dueDate.takeIf { it > 0 }
            ?.toEditorLocalDateTime()
            ?.toLocalDate()
            ?.minusDays(1)
            ?.toStartMillis(timeMillisOfDay)
            ?: 0L
        START_WEEK_BEFORE_DUE -> dueDate.takeIf { it > 0 }
            ?.toEditorLocalDateTime()
            ?.toLocalDate()
            ?.minusWeeks(1)
            ?.toStartMillis(timeMillisOfDay)
            ?: 0L
        else -> day.toEditorLocalDateTime().toLocalDate().toStartMillis(timeMillisOfDay)
    }

private fun LocalDate.toStartMillis(timeMillisOfDay: Int): Long =
    if (timeMillisOfDay == EDITOR_NO_TIME) {
        toStartOfDayMillis()
    } else {
        atMillisOfDay(timeMillisOfDay).withTaskTimeMarker()
    }

private fun LocalDate.atMillisOfDay(timeMillisOfDay: Int): Long {
    val hour = (timeMillisOfDay / HOUR_MILLIS_OF_DAY).coerceIn(0, 23)
    val minute = ((timeMillisOfDay % HOUR_MILLIS_OF_DAY) / MINUTE_MILLIS_OF_DAY).coerceIn(0, 59)
    return atTime(hour, minute).toMillis()
}

private fun LocalDate.nextWeekend(): LocalDate {
    val daysUntilSaturday = (DayOfWeek.SATURDAY.value - dayOfWeek.value + 7) % 7
    return plusDays(daysUntilSaturday.toLong())
}

private fun LocalDate.nextOrSame(day: DayOfWeek): LocalDate {
    val days = (day.value - dayOfWeek.value + 7) % 7
    return plusDays(days.toLong())
}

private fun LocalDate.quickDateMillis(picker: EditorPicker): Long =
    when (picker) {
        EditorPicker.START -> atTime(9, 0).toMillis()
        EditorPicker.DUE -> endOfDayMillis()
        EditorPicker.REMINDER -> atTime(9, 0).toMillis()
        EditorPicker.REPEAT -> endOfDayMillis()
    }

private fun LocalDateTime.toMillis(): Long =
    atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

private fun LocalDate.toDatePickerMillis(): Long =
    atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toDatePickerLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

private fun defaultReminderMillis(dueDate: Long): Long =
    if (dueDate > 0) dueDate else LocalDate.now().plusDays(1).atTime(9, 0).toMillis()

private fun Long.withTaskTimeMarker(): Long =
    if (this > 0 && this % ONE_MINUTE_MILLIS == 0L) this + 1000L else this

private fun Long.hasTaskTimeMarker(): Boolean =
    this > 0 && this % ONE_MINUTE_MILLIS != 0L

private fun taskListIconLabel(icon: String?): String =
    when (icon?.trim()?.lowercase(Locale.getDefault())) {
        "list" -> "列"
        "label" -> "签"
        "flag" -> "旗"
        "star" -> "星"
        "work" -> "工"
        "home" -> "家"
        else -> ""
    }

private fun TaskDefaultReminder.toReminderDrafts(dueDate: Long): List<TaskReminderDraft> =
    when (this) {
        TaskDefaultReminder.NONE -> emptyList()
        TaskDefaultReminder.AT_DUE_TIME -> {
            if (dueDate > 0) listOf(TaskReminderDraft(time = 0L, type = Alarm.TYPE_REL_END)) else emptyList()
        }
        TaskDefaultReminder.TEN_MINUTES_BEFORE -> {
            if (dueDate > 0) {
                listOf(TaskReminderDraft(time = -TEN_MINUTES_MILLIS, type = Alarm.TYPE_REL_END))
            } else {
                emptyList()
            }
        }
        TaskDefaultReminder.TOMORROW_MORNING -> listOf(
            TaskReminderDraft(
                time = LocalDate.now().plusDays(1).atTime(9, 0).toMillis(),
                type = Alarm.TYPE_DATE_TIME,
            )
        )
    }

private fun formatEditorTimestamp(timestamp: Long, settings: TaskSettings): String =
    SimpleDateFormat(settings.dateTimePattern(), Locale.getDefault()).format(Date(timestamp))

private fun formatInfoTimestamp(timestamp: Long, settings: TaskSettings): String =
    SimpleDateFormat(settings.dateTimePattern(), Locale.getDefault()).format(Date(timestamp))

private fun formatShortDate(timestamp: Long, settings: TaskSettings): String =
    SimpleDateFormat(
        if (settings.alwaysDisplayFullDate) settings.dateTimePattern() else "MMM d, yyyy",
        Locale.getDefault(),
    ).format(Date(timestamp))

private fun TaskSettings.dateTimePattern(): String =
    if (use24HourTime) "yyyy-MM-dd HH:mm" else "yyyy-MM-dd h:mm a"

private const val ONE_MINUTE_MILLIS = 60 * 1000L
private const val TEN_MINUTES_MILLIS = 10 * ONE_MINUTE_MILLIS
private const val ONE_HOUR_MILLIS = 60 * ONE_MINUTE_MILLIS
private const val ONE_DAY_MILLIS = 24 * ONE_HOUR_MILLIS
private const val ONE_WEEK_MILLIS = 7 * ONE_DAY_MILLIS
private const val MINUTE_MILLIS_OF_DAY = 60 * 1000
private const val HOUR_MILLIS_OF_DAY = 60 * MINUTE_MILLIS_OF_DAY
private const val EDITOR_NO_DAY = 0L
private const val EDITOR_NO_TIME = 0
private const val START_DUE_DATE = -1L
private const val START_DAY_BEFORE_DUE = -2L
private const val START_WEEK_BEFORE_DUE = -3L
private const val START_DUE_TIME = -4L
private const val NINE_AM_MILLIS_OF_DAY = 9 * HOUR_MILLIS_OF_DAY
private const val ONE_PM_MILLIS_OF_DAY = 13 * HOUR_MILLIS_OF_DAY
private const val FIVE_PM_MILLIS_OF_DAY = 17 * HOUR_MILLIS_OF_DAY
private const val EIGHT_PM_MILLIS_OF_DAY = 20 * HOUR_MILLIS_OF_DAY

private fun TaskDefaultPriority.toTaskPriority(): Int =
    when (this) {
        TaskDefaultPriority.NONE -> Task.Priority.NONE
        TaskDefaultPriority.LOW -> Task.Priority.LOW
        TaskDefaultPriority.MEDIUM -> Task.Priority.MEDIUM
        TaskDefaultPriority.HIGH -> Task.Priority.HIGH
    }

private fun Context.canScheduleExactTaskAlarms(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            (getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()

private fun Context.exactAlarmSettingsIntent(): Intent =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            .apply { data = Uri.parse("package:$packageName") }
    } else {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }
    }

private fun Context.displayName(uri: Uri): String {
    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (index >= 0 && cursor.moveToFirst()) {
            return cursor.getString(index)
        }
    }
    return uri.lastPathSegment ?: "attachment"
}

private fun Context.importTaskAttachment(uri: Uri): TaskAttachmentDraft? =
    runCatching {
        val displayName = displayName(uri).sanitizeAttachmentName()
        val directory = File(filesDir, "attachments").apply { mkdirs() }
        val target = File(directory, uniqueAttachmentFileName(directory, displayName))
        contentResolver.openInputStream(uri)?.use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        } ?: return null
        val localUri = FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            target,
        )
        TaskAttachmentDraft(
            name = displayName,
            uri = localUri.toString(),
        )
    }.getOrNull()

private fun String.sanitizeAttachmentName(): String {
    val cleaned = replace(Regex("""[\\/:*?"<>|]"""), "_")
        .replace(Regex("\\s+"), " ")
        .trim()
    return cleaned.ifBlank { "attachment" }.take(120)
}

private fun uniqueAttachmentFileName(directory: File, displayName: String): String {
    val base = displayName.substringBeforeLast('.', displayName)
    val extension = displayName.substringAfterLast('.', missingDelimiterValue = "")
        .takeIf { it != displayName && it.isNotBlank() }
        ?.let { ".$it" }
        .orEmpty()
    var candidate = displayName
    var index = 1
    while (File(directory, candidate).exists()) {
        candidate = "${base}_$index$extension"
        index++
    }
    return candidate
}

private fun Context.openAttachment(attachment: TaskAttachmentDraft) {
    val uri = runCatching { Uri.parse(attachment.uri) }.getOrNull()
        ?: return toastAttachmentOpenFailed()
    val type = runCatching { contentResolver.getType(uri) }.getOrNull() ?: "*/*"
    val intent = Intent(Intent.ACTION_VIEW)
        .setDataAndType(uri, type)
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    runCatching {
        startActivity(Intent.createChooser(intent, attachment.name))
    }.onFailure {
        toastAttachmentOpenFailed()
    }
}

private fun Context.shareAttachment(attachment: TaskAttachmentDraft) {
    val uri = runCatching { Uri.parse(attachment.uri) }.getOrNull()
        ?: return toastAttachmentOpenFailed()
    val type = runCatching { contentResolver.getType(uri) }.getOrNull() ?: "*/*"
    val intent = Intent(Intent.ACTION_SEND)
        .setType(type)
        .putExtra(Intent.EXTRA_STREAM, uri)
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    runCatching {
        startActivity(Intent.createChooser(intent, attachment.name))
    }.onFailure {
        toastAttachmentOpenFailed()
    }
}

private fun Context.hasPersistedReadPermission(uri: String): Boolean {
    val parsed = runCatching { Uri.parse(uri) }.getOrNull() ?: return false
    if (parsed.authority == "$packageName.provider") return true
    if (parsed.scheme != "content") return true
    return contentResolver.persistedUriPermissions.any {
        it.uri == parsed && it.isReadPermission
    }
}

private fun Context.toastAttachmentOpenFailed() {
    Toast.makeText(this, "无法打开附件，文件权限可能已失效", Toast.LENGTH_SHORT).show()
}
