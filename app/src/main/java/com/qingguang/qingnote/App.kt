package com.qingguang.qingnote

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.qingguang.qingnote.db.repo.TagNoteRepo
import com.qingguang.qingnote.tasks.TasksRepository
import com.qingguang.qingnote.tasks.scheduling.TaskAlarmScheduler
import com.qingguang.qingnote.tasks.scheduling.TaskReminderWorker
import com.qingguang.qingnote.utils.SettingsPreferences
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

fun getAppName(): String {
    return "QingNote"
}


@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        instance = this
        super.onCreate()

        applicationScope.launch {
            try {
                val entryPoint = EntryPointAccessors.fromApplication(this@App, AppEntryPoint::class.java)
                entryPoint.taskAlarmScheduler().scheduleNextAlarm()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 注册 WorkManager 定时检查，作为 AlarmManager 的后备
        TaskReminderWorker.enqueue(this)

        applicationScope.launch {
            SettingsPreferences.themeMode.collect {
                SettingsPreferences.applyAppCompatThemeMode(it)
            }
        }
    }

    companion object {
        lateinit var instance: App
            private set
    }
}

@InstallIn(SingletonComponent::class)
@EntryPoint
interface AppEntryPoint {
    fun tagNoteRepo(): TagNoteRepo
    fun tasksRepository(): TasksRepository
    fun taskAlarmScheduler(): TaskAlarmScheduler
}
