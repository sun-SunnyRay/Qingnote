package com.qingguang.qingnote.tasks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.qingguang.qingnote.tasks.TaskEditData
import com.qingguang.qingnote.tasks.TaskEditExtras
import com.qingguang.qingnote.tasks.TaskDrawerTag
import com.qingguang.qingnote.tasks.TaskListExtras
import com.qingguang.qingnote.tasks.TaskSortMode
import com.qingguang.qingnote.tasks.TaskSortOrder
import com.qingguang.qingnote.tasks.TaskViewFilter
import com.qingguang.qingnote.tasks.TasksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.tasks.data.entity.Task
import javax.inject.Inject

data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val extras: Map<Long, TaskListExtras> = emptyMap(),
    val isLoading: Boolean = true,
    val editingTask: Task? = null,
    val editingExtras: TaskEditExtras = TaskEditExtras(),
    val showCreateDialog: Boolean = false,
    val searchQuery: String = "",
    val filter: TaskViewFilter = TaskViewFilter.ALL,
    val selectedTag: String? = null,
    val availableTags: List<TaskDrawerTag> = emptyList(),
    val filterCounts: Map<TaskViewFilter, Int> = emptyMap(),
    val sortOrder: TaskSortOrder = TaskSortOrder.MODIFIED_DESC,
    val groupMode: TaskSortMode = TaskSortMode.DUE_DATE,
    val groupAscending: Boolean = true,
    val sortMode: TaskSortMode = TaskSortMode.MODIFIED,
    val sortAscending: Boolean = false,
    val subtaskMode: TaskSortMode = TaskSortMode.MY_ORDER,
    val subtaskAscending: Boolean = true,
    val completedAtBottom: Boolean = true,
    val completedMode: TaskSortMode = TaskSortMode.COMPLETED,
    val completedAscending: Boolean = false,
    val showCompleted: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val state = _uiState.value
                val tasks = tasksRepository.getTasks(
                    query = state.searchQuery,
                    filter = state.filter,
                    sortOrder = state.sortOrder,
                    tagFilter = state.selectedTag,
                    showCompleted = state.showCompleted,
                )
                _uiState.value = _uiState.value.copy(
                    tasks = tasks,
                    extras = tasksRepository.getListExtras(tasks),
                    availableTags = tasksRepository.getAvailableTags(),
                    filterCounts = tasksRepository.getFilterCounts(state.selectedTag),
                    isLoading = false,
                    errorMessage = null,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "任务加载失败",
                )
            }
        }
    }

    fun createTask(data: TaskEditData) {
        if (data.title.isBlank()) return
        viewModelScope.launch {
            tasksRepository.createTask(data)
            _uiState.value = _uiState.value.copy(showCreateDialog = false)
            loadTasks()
        }
    }

    fun completeTask(task: Task) {
        setTaskCompleted(task, !task.isCompleted)
    }

    fun setTaskCompleted(task: Task, completed: Boolean) {
        viewModelScope.launch {
            tasksRepository.setComplete(task, completed)
            loadTasks()
        }
    }

    fun setTasksCompleted(tasks: List<Task>, completed: Boolean, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            tasks.forEach { tasksRepository.setComplete(it, completed) }
            loadTasks()
            onDone()
        }
    }

    fun restoreTaskCompletionStates(states: List<Pair<Task, Boolean>>) {
        viewModelScope.launch {
            states.forEach { (task, completed) ->
                tasksRepository.setComplete(task, completed)
            }
            loadTasks()
        }
    }

    fun deleteTask(task: Task, onDeleted: (List<Task>) -> Unit = {}) {
        viewModelScope.launch {
            val deletedTasks = tasksRepository.deleteTask(task)
            loadTasks()
            onDeleted(deletedTasks)
        }
    }

    fun deleteTasks(tasks: List<Task>, onDeleted: (List<Task>) -> Unit = {}) {
        viewModelScope.launch {
            val deletedTasks = tasks.flatMap { tasksRepository.deleteTask(it) }
                .distinctBy { it.id }
            loadTasks()
            onDeleted(deletedTasks)
        }
    }

    fun restoreTasks(tasks: List<Task>) {
        viewModelScope.launch {
            tasksRepository.restoreTasks(tasks)
            loadTasks()
        }
    }

    fun startEditing(task: Task) {
        _uiState.value = _uiState.value.copy(editingTask = task, editingExtras = TaskEditExtras())
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                editingExtras = tasksRepository.getEditExtras(task)
            )
        }
    }

    fun stopEditing() {
        _uiState.value = _uiState.value.copy(editingTask = null, editingExtras = TaskEditExtras())
    }

    fun saveTask(task: Task, data: TaskEditData) {
        viewModelScope.launch {
            tasksRepository.saveTask(task, data)
            _uiState.value = _uiState.value.copy(editingTask = null, editingExtras = TaskEditExtras())
            loadTasks()
        }
    }

    fun showCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = true)
    }

    fun hideCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadTasks()
    }

    fun setFilter(filter: TaskViewFilter) {
        _uiState.value = _uiState.value.copy(filter = filter, selectedTag = null)
        loadTasks()
    }

    fun setTagFilter(tag: String?) {
        _uiState.value = _uiState.value.copy(filter = TaskViewFilter.ALL, selectedTag = tag)
        loadTasks()
    }

    fun createList(name: String, color: Int? = null, icon: String? = null) {
        viewModelScope.launch {
            val createdName = tasksRepository.createList(name, color, icon)
            if (createdName != null) {
                _uiState.value = _uiState.value.copy(filter = TaskViewFilter.ALL, selectedTag = createdName)
            }
            loadTasks()
        }
    }

    fun renameSelectedList(name: String, color: Int? = null, icon: String? = null) {
        val oldName = _uiState.value.selectedTag ?: return
        viewModelScope.launch {
            val renamed = tasksRepository.renameList(oldName, name, color, icon)
            if (renamed != null) {
                _uiState.value = _uiState.value.copy(filter = TaskViewFilter.ALL, selectedTag = renamed)
            }
            loadTasks()
        }
    }

    fun deleteSelectedList() {
        val name = _uiState.value.selectedTag ?: return
        viewModelScope.launch {
            tasksRepository.deleteList(name)
            _uiState.value = _uiState.value.copy(filter = TaskViewFilter.ALL, selectedTag = null)
            loadTasks()
        }
    }

    fun moveSelectedList(up: Boolean) {
        val name = _uiState.value.selectedTag ?: return
        viewModelScope.launch {
            tasksRepository.moveList(name, up)
            loadTasks()
        }
    }

    fun moveTasksToList(tasks: List<Task>, destinationListName: String?, onMoved: () -> Unit = {}) {
        val sourceListName = _uiState.value.selectedTag
        viewModelScope.launch {
            tasksRepository.moveTasksToList(tasks, sourceListName, destinationListName)
            loadTasks()
            onMoved()
        }
    }

    fun updateTasksPriority(tasks: List<Task>, priority: Int, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            tasksRepository.updateTasksPriority(tasks, priority)
            loadTasks()
            onDone()
        }
    }

    fun updateTasksStartDate(tasks: List<Task>, startDate: Long, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            tasksRepository.updateTasksStartDate(tasks, startDate)
            loadTasks()
            onDone()
        }
    }

    fun updateTasksDueDate(tasks: List<Task>, dueDate: Long, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            tasksRepository.updateTasksDueDate(tasks, dueDate)
            loadTasks()
            onDone()
        }
    }

    fun updateTasksTags(
        tasks: List<Task>,
        names: List<String>,
        replace: Boolean,
        onDone: () -> Unit = {},
    ) {
        viewModelScope.launch {
            tasksRepository.updateTasksTags(tasks, names, replace)
            loadTasks()
            onDone()
        }
    }

    fun setSortOrder(sortOrder: TaskSortOrder) {
        _uiState.value = _uiState.value.copy(
            sortOrder = sortOrder,
            sortMode = sortOrder.toSortMode(),
            sortAscending = sortOrder.isAscending(),
        )
        loadTasks()
    }

    fun setGroupMode(groupMode: TaskSortMode) {
        _uiState.value = _uiState.value.copy(
            groupMode = groupMode,
            groupAscending = groupMode.defaultAscending(),
        )
    }

    fun setGroupAscending(ascending: Boolean) {
        _uiState.value = _uiState.value.copy(groupAscending = ascending)
    }

    fun setSortMode(sortMode: TaskSortMode) {
        val ascending = sortMode.defaultAscending()
        _uiState.value = _uiState.value.copy(
            sortMode = sortMode,
            sortAscending = ascending,
            sortOrder = sortMode.toSortOrder(ascending),
        )
        loadTasks()
    }

    fun setSortAscending(ascending: Boolean) {
        val state = _uiState.value
        _uiState.value = state.copy(
            sortAscending = ascending,
            sortOrder = state.sortMode.toSortOrder(ascending),
        )
        loadTasks()
    }

    fun setSubtaskMode(subtaskMode: TaskSortMode) {
        _uiState.value = _uiState.value.copy(
            subtaskMode = subtaskMode,
            subtaskAscending = subtaskMode.defaultAscending(),
        )
    }

    fun setSubtaskAscending(ascending: Boolean) {
        _uiState.value = _uiState.value.copy(subtaskAscending = ascending)
    }

    fun setCompletedAtBottom(completedAtBottom: Boolean) {
        _uiState.value = _uiState.value.copy(completedAtBottom = completedAtBottom)
    }

    fun setCompletedMode(completedMode: TaskSortMode) {
        _uiState.value = _uiState.value.copy(
            completedMode = completedMode,
            completedAscending = completedMode.defaultAscending(),
        )
    }

    fun setCompletedAscending(ascending: Boolean) {
        _uiState.value = _uiState.value.copy(completedAscending = ascending)
    }

    fun setShowCompleted(showCompleted: Boolean) {
        _uiState.value = _uiState.value.copy(showCompleted = showCompleted)
        loadTasks()
    }

}

