package com.qingguang.qingnote.tasks.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.ExpandCircleDown
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SubdirectoryArrowRight
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.moriafly.salt.ui.SaltTheme
import com.qingguang.qingnote.tasks.TaskDrawerTag
import com.qingguang.qingnote.tasks.TaskSettings
import com.qingguang.qingnote.tasks.TaskSortMode
import com.qingguang.qingnote.tasks.TaskViewFilter
import com.qingguang.qingnote.utils.SettingsPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class TaskListAppearanceDraft(
    val name: String,
    val color: Int?,
    val icon: String?,
)

private data class TaskListIconOption(
    val value: String?,
    val label: String,
)

private val TaskListColorOptions = listOf(
    0xFF5E97F6.toInt(),
    0xFF26A69A.toInt(),
    0xFFFFB300.toInt(),
    0xFFEF5350.toInt(),
    0xFF7E57C2.toInt(),
    0xFF78909C.toInt(),
)

private val TaskListIconOptions = listOf(
    TaskListIconOption(null, "无"),
    TaskListIconOption("list", "列"),
    TaskListIconOption("label", "签"),
    TaskListIconOption("flag", "旗"),
    TaskListIconOption("star", "星"),
    TaskListIconOption("work", "工"),
    TaskListIconOption("home", "家"),
)

