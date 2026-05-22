package org.tasks.injection

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.todoroo.astrid.alarms.AlarmCalculator
import com.todoroo.astrid.alarms.AlarmService
import com.todoroo.astrid.repeats.RepeatTaskHelper
import com.todoroo.astrid.timers.TimerPlugin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.tasks.audio.SoundPlayer
import org.tasks.broadcast.RefreshBroadcaster
import org.tasks.caldav.VtodoCache
import org.tasks.calendars.CalendarHelper
import org.tasks.data.TaskSaver
import org.tasks.data.dao.AlarmDao
import org.tasks.data.dao.CaldavDao
import org.tasks.data.dao.CompletionDao
import org.tasks.data.dao.DeletionDao
import org.tasks.data.dao.TaskDao
import org.tasks.data.entity.CaldavAccount
import org.tasks.data.entity.CaldavCalendar
import org.tasks.data.entity.CaldavTask
import org.tasks.data.entity.Task
import org.tasks.jobs.BackgroundWork
import org.tasks.location.LocationService
import org.tasks.notifications.CancelReason
import org.tasks.notifications.Notifier
import org.tasks.preferences.AppPreferences
import org.tasks.preferences.TasksPreferences
import org.tasks.service.TaskCleanup
import org.tasks.service.TaskCompleter
import org.tasks.service.TaskDeleter
import org.tasks.sync.SyncAdapters
import org.tasks.sync.SyncSource
import org.tasks.data.entity.Alarm
import javax.inject.Qualifier
import javax.inject.Singleton
import com.qingguang.qingnote.tasks.scheduling.TaskAlarmScheduler

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TasksDataStore

@Module
@InstallIn(SingletonComponent::class)
object TasksBindsModule {

