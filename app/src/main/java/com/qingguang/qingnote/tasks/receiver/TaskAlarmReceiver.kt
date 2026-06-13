package com.qingguang.qingnote.tasks.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import com.qingguang.qingnote.tasks.scheduling.TaskAlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TaskAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmScheduler: TaskAlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TaskAlarmScheduler.ACTION_ALARM) {
            Log.i(TAG, "Alarm received, processing task reminders...")
            val pendingResult = goAsync()
            val wakeLock = acquireWakeLock(context)
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    alarmScheduler.scheduleNextAlarm()
                    Log.i(TAG, "Alarm processing completed successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to process alarm", e)
                } finally {
                    wakeLock?.release()
                    pendingResult.finish()
                }
            }
        }
    }

    private fun acquireWakeLock(context: Context): PowerManager.WakeLock? {
        return try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "qingnote:task_alarm_wakelock"
            )
            wakeLock.acquire(30_000L) // 30秒超时，防止忘记释放
            wakeLock
        } catch (e: Exception) {
            Log.e(TAG, "Failed to acquire wake lock", e)
            null
        }
    }

    companion object {
        private const val TAG = "TaskAlarmReceiver"
    }
}