@Composable
fun TasksHomeActions(
    viewModel: TaskListViewModel,
    onSettingsClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by SettingsPreferences.taskSettings.collectAsState(TaskSettings())
    val scope = rememberCoroutineScope()
    var showSearchDialog by rememberSaveable { mutableStateOf(false) }
    var showDrawer by rememberSaveable { mutableStateOf(false) }
    var showListPicker by rememberSaveable { mutableStateOf(false) }
    var showCreateListDialog by rememberSaveable { mutableStateOf(false) }
    var showRenameListDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteListDialog by rememberSaveable { mutableStateOf(false) }
    val selectedTag = remember(uiState.availableTags, uiState.selectedTag) {
        uiState.availableTags.firstOrNull { tag ->
            tag.name.equals(uiState.selectedTag, ignoreCase = true)
        }
    }

    TaskMoreMenu(
        hasSearchQuery = uiState.searchQuery.isNotBlank(),
        selectedListName = uiState.selectedTag,
        defaultListName = settings.defaultListName,
        showCompleted = uiState.showCompleted,
        onChooseList = { showListPicker = true },
        onCreateList = { showCreateListDialog = true },
        onRenameList = { showRenameListDialog = true },
        onDeleteList = { showDeleteListDialog = true },
        onMoveListUp = { viewModel.moveSelectedList(up = true) },
        onMoveListDown = { viewModel.moveSelectedList(up = false) },
        onToggleDefaultList = {
            val selected = uiState.selectedTag.orEmpty()
            scope.launch {
                SettingsPreferences.changeTaskDefaultList(
                    if (settings.defaultListName.equals(selected, ignoreCase = true)) "" else selected
                )
            }
        },
        onToggleCompleted = { viewModel.setShowCompleted(!uiState.showCompleted) },
        onClearSearch = { viewModel.setSearchQuery("") },
        onRefresh = { viewModel.loadTasks() },
        onSettings = onSettingsClick,
    )
    IconButton(onClick = { showSearchDialog = true }) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = "搜索任务",
            tint = SaltTheme.colors.text,
        )
    }
    TaskSortMenu(
        state = uiState,
        onGroupModeChanged = { viewModel.setGroupMode(it) },
        onGroupAscendingChanged = { viewModel.setGroupAscending(it) },
        onSortModeChanged = { viewModel.setSortMode(it) },
        onSortAscendingChanged = { viewModel.setSortAscending(it) },
        onSubtaskModeChanged = { viewModel.setSubtaskMode(it) },
        onSubtaskAscendingChanged = { viewModel.setSubtaskAscending(it) },
        onCompletedAtBottomChanged = { viewModel.setCompletedAtBottom(it) },
        onCompletedModeChanged = { viewModel.setCompletedMode(it) },
        onCompletedAscendingChanged = { viewModel.setCompletedAscending(it) },
    )
    IconButton(onClick = { showDrawer = true }) {
        Icon(
            imageVector = Icons.Rounded.Menu,
            contentDescription = "task drawer",
            tint = SaltTheme.colors.text,
        )
    }

    if (showSearchDialog) {
        TaskSearchDialog(
            initialQuery = uiState.searchQuery,
            onDismiss = { showSearchDialog = false },
            onSearch = {
                viewModel.setSearchQuery(it)
                showSearchDialog = false
            },
        )
    }
    if (showListPicker) {
        TaskListPickerDialog(
            filter = uiState.filter,
            selectedTag = uiState.selectedTag,
            availableTags = uiState.availableTags,
            filterCounts = uiState.filterCounts,
            settings = settings,
            onFilterChanged = {
                viewModel.setFilter(it)
                showListPicker = false
            },
            onTagChanged = {
                viewModel.setTagFilter(it)
                showListPicker = false
            },
            onCreateList = {
                showListPicker = false
                showCreateListDialog = true
            },
            onDismiss = { showListPicker = false },
        )
    }
    if (showCreateListDialog) {
        TaskListNameDialog(
            title = "新建清单",
            initialName = "",
            confirmText = "创建",
            onDismiss = { showCreateListDialog = false },
            onConfirm = {
                viewModel.createList(it.name, it.color, it.icon)
                showCreateListDialog = false
            },
        )
    }
    if (showRenameListDialog) {
        TaskListNameDialog(
            title = "重命名清单",
            initialName = uiState.selectedTag.orEmpty(),
            initialColor = selectedTag?.color?.takeIf { it != 0 },
            initialIcon = selectedTag?.icon,
            confirmText = "保存",
            onDismiss = { showRenameListDialog = false },
            onConfirm = {
                viewModel.renameSelectedList(it.name, it.color, it.icon)
                showRenameListDialog = false
            },
        )
    }
    if (showDeleteListDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteListDialog = false },
            title = { Text("删除清单") },
            text = { Text("删除“${uiState.selectedTag.orEmpty()}”清单？任务本身不会被删除。") },
            confirmButton = {
                TextButton(onClick = {
                    val deleting = uiState.selectedTag.orEmpty()
                    viewModel.deleteSelectedList()
                    if (settings.defaultListName.equals(deleting, ignoreCase = true)) {
                        scope.launch { SettingsPreferences.changeTaskDefaultList("") }
                    }
                    showDeleteListDialog = false
                }) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteListDialog = false }) {
                    Text("取消")
                }
            },
        )
    }
    if (showDrawer) {
        TaskNavigationDrawer(
            filter = uiState.filter,
            selectedTag = uiState.selectedTag,
            availableTags = uiState.availableTags,
            filterCounts = uiState.filterCounts,
            settings = settings,
            hasSearchQuery = uiState.searchQuery.isNotBlank(),
            onFilterChanged = {
                viewModel.setFilter(it)
                showDrawer = false
            },
            onTagChanged = {
                viewModel.setTagFilter(it)
                showDrawer = false
            },
            onClearSearch = {
                viewModel.setSearchQuery("")
                showDrawer = false
            },
            onCreateList = {
                showDrawer = false
                showCreateListDialog = true
            },
            onRefresh = {
                viewModel.loadTasks()
                showDrawer = false
            },
            onSettingsClick = {
                showDrawer = false
                onSettingsClick()
            },
            onDismiss = { showDrawer = false },
        )
    }
}

private enum class TaskSortPicker {
    GROUP,
    SORT,
    SUBTASK,
    COMPLETED,
}

private val TaskGroupOptions = listOf(
    TaskSortMode.NONE,
    TaskSortMode.DUE_DATE,
    TaskSortMode.START_DATE,
    TaskSortMode.PRIORITY,
    TaskSortMode.MODIFIED,
    TaskSortMode.CREATED,
    TaskSortMode.LIST,
)

private val TaskSortOptions = listOf(
    TaskSortMode.DUE_DATE,
    TaskSortMode.START_DATE,
    TaskSortMode.PRIORITY,
    TaskSortMode.TITLE,
    TaskSortMode.MODIFIED,
    TaskSortMode.AUTO,
    TaskSortMode.CREATED,
    TaskSortMode.MY_ORDER,
)

