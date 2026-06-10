package com.qingguang.qingnote.tasks.service

import com.qingguang.qingnote.tasks.TaskEditExtras
import com.qingguang.qingnote.tasks.TaskListExtras
import com.qingguang.qingnote.tasks.TaskSortOrder
import com.qingguang.qingnote.tasks.TaskSubtaskDraft
import com.qingguang.qingnote.tasks.TaskViewFilter
import org.tasks.data.dao.TaskDao
import org.tasks.data.entity.Task
import org.tasks.time.DateTimeUtils2.currentTimeMillis
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import javax.inject.Inject

class TaskQueryService @Inject constructor(
    private val taskDao: TaskDao,
    private val listService: TaskListService,
    private val subtaskService: TaskSubtaskService,
    private val attachmentService: TaskAttachmentService,
    private val reminderService: TaskReminderService,
) {
    suspend fun getTasks(
        query: String,
        filter: TaskViewFilter,
        sortOrder: TaskSortOrder,
        tagFilter: String?,
        showCompleted: Boolean,
    ): List<Task> {
        val allTasks = taskDao.getAll()
            .filter { !it.isDeleted && it.parent == 0L }
        val visibleTasks = if (filter == TaskViewFilter.COMPLETED) {
            allTasks.filter { it.isCompleted }
        } else if (showCompleted) {
            allTasks
        } else {
            allTasks.filterNot { it.isCompleted }
        }
        return visibleTasks
            .filter { it.matches(filter) }
            .filter { listService.taskHasTag(it.id, tagFilter) }
            .filter { it.matches(query) }
            .sortBy(sortOrder)
    }

    suspend fun getFilterCounts(tagFilter: String?): Map<TaskViewFilter, Int> {
        val allTasks = taskDao.getAll()
            .filter { !it.isDeleted && it.parent == 0L }
            .filter { listService.taskHasTag(it.id, tagFilter) }
        val activeTasks = allTasks.filterNot { it.isCompleted }
        return mapOf(
            TaskViewFilter.ALL to activeTasks.size,
            TaskViewFilter.TODAY to activeTasks.count { it.matches(TaskViewFilter.TODAY) },
            TaskViewFilter.OVERDUE to activeTasks.count { it.matches(TaskViewFilter.OVERDUE) },
            TaskViewFilter.UPCOMING to activeTasks.count { it.matches(TaskViewFilter.UPCOMING) },
            TaskViewFilter.WITH_DUE_DATE to activeTasks.count { it.matches(TaskViewFilter.WITH_DUE_DATE) },
            TaskViewFilter.WITHOUT_DUE_DATE to activeTasks.count { it.matches(TaskViewFilter.WITHOUT_DUE_DATE) },
            TaskViewFilter.COMPLETED to allTasks.count { it.isCompleted },
        )
    }

    suspend fun getListExtras(tasks: List<Task>): Map<Long, TaskListExtras> =
        tasks.associate { task ->
            val subtasks = subtaskService.getSubtasks(task)
            task.id to TaskListExtras(
                tagNames = listService.getTagNames(task.id),
                subtasks = subtasks.map {
                    TaskSubtaskDraft(
                        id = it.id,
                        title = it.title.orEmpty(),
                        completed = it.isCompleted,
                    )
                },
                subtaskCount = subtasks.size,
                completedSubtaskCount = subtasks.count { it.isCompleted },
                attachmentCount = attachmentService.getAttachmentCount(task.id),
                reminderCount = reminderService.getReminderCount(task.id),
            )
        }

    suspend fun getEditExtras(task: Task): TaskEditExtras {
        return TaskEditExtras(
            reminders = reminderService.getReminders(task.id),
            tagNames = listService.getTagNames(task.id),
            subtasks = subtaskService.getSubtasks(task).map {
                TaskSubtaskDraft(id = it.id, title = it.title.orEmpty(), completed = it.isCompleted)
            },
            attachments = attachmentService.getAttachments(task.id),
        )
    }

    private fun Task.matches(filter: TaskViewFilter): Boolean =
        when (filter) {
            TaskViewFilter.ALL -> true
            TaskViewFilter.TODAY -> dueDate in todayRange()
            TaskViewFilter.OVERDUE -> dueDate > 0 && dueDate < currentTimeMillis()
            TaskViewFilter.UPCOMING -> dueDate > todayEnd()
            TaskViewFilter.WITH_DUE_DATE -> hasDueDate()
            TaskViewFilter.WITHOUT_DUE_DATE -> !hasDueDate()
            TaskViewFilter.COMPLETED -> true
        }

    private fun Task.matches(query: String): Boolean =
        query.isBlank() ||
                title.orEmpty().contains(query, ignoreCase = true) ||
                notes.orEmpty().contains(query, ignoreCase = true)

    private suspend fun List<Task>.sortBy(sortOrder: TaskSortOrder): List<Task> =
        when (sortOrder) {
            TaskSortOrder.MODIFIED_DESC -> sortedByDescending { it.modificationDate }
            TaskSortOrder.MODIFIED_ASC -> sortedBy { it.modificationDate }
            TaskSortOrder.CREATED_DESC -> sortedByDescending { it.creationDate }
            TaskSortOrder.CREATED_ASC -> sortedBy { it.creationDate }
            TaskSortOrder.DUE_DATE_ASC -> sortedWith(
                compareBy<Task> { if (it.dueDate > 0) 0 else 1 }
                    .thenBy { it.dueDate }
                    .thenByDescending { it.modificationDate }
            )
            TaskSortOrder.DUE_DATE_DESC -> sortedWith(
                compareBy<Task> { if (it.dueDate > 0) 0 else 1 }
                    .thenByDescending { it.dueDate }
                    .thenByDescending { it.modificationDate }
            )
            TaskSortOrder.START_DATE_ASC -> sortedWith(
                compareBy<Task> { if (it.hideUntil > 0) 0 else 1 }
                    .thenBy { it.hideUntil }
                    .thenByDescending { it.modificationDate }
            )
            TaskSortOrder.START_DATE_DESC -> sortedWith(
                compareBy<Task> { if (it.hideUntil > 0) 0 else 1 }
                    .thenByDescending { it.hideUntil }
                    .thenByDescending { it.modificationDate }
            )
            TaskSortOrder.PRIORITY_DESC -> sortedWith(
                compareByDescending<Task> { it.priority }
                    .thenBy { if (it.dueDate > 0) 0 else 1 }
                    .thenBy { it.dueDate }
                    .thenByDescending { it.modificationDate }
            )
            TaskSortOrder.PRIORITY_ASC -> sortedWith(
                compareBy<Task> { it.priority }
                    .thenBy { if (it.dueDate > 0) 0 else 1 }
                    .thenBy { it.dueDate }
                    .thenByDescending { it.modificationDate }
            )
            TaskSortOrder.TITLE_ASC -> sortedWith(
                compareBy<Task> { it.title.orEmpty().lowercase(Locale.getDefault()) }
                    .thenByDescending { it.modificationDate }
            )
            TaskSortOrder.TITLE_DESC -> sortedWith(
                compareByDescending<Task> { it.title.orEmpty().lowercase(Locale.getDefault()) }
                    .thenByDescending { it.modificationDate }
            )
            TaskSortOrder.COMPLETED_DESC -> sortedWith(
                compareByDescending<Task> { it.completionDate }
                    .thenByDescending { it.modificationDate }
            )
            TaskSortOrder.COMPLETED_ASC -> sortedWith(
                compareBy<Task> { if (it.completionDate > 0) 0 else 1 }
                    .thenBy { it.completionDate }
                    .thenByDescending { it.modificationDate }
            )
            TaskSortOrder.MY_ORDER_ASC -> sortedWith(
                compareBy<Task> { it.order ?: Long.MAX_VALUE }
                    .thenByDescending { it.modificationDate }
            )
            TaskSortOrder.MY_ORDER_DESC -> sortedWith(
                compareByDescending<Task> { it.order ?: Long.MIN_VALUE }
                    .thenByDescending { it.modificationDate }
            )
            TaskSortOrder.LIST_ASC -> sortByList(ascending = true)
            TaskSortOrder.LIST_DESC -> sortByList(ascending = false)
            TaskSortOrder.AUTO -> sortedWith(
                compareByDescending<Task> { it.priority }
                    .thenBy { if (it.dueDate > 0) 0 else 1 }
                    .thenBy { it.dueDate }
                    .thenByDescending { it.modificationDate }
            )
        }

    private suspend fun List<Task>.sortByList(ascending: Boolean): List<Task> {
        val tagged = map { task ->
            task to listService.getTagNames(task.id)
                .firstOrNull()
                .orEmpty()
                .lowercase(Locale.getDefault())
        }
        val comparator = compareBy<Pair<Task, String>> { if (it.second.isBlank()) 1 else 0 }
            .thenBy { it.second }
            .thenByDescending { it.first.modificationDate }
        return tagged
            .sortedWith(if (ascending) comparator else comparator.reversed())
            .map { it.first }
    }

    private fun todayRange(): LongRange {
        val zone = ZoneId.systemDefault()
        val start = LocalDate.now(zone).atStartOfDay(zone).toInstant().toEpochMilli()
        val end = LocalDate.now(zone).plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
        return start..end
    }

    private fun todayEnd(): Long =
        LocalDate.now(ZoneId.systemDefault())
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli() - 1

    suspend fun getTasksByDueDate(start: Long, end: Long): List<Task> =
        taskDao.getTasksByDueDate(start, end)
}
