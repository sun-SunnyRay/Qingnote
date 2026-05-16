package com.qingguang.qingnote.tasks

import com.qingguang.qingnote.tasks.service.TaskEditService
import com.qingguang.qingnote.tasks.service.TaskListService
import com.qingguang.qingnote.tasks.service.TaskQueryService
import org.tasks.data.entity.Task
import javax.inject.Inject

class TasksRepository @Inject constructor(
    private val queryService: TaskQueryService,
    private val editService: TaskEditService,
    private val listService: TaskListService,
) {
    suspend fun getTasks(
        query: String,
        filter: TaskViewFilter,
        sortOrder: TaskSortOrder,
        tagFilter: String?,
        showCompleted: Boolean,
    ): List<Task> = queryService.getTasks(query, filter, sortOrder, tagFilter, showCompleted)

    suspend fun getAvailableTags(): List<TaskDrawerTag> =
        listService.getAvailableTags()

    suspend fun createList(name: String, color: Int? = null, icon: String? = null): String? =
        listService.createList(name, color, icon)

    suspend fun renameList(
        oldName: String,
        newName: String,
        color: Int? = null,
        icon: String? = null,
    ): String? = listService.renameList(oldName, newName, color, icon)

    suspend fun deleteList(name: String) =
        listService.deleteList(name)

    suspend fun moveList(name: String, up: Boolean) =
        listService.moveList(name, up)

    suspend fun moveTasksToList(
        tasks: List<Task>,
        sourceListName: String?,
        destinationListName: String?,
    ) = listService.moveTasksToList(tasks, sourceListName, destinationListName)

    suspend fun updateTasksPriority(tasks: List<Task>, priority: Int) =
        editService.updateTasksPriority(tasks, priority)

    suspend fun updateTasksStartDate(tasks: List<Task>, startDate: Long) =
        editService.updateTasksStartDate(tasks, startDate)

    suspend fun updateTasksDueDate(tasks: List<Task>, dueDate: Long) =
        editService.updateTasksDueDate(tasks, dueDate)

    suspend fun updateTasksTags(tasks: List<Task>, names: List<String>, replace: Boolean) =
        listService.updateTasksTags(tasks, names, replace)

    suspend fun getFilterCounts(tagFilter: String?): Map<TaskViewFilter, Int> =
        queryService.getFilterCounts(tagFilter)

    suspend fun getListExtras(tasks: List<Task>): Map<Long, TaskListExtras> =
        queryService.getListExtras(tasks)

    suspend fun createTask(data: TaskEditData) =
        editService.createTask(data)

    suspend fun setComplete(task: Task, completed: Boolean) =
        editService.setComplete(task, completed)

    suspend fun deleteTask(task: Task): List<Task> =
        editService.deleteTask(task)

    suspend fun restoreTasks(tasks: List<Task>) =
        editService.restoreTasks(tasks)

    suspend fun saveTask(task: Task, data: TaskEditData) =
        editService.saveTask(task, data)

    suspend fun getEditExtras(task: Task): TaskEditExtras =
        queryService.getEditExtras(task)
}