private val TaskSubtaskOptions = listOf(
    TaskSortMode.MY_ORDER,
    TaskSortMode.DUE_DATE,
    TaskSortMode.START_DATE,
    TaskSortMode.PRIORITY,
    TaskSortMode.TITLE,
    TaskSortMode.MODIFIED,
    TaskSortMode.AUTO,
    TaskSortMode.CREATED,
)

private val TaskCompletedOptions = listOf(
    TaskSortMode.COMPLETED,
    TaskSortMode.DUE_DATE,
    TaskSortMode.START_DATE,
    TaskSortMode.PRIORITY,
    TaskSortMode.TITLE,
    TaskSortMode.MODIFIED,
    TaskSortMode.CREATED,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskSortMenu(
    state: TaskListUiState,
    onGroupModeChanged: (TaskSortMode) -> Unit,
    onGroupAscendingChanged: (Boolean) -> Unit,
    onSortModeChanged: (TaskSortMode) -> Unit,
    onSortAscendingChanged: (Boolean) -> Unit,
    onSubtaskModeChanged: (TaskSortMode) -> Unit,
    onSubtaskAscendingChanged: (Boolean) -> Unit,
    onCompletedAtBottomChanged: (Boolean) -> Unit,
    onCompletedModeChanged: (TaskSortMode) -> Unit,
    onCompletedAscendingChanged: (Boolean) -> Unit,
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var picker by rememberSaveable { mutableStateOf<TaskSortPicker?>(null) }

    IconButton(onClick = { showSheet = true }) {
        Icon(Icons.Outlined.SwapVert, contentDescription = "task sort", tint = SaltTheme.colors.text)
    }

    if (showSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            modifier = Modifier.statusBarsPadding(),
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = SaltTheme.colors.background,
            scrimColor = Color.Black.copy(alpha = 0.42f),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = { BottomSheetDefaults.DragHandle(color = SaltTheme.colors.subText.copy(alpha = 0.55f)) },
        ) {
            TaskSortSheetContent(
                state = state,
                onGroupClick = { picker = TaskSortPicker.GROUP },
                onSortClick = { picker = TaskSortPicker.SORT },
                onSubtaskClick = { picker = TaskSortPicker.SUBTASK },
                onCompletedClick = { picker = TaskSortPicker.COMPLETED },
                onGroupAscendingChanged = onGroupAscendingChanged,
                onSortAscendingChanged = onSortAscendingChanged,
                onSubtaskAscendingChanged = onSubtaskAscendingChanged,
                onCompletedAscendingChanged = onCompletedAscendingChanged,
                onCompletedAtBottomChanged = onCompletedAtBottomChanged,
            )
        }
    }

    picker?.let { currentPicker ->
        val pickerState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            modifier = Modifier.statusBarsPadding(),
            onDismissRequest = { picker = null },
            sheetState = pickerState,
            containerColor = SaltTheme.colors.background,
            scrimColor = Color.Transparent,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = { BottomSheetDefaults.DragHandle(color = SaltTheme.colors.subText.copy(alpha = 0.55f)) },
        ) {
            TaskSortPickerContent(
                picker = currentPicker,
                selected = when (currentPicker) {
                    TaskSortPicker.GROUP -> state.groupMode
                    TaskSortPicker.SORT -> state.sortMode
                    TaskSortPicker.SUBTASK -> state.subtaskMode
                    TaskSortPicker.COMPLETED -> state.completedMode
                },
                onSelected = { selected ->
                    when (currentPicker) {
                        TaskSortPicker.GROUP -> onGroupModeChanged(selected)
                        TaskSortPicker.SORT -> onSortModeChanged(selected)
                        TaskSortPicker.SUBTASK -> onSubtaskModeChanged(selected)
                        TaskSortPicker.COMPLETED -> onCompletedModeChanged(selected)
                    }
                    picker = null
                },
            )
        }
    }
}