private fun TaskSortMode.defaultAscending(): Boolean =
    when (this) {
        TaskSortMode.MODIFIED,
        TaskSortMode.CREATED,
        TaskSortMode.COMPLETED -> false
        else -> true
    }

private fun TaskSortMode.toSortOrder(ascending: Boolean): TaskSortOrder =
    when (this) {
        TaskSortMode.DUE_DATE -> if (ascending) TaskSortOrder.DUE_DATE_ASC else TaskSortOrder.DUE_DATE_DESC
        TaskSortMode.START_DATE -> if (ascending) TaskSortOrder.START_DATE_ASC else TaskSortOrder.START_DATE_DESC
        TaskSortMode.PRIORITY -> if (ascending) TaskSortOrder.PRIORITY_ASC else TaskSortOrder.PRIORITY_DESC
        TaskSortMode.TITLE -> if (ascending) TaskSortOrder.TITLE_ASC else TaskSortOrder.TITLE_DESC
        TaskSortMode.MODIFIED -> if (ascending) TaskSortOrder.MODIFIED_ASC else TaskSortOrder.MODIFIED_DESC
        TaskSortMode.CREATED -> if (ascending) TaskSortOrder.CREATED_ASC else TaskSortOrder.CREATED_DESC
        TaskSortMode.LIST -> if (ascending) TaskSortOrder.LIST_ASC else TaskSortOrder.LIST_DESC
        TaskSortMode.COMPLETED -> if (ascending) TaskSortOrder.COMPLETED_ASC else TaskSortOrder.COMPLETED_DESC
        TaskSortMode.MY_ORDER -> if (ascending) TaskSortOrder.MY_ORDER_ASC else TaskSortOrder.MY_ORDER_DESC
        TaskSortMode.AUTO -> TaskSortOrder.AUTO
        TaskSortMode.NONE -> TaskSortOrder.MODIFIED_DESC
    }

