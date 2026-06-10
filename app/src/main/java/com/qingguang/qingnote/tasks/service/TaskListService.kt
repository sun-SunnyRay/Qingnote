package com.qingguang.qingnote.tasks.service

import com.qingguang.qingnote.tasks.TaskDrawerTag
import org.tasks.data.NO_ORDER
import org.tasks.data.dao.TagDao
import org.tasks.data.dao.TagDataDao
import org.tasks.data.dao.TaskDao
import org.tasks.data.entity.TagData
import org.tasks.data.entity.Task
import java.util.Locale
import javax.inject.Inject

class TaskListService @Inject constructor(
    private val taskDao: TaskDao,
    private val tagDao: TagDao,
    private val tagDataDao: TagDataDao,
) {
    suspend fun getAvailableTags(): List<TaskDrawerTag> {
        val taskTags = taskDao.getAll()
            .filter { !it.isDeleted && it.parent == 0L }
            .flatMap { task ->
                tagDataDao.getTagDataForTask(task.id)
                    .mapNotNull { it.name?.trim() }
                    .filter { it.isNotBlank() }
                    .distinctBy { it.lowercase(Locale.US) }
            }
            .groupingBy { it.lowercase(Locale.US) }
            .eachCount()

        return tagDataDao.tagDataOrderedByName()
            .mapNotNull { tagData ->
                val name = tagData.name?.trim()?.takeIf { it.isNotBlank() }
                    ?: return@mapNotNull null
                TaskDrawerTag(
                    name = name,
                    count = taskTags[name.lowercase(Locale.US)] ?: 0,
                    color = tagData.color,
                    icon = tagData.icon?.takeIf { it.isNotBlank() },
                    order = tagData.order,
                )
            }
            .distinctBy { it.name.lowercase(Locale.US) }
            .sortedWith(
                compareBy<TaskDrawerTag> { if (it.order == NO_ORDER) Int.MAX_VALUE else it.order }
                    .thenBy { it.name.lowercase(Locale.US) }
            )
    }

    suspend fun createList(name: String, color: Int? = null, icon: String? = null): String? {
        val cleaned = name.trim()
        if (cleaned.isBlank()) return null
        val existing = tagDataDao.getTagByName(cleaned)
        if (existing != null) return existing.name?.trim()?.ifBlank { cleaned } ?: cleaned
        tagDataDao.insert(
            TagData(
                name = cleaned,
                color = color?.takeIf { it != 0 },
                icon = icon?.trim()?.takeIf { it.isNotBlank() },
            )
        )
        return cleaned
    }

    suspend fun renameList(
        oldName: String,
        newName: String,
        color: Int? = null,
        icon: String? = null,
    ): String? {
        val cleaned = newName.trim()
        if (oldName.isBlank() || cleaned.isBlank()) return null
        val tag = tagDataDao.getTagByName(oldName) ?: return null
        val existing = tagDataDao.getTagByName(cleaned)
        if (existing != null && !existing.name.equals(oldName, ignoreCase = true)) return existing.name
        tagDataDao.update(
            tag.copy(
                name = cleaned,
                color = color?.takeIf { it != 0 },
                icon = icon?.trim()?.takeIf { it.isNotBlank() },
            )
        )
        return cleaned
    }

    suspend fun deleteList(name: String) {
        val tag = tagDataDao.getTagByName(name) ?: return
        tagDataDao.delete(tag)
    }

    suspend fun moveList(name: String, up: Boolean) {
        val ordered = tagDataDao.tagDataOrderedByName()
            .filter { !it.name.isNullOrBlank() }
            .sortedWith(
                compareBy<TagData> { if (it.order == NO_ORDER) Int.MAX_VALUE else it.order }
                    .thenBy { it.name.orEmpty().lowercase(Locale.US) }
            )
            .toMutableList()
        val index = ordered.indexOfFirst { it.name.equals(name, ignoreCase = true) }
        if (index < 0) return
        val target = if (up) index - 1 else index + 1
        if (target !in ordered.indices) return
        val moved = ordered.removeAt(index)
        ordered.add(target, moved)
        ordered.forEachIndexed { order, tag ->
            tag.id?.let { tagDataDao.setOrder(it, order) }
        }
    }

    suspend fun moveTasksToList(
        tasks: List<Task>,
        sourceListName: String?,
        destinationListName: String?,
    ) {
        val source = sourceListName?.trim()?.takeIf { it.isNotBlank() }
        val destination = destinationListName?.trim()?.takeIf { it.isNotBlank() }
        if (tasks.isEmpty() || (source == null && destination == null)) return
        destination?.let { createList(it) }
        tasks.forEach { task ->
            val currentTags = getTagNames(task.id)
            val movedTags = currentTags
                .filterNot { tag -> source != null && tag.equals(source, ignoreCase = true) }
                .let { tags ->
                    if (destination != null && tags.none { it.equals(destination, ignoreCase = true) }) {
                        tags + destination
                    } else {
                        tags
                    }
                }
            syncTags(task, movedTags)
        }
    }

    suspend fun updateTasksTags(tasks: List<Task>, names: List<String>, replace: Boolean) {
        val cleaned = names
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase(Locale.US) }
        if (tasks.isEmpty() || cleaned.isEmpty()) return
        tasks.forEach { task ->
            val currentTags = if (replace) emptyList() else getTagNames(task.id)
            syncTags(task, currentTags + cleaned)
        }
    }

    suspend fun getTagNames(taskId: Long): List<String> =
        tagDataDao.getTagDataForTask(taskId)
            .mapNotNull { it.name?.trim() }
            .filter { it.isNotBlank() }

    suspend fun taskHasTag(taskId: Long, tag: String?): Boolean =
        tag.isNullOrBlank() ||
                tagDataDao.getTagDataForTask(taskId).any { it.name.equals(tag, ignoreCase = true) }

    suspend fun syncTags(task: Task, names: List<String>) {
        val tags = names
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase(Locale.US) }
            .map { name ->
                tagDataDao.getTagByName(name)
                    ?: TagData(name = name).also { tagDataDao.insert(it) }
            }
        tagDao.applyTags(task, tagDataDao, tags)
    }
}
