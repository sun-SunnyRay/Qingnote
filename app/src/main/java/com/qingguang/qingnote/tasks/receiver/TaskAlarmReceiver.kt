package com.qingguang.qingnote.tasks.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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
