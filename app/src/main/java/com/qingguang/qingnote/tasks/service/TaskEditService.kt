package com.qingguang.qingnote.tasks.service

import com.qingguang.qingnote.tasks.TaskCalendarWriter
import com.qingguang.qingnote.tasks.TaskEditData
import org.tasks.data.TaskSaver
import org.tasks.data.dao.TaskDao
import org.tasks.data.entity.Task
import org.tasks.service.TaskCompleter
import org.tasks.service.TaskDeleter
import org.tasks.time.DateTimeUtils2.currentTimeMillis
import javax.inject.Inject

class TaskEditService @Inject constructor(
    private val taskDao: TaskDao,
    private val taskCompleter: TaskCompleter,
    private val taskDeleter: TaskDeleter,
    private val taskSaver: TaskSaver,
    private val calendarWriter: TaskCalendarWriter,
    private val listService: TaskListService,
    private val subtaskService: TaskSubtaskService,
    private val attachmentService: TaskAttachmentService,
    private val reminderService: TaskReminderService,
) {
    suspend fun createTask(data: TaskEditData) {
        val now = currentTimeMillis()
        val task = Task(
            title = data.title,
            notes = data.notes,
            priority = data.priority,
            dueDate = data.dueDate,
            hideUntil = data.startDate,
            recurrence = data.recurrence,
            repeatFrom = data.repeatFrom,
            timerStart = 0L,
            calendarURI = null,
            creationDate = now,
            modificationDate = now,
        )
        taskDao.createNew(task)
        reminderService.syncReminders(task.id, data.reminders.orEmpty())
        data.tagNames?.let { listService.syncTags(task, it) }
        subtaskService.syncSubtasks(task, data.subtasks.orEmpty())
        attachmentService.syncAttachments(task.id, data.attachments.orEmpty())
        val calendarUri = calendarWriter.sync(
            task = task,
            addToCalendar = data.addToCalendar,
            startDate = data.startDate,
            dueDate = data.dueDate,
            endAtDueTime = data.calendarEndAtDueTime,
        )
        if (calendarUri != task.calendarURI) {
            taskSaver.save(task.copy(calendarURI = calendarUri), task)
        } else {
            taskSaver.afterSave(task, null)
        }
    }

    suspend fun saveTask(task: Task, data: TaskEditData) {
        val now = currentTimeMillis()
        val updatedWithoutCalendar = task.copy(
            title = data.title,
            notes = data.notes,
            priority = data.priority,
            dueDate = data.dueDate,
            hideUntil = data.startDate,
            recurrence = data.recurrence,
            repeatFrom = data.repeatFrom,
            timerStart = task.timerStart,
        )
        val calendarUri = calendarWriter.sync(
            task = updatedWithoutCalendar,
            addToCalendar = data.addToCalendar,
            startDate = data.startDate,
            dueDate = data.dueDate,
            endAtDueTime = data.calendarEndAtDueTime,
        )
        val updated = updatedWithoutCalendar.copy(calendarURI = calendarUri)
        data.reminders?.let { reminderService.syncReminders(task.id, it) }
        data.tagNames?.let { listService.syncTags(task, it) }
        data.subtasks?.let { subtaskService.syncSubtasks(task, it) }
        data.attachments?.let { attachmentService.syncAttachments(task.id, it) }
        taskSaver.save(updated, task)
    }

    suspend fun setComplete(task: Task, completed: Boolean) {
        taskCompleter.setComplete(task, completed)
    }

    suspend fun deleteTask(task: Task): List<Task> =
        taskDeleter.markDeleted(task)

    suspend fun restoreTasks(tasks: List<Task>) {
        tasks.forEach { deletedTask ->
            val current = taskDao.fetch(deletedTask.id) ?: deletedTask
            taskSaver.save(
                current.copy(
                    deletionDate = 0L,
                    modificationDate = currentTimeMillis(),
                ),
                current,
            )
        }
    }

    suspend fun updateTasksPriority(tasks: List<Task>, priority: Int) {
        val now = currentTimeMillis()
        tasks.forEach { task ->
            val current = taskDao.fetch(task.id) ?: task
            taskSaver.save(
                current.copy(
                    priority = priority,
                    modificationDate = now,
                ),
                current,
            )
        }
    }

    suspend fun updateTasksStartDate(tasks: List<Task>, startDate: Long) {
        val now = currentTimeMillis()
        tasks.forEach { task ->
            val current = taskDao.fetch(task.id) ?: task
            taskSaver.save(
                current.copy(
                    hideUntil = startDate,
                    modificationDate = now,
                ),
                current,
            )
        }
    }

    suspend fun updateTasksDueDate(tasks: List<Task>, dueDate: Long) {
        val now = currentTimeMillis()
        tasks.forEach { task ->
            val current = taskDao.fetch(task.id) ?: task
            taskSaver.save(
                current.copy(
                    dueDate = dueDate,
                    modificationDate = now,
                ),
                current,
            )
        }
    }
}