@Composable
private fun TaskSortSheetContent(
    state: TaskListUiState,
    onGroupClick: () -> Unit,
    onSortClick: () -> Unit,
    onSubtaskClick: () -> Unit,
    onCompletedClick: () -> Unit,
    onGroupAscendingChanged: (Boolean) -> Unit,
    onSortAscendingChanged: (Boolean) -> Unit,
    onSubtaskAscendingChanged: (Boolean) -> Unit,
    onCompletedAscendingChanged: (Boolean) -> Unit,
    onCompletedAtBottomChanged: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        TaskSortRow(
            icon = Icons.Outlined.ExpandCircleDown,
            title = "分组",
            body = state.groupMode.taskSortLabel(),
            ascending = state.groupAscending,
            showAscending = state.groupMode != TaskSortMode.NONE,
            onClick = onGroupClick,
            onAscendingChanged = onGroupAscendingChanged,
        )
        TaskSortRow(
            icon = Icons.Outlined.SwapVert,
            title = "排序",
            body = state.sortMode.taskSortLabel(),
            ascending = state.sortAscending,
            onClick = onSortClick,
            onAscendingChanged = onSortAscendingChanged,
        )
        TaskSortRow(
            icon = Icons.Outlined.SubdirectoryArrowRight,
            title = "子任务",
            body = state.subtaskMode.taskSortLabel(),
            ascending = state.subtaskAscending,
            showAscending = state.subtaskMode != TaskSortMode.MY_ORDER,
            onClick = onSubtaskClick,
            onAscendingChanged = onSubtaskAscendingChanged,
        )
        HorizontalDivider(color = SaltTheme.colors.subText.copy(alpha = 0.18f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCompletedAtBottomChanged(!state.completedAtBottom) }
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Text(
                text = "将已完成任务移至底部",
                color = SaltTheme.colors.text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = state.completedAtBottom,
                onCheckedChange = onCompletedAtBottomChanged,
            )
        }
        if (state.completedAtBottom) {
            HorizontalDivider(color = SaltTheme.colors.subText.copy(alpha = 0.18f))
            TaskSortRow(
                title = "已完成",
                body = state.completedMode.taskSortLabel(),
                ascending = state.completedAscending,
                onClick = onCompletedClick,
                onAscendingChanged = onCompletedAscendingChanged,
            )
        }
    }
}

@Composable
private fun TaskSortPickerContent(
    picker: TaskSortPicker,
    selected: TaskSortMode,
    onSelected: (TaskSortMode) -> Unit,
) {
    val options = when (picker) {
        TaskSortPicker.GROUP -> TaskGroupOptions
        TaskSortPicker.SORT -> TaskSortOptions
        TaskSortPicker.SUBTASK -> TaskSubtaskOptions
        TaskSortPicker.COMPLETED -> TaskCompletedOptions
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 12.dp),
    ) {
        options.forEach { option ->
            TaskSortOptionRow(
                label = option.taskSortLabel(),
                selected = option == selected,
                onClick = { onSelected(option) },
            )
        }
    }
}

@Composable
private fun TaskSortOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge.copy(
                color = if (selected) SaltTheme.colors.highlight else SaltTheme.colors.text,
            ),
        )
    }
}

@Composable
private fun TaskSortRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Outlined.SwapVert,
    title: String,
    body: String,
    ascending: Boolean,
    showAscending: Boolean = true,
    onClick: () -> Unit,
    onAscendingChanged: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SaltTheme.colors.subText,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(24.dp)
                .alpha(0.82f),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = SaltTheme.colors.text,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = body,
                color = SaltTheme.colors.text,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        if (showAscending) {
            Spacer(modifier = Modifier.width(16.dp))
            TaskOrderingButton(
                ascending = ascending,
                onClick = { onAscendingChanged(!ascending) },
            )
        }
    }
}

