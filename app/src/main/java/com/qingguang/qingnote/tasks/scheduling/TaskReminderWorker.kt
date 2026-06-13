package com.qingguang.qingnote.tasks.scheduling

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.todoroo.astrid.alarms.AlarmService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.tasks.data.dao.TaskDao
import java.util.concurrent.TimeUnit

@HiltWorker
class TaskReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val alarmService: AlarmService,
    private val taskDao: TaskDao,
    private val alarmScheduler: TaskAlarmScheduler,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 触发过期闹钟并弹通知
            alarmService.triggerAlarms { notifications ->
                notifications.forEach { notification ->
                    val task = taskDao.fetch(notification.taskId) ?: return@forEach
                    if (!task.isCompleted && !task.isDeleted) {
                        alarmScheduler.showNotification(task)
                    }
                }
            }
            // 调度下一次精确闹钟
            alarmScheduler.scheduleNextAlarm()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "task_reminder_periodic"

        fun enqueue(context: Context) {
            val request = PeriodicWorkRequestBuilder<TaskReminderWorker>(
                15, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
