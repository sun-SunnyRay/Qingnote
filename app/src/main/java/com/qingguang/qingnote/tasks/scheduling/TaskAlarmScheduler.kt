package com.qingguang.qingnote.tasks.scheduling

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.todoroo.astrid.alarms.AlarmService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.tasks.data.dao.TaskDao
import org.tasks.data.entity.Task
import javax.inject.Inject
import javax.inject.Singleton
import com.qingguang.qingnote.R
import com.qingguang.qingnote.tasks.receiver.TaskAlarmReceiver

@Singleton
class TaskAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SchedulerEntryPoint {
        fun alarmService(): AlarmService
        fun taskDao(): TaskDao
    }

    suspend fun scheduleNextAlarm() {
        val entryPoint = EntryPointAccessors.fromApplication(context, SchedulerEntryPoint::class.java)
        val alarmService = entryPoint.alarmService()
        val taskDao = entryPoint.taskDao()

        // 1. 触发当前过期的 alarms 并弹通知
        val nextAlarmTime = alarmService.triggerAlarms { notifications ->
            notifications.forEach { notification ->
                val task = taskDao.fetch(notification.taskId) ?: return@forEach
                if (!task.isCompleted && !task.isDeleted) {
                    showNotification(task)
                }
            }
        }

        // 2. 调度下一次闹钟
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
            action = ACTION_ALARM
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)

        if (nextAlarmTime > System.currentTimeMillis()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextAlarmTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextAlarmTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextAlarmTime,
                    pendingIntent
                )
            }
        }
    }

    internal fun showNotification(task: Task) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.task_reminder_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.task_reminder_channel_desc)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, com.qingguang.qingnote.ui.page.main.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val priorityStr = when (task.priority) {
            Task.Priority.HIGH -> "!!! "
            Task.Priority.MEDIUM -> "!! "
            Task.Priority.LOW -> "! "
            else -> ""
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("${priorityStr}${task.title}")
            .setContentText(task.notes ?: context.getString(R.string.task_due_reminder))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        notificationManager.notify(task.id.hashCode(), builder.build())
    }

    companion object {
        const val ACTION_ALARM = "com.qingguang.qingnote.tasks.ALARM"
        const val ALARM_REQUEST_CODE = 4004
        const val CHANNEL_ID = "qingnote_task_reminders_channel"
    }
}
