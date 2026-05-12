package org.tasks.jobs

import org.tasks.data.entity.Task

/**
 * Stub interface - actual implementation provided by app module.
 */
interface BackgroundWork {
    suspend fun updateCalendar(task: Task)
    suspend fun scheduleRefresh()
}