    @Provides
    @Singleton
    @TasksDataStore
    fun provideTasksDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("tasks_preferences")
        }
    }

    @Provides
    @Singleton
    fun provideTasksPreferences(@TasksDataStore dataStore: DataStore<Preferences>): TasksPreferences {
        return TasksPreferences(dataStore)
    }

    @Provides
    @Singleton
    fun provideRefreshBroadcaster(
        taskAlarmScheduler: TaskAlarmScheduler
    ): RefreshBroadcaster = object : RefreshBroadcaster {
        override suspend fun broadcastRefresh() {
            taskAlarmScheduler.scheduleNextAlarm()
        }
        override suspend fun broadcastTaskCompleted(taskIds: List<Long>, oldDueDate: Long) {
            taskAlarmScheduler.scheduleNextAlarm()
        }
    }

    @Provides
    @Singleton
    fun provideNotifier(
        taskAlarmScheduler: TaskAlarmScheduler
    ): Notifier = object : Notifier {
        override suspend fun cancel(taskId: Long, reason: CancelReason) {
            taskAlarmScheduler.scheduleNextAlarm()
        }
        override suspend fun cancel(taskIds: List<Long>, reason: CancelReason) {
            taskAlarmScheduler.scheduleNextAlarm()
        }
        override suspend fun triggerNotifications() {
            taskAlarmScheduler.scheduleNextAlarm()
        }
        override suspend fun updateTimerNotification() {}
    }

    @Provides
    @Singleton
    fun provideLocationService(): LocationService = object : LocationService {
        override suspend fun updateGeofences(taskId: Long) {}
    }

    @Provides
    @Singleton
    fun provideSyncAdapters(): SyncAdapters = object : SyncAdapters {
        override suspend fun sync(task: Task, original: Task?) {}
        override suspend fun sync(source: SyncSource) {}
    }

    @Provides
    @Singleton
    fun provideBackgroundWork(): BackgroundWork = object : BackgroundWork {
        override suspend fun updateCalendar(task: Task) {}
        override suspend fun scheduleRefresh() {}
    }

    @Provides
    @Singleton
    fun provideSoundPlayer(): SoundPlayer = object : SoundPlayer {
        override suspend fun playCompletionSound() {}
    }

    @Provides
    @Singleton
    fun provideCalendarHelper(): CalendarHelper = object : CalendarHelper {
        override suspend fun updateEvent(task: Task) {}
        override suspend fun rescheduleRepeatingTask(task: Task) {}
        override suspend fun updateCalendar(task: Task) {}
    }

    @Provides
    @Singleton
    fun provideVtodoCache(): VtodoCache = object : VtodoCache {
        override suspend fun delete(list: CaldavCalendar) {}
        override suspend fun delete(account: CaldavAccount) {}
        override suspend fun move(from: CaldavCalendar, to: CaldavCalendar, task: CaldavTask) {}
    }

    @Provides
    @Singleton
    fun provideTaskCleanup(): TaskCleanup = object : TaskCleanup {}

    @Provides
    @Singleton
    fun provideAppPreferences(): AppPreferences = object : AppPreferences {
        override suspend fun getInstallVersion(): Int = 0
        override suspend fun setInstallVersion(value: Int) {}
        override suspend fun getInstallDate(): Long = 0
        override suspend fun setInstallDate(value: Long) {}
        override suspend fun getDeviceInstallVersion(): Int = 0
        override suspend fun setDeviceInstallVersion(value: Int) {}
        override suspend fun isDefaultDueTimeEnabled(): Boolean = false
        override suspend fun defaultLocationReminder(): Int = 0
        override suspend fun defaultAlarms(): List<Alarm> = emptyList()
        override suspend fun defaultRandomHours(): Int = 0
        override suspend fun defaultRingMode(): Int = 0
        override suspend fun defaultDueTime(): Int = 0
        override suspend fun defaultPriority(): Int = Task.Priority.NONE
        override suspend fun isCurrentlyQuietHours(): Boolean = false
        override suspend fun adjustForQuietHours(time: Long): Long = time
    }

    @Provides
    @Singleton
    fun provideTimerPlugin(notifier: Notifier, taskDao: TaskDao): TimerPlugin {
        return TimerPlugin(notifier, taskDao)
    }

    @Provides
    @Singleton
    fun provideRandom(): org.tasks.reminders.Random {
        return org.tasks.reminders.Random()
    }

    @Provides
    @Singleton
    fun provideAlarmCalculator(random: org.tasks.reminders.Random): AlarmCalculator {
        return AlarmCalculator(random, 32400000) // 9:00 AM default
    }

    @Provides
    @Singleton
    fun provideAlarmService(
        alarmDao: AlarmDao,
        taskDao: TaskDao,
        refreshBroadcaster: RefreshBroadcaster,
        notifier: Notifier,
        alarmCalculator: AlarmCalculator,
        preferences: AppPreferences,
    ): AlarmService {
        return AlarmService(alarmDao, taskDao, refreshBroadcaster, notifier, alarmCalculator, preferences)
    }

    @Provides
    @Singleton
    fun provideTaskSaver(
        taskDao: TaskDao,
        refreshBroadcaster: RefreshBroadcaster,
        notifier: Notifier,
        locationService: LocationService,
        timerPlugin: TimerPlugin,
        syncAdapters: SyncAdapters,
        backgroundWork: BackgroundWork,
    ): TaskSaver {
        return TaskSaver(taskDao, refreshBroadcaster, notifier, locationService, timerPlugin, syncAdapters, backgroundWork)
    }

    @Provides
    @Singleton
    fun provideRepeatTaskHelper(
        calendarHelper: CalendarHelper,
        alarmService: AlarmService,
        taskSaver: TaskSaver,
    ): RepeatTaskHelper {
        return RepeatTaskHelper(calendarHelper, alarmService, taskSaver)
    }

    @Provides
    @Singleton
    fun provideTaskCompleter(
        taskDao: TaskDao,
        taskSaver: TaskSaver,
        notifier: Notifier,
        refreshBroadcaster: RefreshBroadcaster,
        repeatTaskHelper: RepeatTaskHelper,
        caldavDao: CaldavDao,
        calendarHelper: CalendarHelper,
        completionDao: CompletionDao,
        soundPlayer: SoundPlayer,
    ): TaskCompleter {
        return TaskCompleter(
            taskDao, taskSaver, notifier, refreshBroadcaster,
            repeatTaskHelper, caldavDao, calendarHelper, completionDao, soundPlayer
        )
    }

    @Provides
    @Singleton
    fun provideTaskDeleter(
        deletionDao: DeletionDao,
        taskDao: TaskDao,
        refreshBroadcaster: RefreshBroadcaster,
        vtodoCache: VtodoCache,
        tasksPreferences: TasksPreferences,
        taskCleanup: TaskCleanup,
    ): TaskDeleter {
        return TaskDeleter(deletionDao, taskDao, refreshBroadcaster, vtodoCache, tasksPreferences, taskCleanup)
    }
}
