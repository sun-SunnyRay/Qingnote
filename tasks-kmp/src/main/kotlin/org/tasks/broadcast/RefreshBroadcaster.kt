package org.tasks.broadcast

/**
 * Stub interface - actual implementation provided by app module.
 */
interface RefreshBroadcaster {
    suspend fun broadcastRefresh()
    suspend fun broadcastTaskCompleted(taskIds: List<Long>, oldDueDate: Long = 0)
}
