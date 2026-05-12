package org.tasks.notifications

/**
 * Stub interface - actual implementation provided by app module.
 */
interface Notifier {
    suspend fun cancel(taskId: Long, reason: CancelReason)
    suspend fun cancel(taskIds: List<Long>, reason: CancelReason)
    suspend fun triggerNotifications()
    suspend fun updateTimerNotification()
}
