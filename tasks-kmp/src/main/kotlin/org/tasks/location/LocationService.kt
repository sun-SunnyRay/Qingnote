package org.tasks.location

/**
 * Stub interface - actual implementation provided by app module.
 */
interface LocationService {
    suspend fun updateGeofences(taskId: Long)
}
