package org.tasks.service

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import org.tasks.broadcast.RefreshBroadcaster
import org.tasks.data.dao.DeletionDao
import org.tasks.data.dao.TaskDao
import org.tasks.data.db.SuspendDbUtils.chunkedMap
import org.tasks.data.entity.Task

class TaskDeleter(
    private val deletionDao: DeletionDao,
    private val taskDao: TaskDao,
    private val refreshBroadcaster: RefreshBroadcaster,
    private val taskCleanup: TaskCleanup,
) {
    suspend fun markDeleted(item: Task) = markDeleted(listOf(item.id))

    suspend fun markDeleted(taskIds: List<Long>): List<Task> = withContext(NonCancellable) {
        val ids = taskIds
            .toSet()
            .plus(taskIds.chunkedMap(taskDao::getChildren))
            .let { taskDao.fetch(it.toList()) }
            .filterNot { it.readOnly }
            .map { it.id }
        deletionDao.markDeleted(
            ids = ids,
            cleanup = { taskCleanup.cleanup(it) }
        )
        taskCleanup.onMarkedDeleted()
        refreshBroadcaster.broadcastRefresh()
        taskDao.fetch(ids)
    }
}
