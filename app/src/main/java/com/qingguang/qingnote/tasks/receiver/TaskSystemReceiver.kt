package com.qingguang.qingnote.tasks.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.qingguang.qingnote.tasks.scheduling.TaskAlarmScheduler
import com.qingguang.qingnote.tasks.service.TaskReminderForegroundService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TaskSystemReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmScheduler: TaskAlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            // 开机后或时间变更后，重新启动前台保活服务
            if (action == Intent.ACTION_BOOT_COMPLETED) {
                TaskReminderForegroundService.start(context)
            }

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    alarmScheduler.scheduleNextAlarm()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
