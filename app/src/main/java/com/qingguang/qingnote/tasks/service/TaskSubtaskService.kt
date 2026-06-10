package com.qingguang.qingnote.tasks.service

import com.qingguang.qingnote.tasks.TaskSubtaskDraft
import org.tasks.data.TaskSaver
import org.tasks.data.dao.TaskDao
import org.tasks.data.entity.Task
import org.tasks.service.TaskCompleter
import org.tasks.service.TaskDeleter
import org.tasks.time.DateTimeUtils2.currentTimeMillis
import javax.inject.Inject

class TaskSubtaskService @Inject constructor(
    private val taskDao: TaskDao,
    private val taskSaver: TaskSaver,
    private val taskCompleter: TaskCompleter,
    private val taskDeleter: TaskDeleter,
) {
    suspend fun getSubtasks(parent: Task): List<Task> =
        taskDao.fetch(taskDao.getChildren(parent.id))
            .filter { !it.isDeleted && it.parent == parent.id }
            .sortedWith(compareBy<Task> { it.order ?: Long.MAX_VALUE }.thenBy { it.creationDate })

    suspend fun syncSubtasks(
        parent: Task,
        drafts: List<TaskSubtaskDraft>,
    ) {
        val existing = getSubtasks(parent)
        val now = currentTimeMillis()
        val cleanedDrafts = drafts
            .map { it.copy(title = it.title.trim()) }
            .filter { it.title.isNotBlank() }
        val draftIds = cleanedDrafts.mapNotNull { it.id }.toSet()

        existing
            .filter { it.id !in draftIds }
            .forEach { taskDeleter.markDeleted(it) }

        cleanedDrafts.forEachIndexed { index, draft ->
            if (draft.id != null) {
                val original = existing.firstOrNull { it.id == draft.id } ?: taskDao.fetch(draft.id)
                if (original != null) {
                    val updatedTitle = original.copy(
                        title = draft.title,
                        parent = parent.id,
                        order = index.toLong(),
                    )
                    taskSaver.save(updatedTitle, original)
                    if (original.isCompleted != draft.completed) {
                        taskCompleter.setComplete(updatedTitle, draft.completed, includeChildren = false)
                    }
                }
            } else {
                taskDao.createNew(
                    Task(
                        title = draft.title,
                        parent = parent.id,
                        order = index.toLong(),
                        completionDate = if (draft.completed) now else 0L,
                        creationDate = now,
                        modificationDate = now,
                    )
                )
            }
        }
    }
}
