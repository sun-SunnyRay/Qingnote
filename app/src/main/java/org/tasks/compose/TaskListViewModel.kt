package org.tasks.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.tasks.data.dao.TaskDao
import org.tasks.data.entity.Task
import org.tasks.service.TaskCompleter
import org.tasks.service.TaskDeleter
import org.tasks.data.TaskSaver
import org.tasks.time.DateTimeUtils2.currentTimeMillis
import javax.inject.Inject

data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val editingTask: Task? = null,
    val showCreateDialog: Boolean = false,
)

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val taskCompleter: TaskCompleter,
    private val taskDeleter: TaskDeleter,
    private val taskSaver: TaskSaver,
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
                val tasks = taskDao.getActiveTasks()
                    .sortedByDescending { it.modificationDate }
                _uiState.value = _uiState.value.copy(tasks = tasks, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun createTask(title: String, priority: Int = Task.Priority.NONE, dueDate: Long = 0L) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val task = Task(
                title = title,
                priority = priority,
                dueDate = dueDate,
                creationDate = currentTimeMillis(),
                modificationDate = currentTimeMillis(),
            )
            taskDao.createNew(task)
            _uiState.value = _uiState.value.copy(showCreateDialog = false)
            loadTasks()
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            taskCompleter.setComplete(task, !task.isCompleted)
            loadTasks()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDeleter.markDeleted(task)
            loadTasks()
        }
    }

    fun startEditing(task: Task) {
        _uiState.value = _uiState.value.copy(editingTask = task)
    }

    fun stopEditing() {
        _uiState.value = _uiState.value.copy(editingTask = null)
    }

    fun saveTask(task: Task, newTitle: String, newPriority: Int, newDueDate: Long) {
        viewModelScope.launch {
            val updated = task.copy(
                title = newTitle,
                priority = newPriority,
                dueDate = newDueDate,
            )
            taskSaver.save(updated, task)
            _uiState.value = _uiState.value.copy(editingTask = null)
            loadTasks()
        }
    }

    fun showCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = true)
    }

    fun hideCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }
}