private fun TaskSortOrder.toSortMode(): TaskSortMode =
    when (this) {
        TaskSortOrder.DUE_DATE_ASC,
        TaskSortOrder.DUE_DATE_DESC -> TaskSortMode.DUE_DATE
        TaskSortOrder.START_DATE_ASC,
        TaskSortOrder.START_DATE_DESC -> TaskSortMode.START_DATE
        TaskSortOrder.PRIORITY_ASC,
        TaskSortOrder.PRIORITY_DESC -> TaskSortMode.PRIORITY
        TaskSortOrder.TITLE_ASC,
        TaskSortOrder.TITLE_DESC -> TaskSortMode.TITLE
        TaskSortOrder.MODIFIED_ASC,
        TaskSortOrder.MODIFIED_DESC -> TaskSortMode.MODIFIED
        TaskSortOrder.CREATED_ASC,
        TaskSortOrder.CREATED_DESC -> TaskSortMode.CREATED
        TaskSortOrder.LIST_ASC,
        TaskSortOrder.LIST_DESC -> TaskSortMode.LIST
        TaskSortOrder.COMPLETED_ASC,
        TaskSortOrder.COMPLETED_DESC -> TaskSortMode.COMPLETED
        TaskSortOrder.MY_ORDER_ASC,
        TaskSortOrder.MY_ORDER_DESC -> TaskSortMode.MY_ORDER
        TaskSortOrder.AUTO -> TaskSortMode.AUTO
    }

private fun TaskSortOrder.isAscending(): Boolean =
    when (this) {
        TaskSortOrder.MODIFIED_ASC,
        TaskSortOrder.CREATED_ASC,
        TaskSortOrder.DUE_DATE_ASC,
        TaskSortOrder.START_DATE_ASC,
        TaskSortOrder.PRIORITY_ASC,
        TaskSortOrder.TITLE_ASC,
        TaskSortOrder.COMPLETED_ASC,
        TaskSortOrder.MY_ORDER_ASC,
        TaskSortOrder.LIST_ASC,
        TaskSortOrder.AUTO -> true
        else -> false
    }
