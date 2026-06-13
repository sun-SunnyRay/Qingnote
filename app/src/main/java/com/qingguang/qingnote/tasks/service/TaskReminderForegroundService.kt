package com.qingguang.qingnote.tasks.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.qingguang.qingnote.R
import com.qingguang.qingnote.ui.page.main.MainActivity

/**
 * 前台保活服务，用于在国产ROM上防止应用被杀后闹钟广播无法唤醒进程。
 *
 * 通过一个低优先级的常驻通知保持进程存活，确保 AlarmManager 的广播能够被正常接收。
 * 使用 START_STICKY 策略，系统杀掉后会自动重启。
 */
class TaskReminderForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // START_STICKY: 系统杀掉服务后会尝试重新创建
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.fg_service_channel_name),
                NotificationManager.IMPORTANCE_LOW // 低重要性，不会弹出横幅，只在通知栏静默显示
            ).apply {
                description = getString(R.string.fg_service_channel_desc)
                setShowBadge(false)
                enableVibration(false)
                setSound(null, null)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): android.app.Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(getString(R.string.fg_service_notification_title))
            .setContentText(getString(R.string.fg_service_notification_text))
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setShowWhen(false)
            .build()
    }

    companion object {
        const val CHANNEL_ID = "qingnote_fg_service_channel"
        const val NOTIFICATION_ID = 4005

        fun start(context: Context) {
            val intent = Intent(context, TaskReminderForegroundService::class.java)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, TaskReminderForegroundService::class.java)
            context.stopService(intent)
        }
    }
}
