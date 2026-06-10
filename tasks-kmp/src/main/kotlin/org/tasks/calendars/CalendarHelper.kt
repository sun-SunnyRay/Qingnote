package org.tasks.calendars

import org.tasks.data.entity.Task

/**
 * Stub interface - actual implementation provided by app module.
 */
interface CalendarHelper {
    suspend fun updateEvent(task: Task)
    suspend fun rescheduleRepeatingTask(task: Task)
    suspend fun updateCalendar(task: Task)
}
