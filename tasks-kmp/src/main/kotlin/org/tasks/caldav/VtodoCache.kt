package org.tasks.caldav

import org.tasks.data.entity.CaldavAccount
import org.tasks.data.entity.CaldavCalendar
import org.tasks.data.entity.CaldavTask

/**
 * Stub interface - actual implementation provided by app module.
 */
interface VtodoCache {
    suspend fun delete(list: CaldavCalendar)
    suspend fun delete(account: CaldavAccount)
    suspend fun move(from: CaldavCalendar, to: CaldavCalendar, task: CaldavTask)
}
