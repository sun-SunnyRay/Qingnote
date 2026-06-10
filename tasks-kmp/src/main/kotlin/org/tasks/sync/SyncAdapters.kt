package org.tasks.sync

import org.tasks.data.entity.Task

/**
 * Stub interface - actual implementation provided by app module.
 */
interface SyncAdapters {
    suspend fun sync(task: Task, original: Task?)
    suspend fun sync(source: SyncSource)
}