@Composable
private fun TaskOrderingButton(
    ascending: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .height(32.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VerticalDivider(color = SaltTheme.colors.subText.copy(alpha = 0.28f))
        Icon(
            imageVector = if (ascending) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(16.dp),
            contentDescription = null,
            tint = SaltTheme.colors.text,
        )
        Text(
            text = if (ascending) "升序" else "降序",
            color = SaltTheme.colors.text,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun TaskSortMode.taskSortLabel(): String =
    when (this) {
        TaskSortMode.NONE -> "无"
        TaskSortMode.DUE_DATE -> "按截止日期"
        TaskSortMode.START_DATE -> "按开始日期"
        TaskSortMode.PRIORITY -> "按优先级"
        TaskSortMode.TITLE -> "按标题"
        TaskSortMode.MODIFIED -> "按最后修改"
        TaskSortMode.CREATED -> "按创建时间"
        TaskSortMode.LIST -> "按清单"
        TaskSortMode.COMPLETED -> "按完成时间"
        TaskSortMode.MY_ORDER -> "我的顺序"
        TaskSortMode.AUTO -> "智能排序"
    }

@Composable
private fun TaskMoreMenu(
    hasSearchQuery: Boolean,
    selectedListName: String?,
    defaultListName: String,
    showCompleted: Boolean,
    onChooseList: () -> Unit,
    onCreateList: () -> Unit,
    onRenameList: () -> Unit,
    onDeleteList: () -> Unit,
    onMoveListUp: () -> Unit,
    onMoveListDown: () -> Unit,
    onToggleDefaultList: () -> Unit,
    onToggleCompleted: () -> Unit,
    onClearSearch: () -> Unit,
    onRefresh: () -> Unit,
    onSettings: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Rounded.MoreVert, contentDescription = "task options", tint = SaltTheme.colors.text)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(SaltTheme.colors.popup),
        ) {
            TaskDropdownItem("选择清单") { onChooseList(); expanded = false }
            if (!selectedListName.isNullOrBlank()) {
                TaskDropdownItem("重命名清单") { onRenameList(); expanded = false }
                TaskDropdownItem("删除清单") { onDeleteList(); expanded = false }
                TaskDropdownItem(
                    if (defaultListName.equals(selectedListName, ignoreCase = true)) "取消默认清单" else "设为默认清单"
                ) { onToggleDefaultList(); expanded = false }
                TaskDropdownItem("清单上移") { onMoveListUp(); expanded = false }
                TaskDropdownItem("清单下移") { onMoveListDown(); expanded = false }
            }
            TaskDropdownItem("新建清单") { onCreateList(); expanded = false }
            TaskDropdownItem(if (showCompleted) "隐藏已完成" else "显示已完成") {
                onToggleCompleted()
                expanded = false
            }
            TaskDropdownItem("清除搜索", enabled = hasSearchQuery) {
                onClearSearch()
                expanded = false
            }
            TaskDropdownItem("刷新") { onRefresh(); expanded = false }
            TaskDropdownItem("任务设置") { onSettings(); expanded = false }
        }
    }
}

@Composable
private fun TaskDropdownItem(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        enabled = enabled,
        text = {
            Text(
                text = text,
                color = if (enabled) SaltTheme.colors.text else SaltTheme.colors.subText,
                fontSize = 14.sp,
            )
        },
        onClick = onClick,
    )
}

@Composable
private fun TaskSearchDialog(
    initialQuery: String,
    onDismiss: () -> Unit,
    onSearch: (String) -> Unit,
) {
    var query by rememberSaveable(initialQuery) { mutableStateOf(initialQuery) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(180)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("搜索任务") },
        text = {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = true,
                placeholder = { Text("任务标题或备注") },
            )
        },
        confirmButton = {
            TextButton(onClick = { onSearch(query.trim()) }) { Text("搜索") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TaskListNameDialog(
    title: String,
    initialName: String,
    initialColor: Int? = null,
    initialIcon: String? = null,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: (TaskListAppearanceDraft) -> Unit,
) {
    var name by rememberSaveable(initialName) { mutableStateOf(initialName) }
    var selectedColor by rememberSaveable(initialColor) { mutableStateOf(initialColor?.takeIf { it != 0 }) }
    var selectedIcon by rememberSaveable(initialIcon) { mutableStateOf(initialIcon?.takeIf { it.isNotBlank() }) }
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
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(80) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    placeholder = { Text("清单名称") },
                )
                Text("颜色", color = SaltTheme.colors.subText, fontSize = 13.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TaskListColorSwatch(null, selectedColor == null) { selectedColor = null }
                    TaskListColorOptions.forEach { color ->
                        TaskListColorSwatch(color, selectedColor == color) { selectedColor = color }
                    }
                }
                Text("图标", color = SaltTheme.colors.subText, fontSize = 13.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskListIconOptions.forEach { option ->
                        TaskListIconChip(option, selectedIcon == option.value) { selectedIcon = option.value }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    onConfirm(TaskListAppearanceDraft(name.trim(), selectedColor, selectedIcon))
                },
            ) { Text(confirmText) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        },
    )
}

