package com.qingguang.qingnote

import android.app.Application
import com.qingguang.qingnote.db.repo.TagNoteRepo
import com.qingguang.qingnote.tasks.TasksRepository
import com.qingguang.qingnote.utils.SettingsPreferences
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

fun getAppName(): String {
    return "IdeaMemo"
}


@HiltAndroidApp
class App : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        instance = this
        super.onCreate()

        applicationScope.launch {

        }

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

@InstallIn(SingletonComponent::class)   // <-- add this line
@EntryPoint
interface AppEntryPoint {
    fun tagNoteRepo(): TagNoteRepo
    fun tasksRepository(): TasksRepository
}
