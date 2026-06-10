package com.qingguang.qingnote.tasks.service

import com.qingguang.qingnote.tasks.TaskReminderDraft
import com.todoroo.astrid.alarms.AlarmService
import org.tasks.data.entity.Alarm
import javax.inject.Inject

class TaskReminderService @Inject constructor(
    private val alarmService: AlarmService,
) {
    suspend fun getReminders(taskId: Long): List<TaskReminderDraft> =
        alarmService.getAlarms(taskId)
            .filter { it.type != Alarm.TYPE_SNOOZE }
            .map {
                TaskReminderDraft(
                    id = it.id,
                    time = it.time,
                    type = it.type,
                    repeat = it.repeat,
                    interval = it.interval,
                )
            }

    suspend fun getReminderCount(taskId: Long): Int =
        alarmService.getAlarms(taskId).count { it.type != Alarm.TYPE_SNOOZE }

    suspend fun syncReminders(taskId: Long, reminders: List<TaskReminderDraft>) {
        val alarms = reminders
            .map {
                if (it.repeat > 0 && it.interval > 0L) {
                    it
                } else {
                    it.copy(repeat = 0, interval = 0L)
                }
            }
            .filter {
                when (it.type) {
                    Alarm.TYPE_DATE_TIME,
                    Alarm.TYPE_RANDOM -> it.time > 0
                    Alarm.TYPE_REL_START,
                    Alarm.TYPE_REL_END -> true
                    else -> false
                }
            }
            .distinctBy { listOf(it.type, it.time, it.repeat, it.interval) }
            .map {
                Alarm(
                    task = taskId,
                    time = it.time,
                    type = it.type,
                    repeat = it.repeat,
                    interval = it.interval,
                )
            }
            .toMutableSet()
        alarmService.synchronizeAlarms(taskId, alarms)
    }
}