@Composable
private fun TaskListColorSwatch(color: Int?, selected: Boolean, onClick: () -> Unit) {
    val swatchColor = color?.let { Color(it) } ?: SaltTheme.colors.subText.copy(alpha = 0.32f)
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (selected) SaltTheme.colors.highlight.copy(alpha = 0.16f) else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(swatchColor),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
            }
        }
    }
}

@Composable
private fun TaskListIconChip(option: TaskListIconOption, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = option.label,
        color = if (selected) SaltTheme.colors.highlight else SaltTheme.colors.text,
        fontSize = 14.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) SaltTheme.colors.highlight.copy(alpha = 0.14f) else SaltTheme.colors.popup)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    )
}

@Composable
private fun TaskListPickerDialog(
    filter: TaskViewFilter,
    selectedTag: String?,
    availableTags: List<TaskDrawerTag>,
    filterCounts: Map<TaskViewFilter, Int>,
    settings: TaskSettings,
    onFilterChanged: (TaskViewFilter) -> Unit,
    onTagChanged: (String?) -> Unit,
    onCreateList: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择清单") },
        text = {
            TaskDrawerList(
                filter = filter,
                selectedTag = selectedTag,
                availableTags = availableTags,
                filterCounts = filterCounts,
                settings = settings,
                onFilterChanged = onFilterChanged,
                onTagChanged = onTagChanged,
                onCreateList = onCreateList,
                showActions = true,
                modifier = Modifier
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("关闭") }
        },
    )
}

@Composable
private fun TaskNavigationDrawer(
    filter: TaskViewFilter,
    selectedTag: String?,
    availableTags: List<TaskDrawerTag>,
    filterCounts: Map<TaskViewFilter, Int>,
    settings: TaskSettings,
    hasSearchQuery: Boolean,
    onFilterChanged: (TaskViewFilter) -> Unit,
    onTagChanged: (String?) -> Unit,
    onClearSearch: () -> Unit,
    onCreateList: () -> Unit,
    onRefresh: () -> Unit,
    onSettingsClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.28f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss,
                    )
            )
            AnimatedVisibility(
                visible = true,
                modifier = Modifier.align(Alignment.CenterEnd),
                enter = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(220)) + fadeIn(tween(160)),
                exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(180)) + fadeOut(tween(120)),
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.82f)
                        .widthIn(max = 340.dp)
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                    color = SaltTheme.colors.background,
                    shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item {
                            Text(
                                text = "任务",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaltTheme.colors.text,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                            )
                        }
                        item {
                            TaskDrawerList(
                                filter = filter,
                                selectedTag = selectedTag,
                                availableTags = availableTags,
                                filterCounts = filterCounts,
                                settings = settings,
                                onFilterChanged = onFilterChanged,
                                onTagChanged = onTagChanged,
                                onCreateList = onCreateList,
                                showActions = false,
                            )
                        }
                        item { TaskDrawerSection("操作") }
                        item { TaskDrawerRow("新建清单", onClick = onCreateList) }
                        item { TaskDrawerRow("清除搜索", enabled = hasSearchQuery, onClick = onClearSearch) }
                        item { TaskDrawerRow("刷新", onClick = onRefresh) }
                        item { TaskDrawerRow("任务设置", onClick = onSettingsClick) }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskDrawerList(
    filter: TaskViewFilter,
    selectedTag: String?,
    availableTags: List<TaskDrawerTag>,
    filterCounts: Map<TaskViewFilter, Int>,
    settings: TaskSettings,
    onFilterChanged: (TaskViewFilter) -> Unit,
    onTagChanged: (String?) -> Unit,
    onCreateList: () -> Unit,
    showActions: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        TaskDrawerSection("\u667a\u80fd\u6e05\u5355")
        TaskDrawerRow("\u6211\u7684\u4efb\u52a1", filterCounts[TaskViewFilter.ALL], filter == TaskViewFilter.ALL && selectedTag == null) { onFilterChanged(TaskViewFilter.ALL) }
        TaskDrawerRow("\u4eca\u5929", filterCounts[TaskViewFilter.TODAY], filter == TaskViewFilter.TODAY) { onFilterChanged(TaskViewFilter.TODAY) }
        TaskDrawerRow("\u903e\u671f", filterCounts[TaskViewFilter.OVERDUE], filter == TaskViewFilter.OVERDUE) { onFilterChanged(TaskViewFilter.OVERDUE) }
        TaskDrawerRow("\u4ee5\u540e", filterCounts[TaskViewFilter.UPCOMING], filter == TaskViewFilter.UPCOMING) { onFilterChanged(TaskViewFilter.UPCOMING) }
        if (settings.drawerShowDueFilters) {
            TaskDrawerRow("\u6709\u622a\u6b62\u65e5\u671f", filterCounts[TaskViewFilter.WITH_DUE_DATE], filter == TaskViewFilter.WITH_DUE_DATE) { onFilterChanged(TaskViewFilter.WITH_DUE_DATE) }
            TaskDrawerRow("\u65e0\u622a\u6b62\u65e5\u671f", filterCounts[TaskViewFilter.WITHOUT_DUE_DATE], filter == TaskViewFilter.WITHOUT_DUE_DATE) { onFilterChanged(TaskViewFilter.WITHOUT_DUE_DATE) }
        }
        TaskDrawerRow("\u5df2\u5b8c\u6210", filterCounts[TaskViewFilter.COMPLETED], filter == TaskViewFilter.COMPLETED) { onFilterChanged(TaskViewFilter.COMPLETED) }

        val drawerTags = availableTags
            .filter { !settings.drawerHideEmptyTags || it.count > 0 }
        if (drawerTags.isNotEmpty()) {
            TaskDrawerSection("\u6e05\u5355")
            TaskDrawerRow("\u5168\u90e8\u6e05\u5355", selected = selectedTag == null) { onTagChanged(null) }
            drawerTags.forEach { tag ->
                TaskDrawerRow(
                    text = tag.name,
                    count = tag.count,
                    selected = selectedTag.equals(tag.name, ignoreCase = true),
                    accentColor = tag.color?.takeIf { it != 0 }?.let { Color(it) },
                    iconLabel = tag.icon,
                    onClick = { onTagChanged(tag.name) },
                )
            }
        }
        if (showActions) {
            TaskDrawerSection("\u64cd\u4f5c")
            TaskDrawerRow("\u65b0\u5efa\u6e05\u5355", onClick = onCreateList)
        }
    }
}
@Composable
private fun TaskDrawerSection(text: String) {
    Text(
        text = text,
        color = SaltTheme.colors.subText,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
    )
}

@Composable
private fun TaskDrawerRow(
    text: String,
    count: Int? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    accentColor: Color? = null,
    iconLabel: String? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) SaltTheme.colors.highlight.copy(alpha = 0.14f) else Color.Transparent)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (accentColor != null || !iconLabel.isNullOrBlank()) {
            TaskDrawerAppearanceMark(accentColor, iconLabel, selected)
            Spacer(modifier = Modifier.width(10.dp))
        }
        Text(
            text = text,
            color = when {
                !enabled -> SaltTheme.colors.subText.copy(alpha = 0.45f)
                selected -> SaltTheme.colors.highlight
                else -> SaltTheme.colors.text
            },
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (count != null) {
            Text(
                text = count.toString(),
                color = if (selected) SaltTheme.colors.highlight else SaltTheme.colors.subText,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
        if (selected) {
            Icon(Icons.Rounded.Check, contentDescription = null, tint = SaltTheme.colors.highlight, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun TaskDrawerAppearanceMark(color: Color?, icon: String?, selected: Boolean) {
    val accent = if (selected) SaltTheme.colors.highlight else color ?: SaltTheme.colors.subText
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(accent.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center,
    ) {
        val label = taskListIconLabel(icon)
        if (label.isNotBlank()) {
            Text(text = label, color = accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        } else {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(accent),
            )
        }
    }
}

private fun taskListIconLabel(icon: String?): String =
    when (icon?.trim()?.lowercase()) {
        "list" -> "列"
        "label" -> "签"
        "flag" -> "旗"
        "star" -> "星"
        "work" -> "工"
        "home" -> "家"
        else -> ""
    }
