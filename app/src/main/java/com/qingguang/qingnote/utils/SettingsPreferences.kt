package com.qingguang.qingnote.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.qingguang.qingnote.App
import com.qingguang.qingnote.R
import com.qingguang.qingnote.tasks.TaskDefaultDate
import com.qingguang.qingnote.tasks.TaskDefaultPriority
import com.qingguang.qingnote.tasks.TaskDefaultRecurrence
import com.qingguang.qingnote.tasks.TaskDefaultReminder
import com.qingguang.qingnote.tasks.TaskSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private const val THEME_PREFERENCES = "THEME_PREFERENCES"
private val Context.themePreferences by preferencesDataStore(name = THEME_PREFERENCES)


object SettingsPreferences {
    enum class ThemeMode(@StringRes val resId: Int) {
        LIGHT(R.string.light_mode), DARK(R.string.dark_mode), SYSTEM(R.string.use_device_theme),
    }

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        val TASK_DEFAULT_PRIORITY = stringPreferencesKey("task_default_priority")
        val TASK_DEFAULT_START_DATE = stringPreferencesKey("task_default_start_date")
        val TASK_DEFAULT_DUE_DATE = stringPreferencesKey("task_default_due_date")
        val TASK_DEFAULT_REMINDER = stringPreferencesKey("task_default_reminder")
        val TASK_DEFAULT_RECURRENCE = stringPreferencesKey("task_default_recurrence")
        val TASK_DEFAULT_TAGS = stringPreferencesKey("task_default_tags")
        val TASK_DEFAULT_LIST_NAME = stringPreferencesKey("task_default_list_name")
        val TASK_DEFAULT_ADD_TO_CALENDAR = booleanPreferencesKey("task_default_add_to_calendar")
        val TASK_LIST_SHOW_FULL_TITLE = booleanPreferencesKey("task_list_show_full_title")
        val TASK_LIST_SHOW_DESCRIPTION = booleanPreferencesKey("task_list_show_description")
        val TASK_LIST_SHOW_FULL_DESCRIPTION = booleanPreferencesKey("task_list_show_full_description")
        val TASK_LIST_SHOW_START_DATE = booleanPreferencesKey("task_list_show_start_date")
        val TASK_LIST_SHOW_DUE_DATE = booleanPreferencesKey("task_list_show_due_date")
        val TASK_LIST_SHOW_PRIORITY = booleanPreferencesKey("task_list_show_priority")
        val TASK_EDIT_BACK_SAVES = booleanPreferencesKey("task_edit_back_saves")
        val TASK_EDIT_MULTILINE_TITLE = booleanPreferencesKey("task_edit_multiline_title")
        val TASK_ALWAYS_FULL_DATE = booleanPreferencesKey("task_always_full_date")
        val TASK_USE_24_HOUR = booleanPreferencesKey("task_use_24_hour")
        val TASK_DRAWER_SHOW_FILTERS = booleanPreferencesKey("task_drawer_show_filters")
        val TASK_DRAWER_SHOW_DUE_FILTERS = booleanPreferencesKey("task_drawer_show_due_filters")
        val TASK_DRAWER_HIDE_EMPTY_TAGS = booleanPreferencesKey("task_drawer_hide_empty_tags")
        val TASK_CALENDAR_END_AT_DUE = booleanPreferencesKey("task_calendar_end_at_due")
    }


    private val themePreferences = App.instance.themePreferences

    val themeMode = themePreferences.getEnum(PreferencesKeys.THEME_MODE, ThemeMode.SYSTEM)
    val dynamicColor = themePreferences.getBoolean(PreferencesKeys.DYNAMIC_COLOR, false)
    val firstLaunch = themePreferences.getBoolean(PreferencesKeys.FIRST_LAUNCH, true)
    val taskSettings: Flow<TaskSettings> = themePreferences.data.map { preferences ->
        TaskSettings(
            defaultPriority = preferences.enumValue(PreferencesKeys.TASK_DEFAULT_PRIORITY, TaskDefaultPriority.NONE),
            defaultStartDate = preferences.enumValue(PreferencesKeys.TASK_DEFAULT_START_DATE, TaskDefaultDate.NONE),
            defaultDueDate = preferences.enumValue(PreferencesKeys.TASK_DEFAULT_DUE_DATE, TaskDefaultDate.NONE),
            defaultReminder = preferences.enumValue(PreferencesKeys.TASK_DEFAULT_REMINDER, TaskDefaultReminder.NONE),
            defaultRecurrence = preferences.enumValue(PreferencesKeys.TASK_DEFAULT_RECURRENCE, TaskDefaultRecurrence.NONE),
            defaultTags = preferences[PreferencesKeys.TASK_DEFAULT_TAGS] ?: "",
            defaultListName = preferences[PreferencesKeys.TASK_DEFAULT_LIST_NAME] ?: "",
            defaultAddToCalendar = preferences[PreferencesKeys.TASK_DEFAULT_ADD_TO_CALENDAR] ?: false,
            showFullTaskTitle = preferences[PreferencesKeys.TASK_LIST_SHOW_FULL_TITLE] ?: false,
            showDescription = preferences[PreferencesKeys.TASK_LIST_SHOW_DESCRIPTION] ?: true,
            showFullDescription = preferences[PreferencesKeys.TASK_LIST_SHOW_FULL_DESCRIPTION] ?: false,
            showStartDate = preferences[PreferencesKeys.TASK_LIST_SHOW_START_DATE] ?: true,
            showDueDate = preferences[PreferencesKeys.TASK_LIST_SHOW_DUE_DATE] ?: true,
            showPriorityIndicator = preferences[PreferencesKeys.TASK_LIST_SHOW_PRIORITY] ?: true,
            backButtonSavesTask = preferences[PreferencesKeys.TASK_EDIT_BACK_SAVES] ?: false,
            multilineTaskTitle = preferences[PreferencesKeys.TASK_EDIT_MULTILINE_TITLE] ?: false,
            alwaysDisplayFullDate = preferences[PreferencesKeys.TASK_ALWAYS_FULL_DATE] ?: false,
            use24HourTime = preferences[PreferencesKeys.TASK_USE_24_HOUR] ?: true,
            drawerShowFilters = preferences[PreferencesKeys.TASK_DRAWER_SHOW_FILTERS] ?: true,
            drawerShowDueFilters = preferences[PreferencesKeys.TASK_DRAWER_SHOW_DUE_FILTERS] ?: true,
            drawerHideEmptyTags = preferences[PreferencesKeys.TASK_DRAWER_HIDE_EMPTY_TAGS] ?: true,
            calendarEndAtDueTime = preferences[PreferencesKeys.TASK_CALENDAR_END_AT_DUE] ?: true,
        )
    }

    private suspend fun <T> updatePreference(key: Preferences.Key<T>, value: T) {
        themePreferences.edit { preferences ->
            preferences[key] = value
        }
    }

    fun applyAppCompatThemeMode(themeMode: ThemeMode) {
        val appCompatMode = when (themeMode) {
            ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(appCompatMode)
    }

    suspend fun changeThemeMode(themeMode: ThemeMode) {
        updatePreference(PreferencesKeys.THEME_MODE, themeMode.name)
        withContext(Dispatchers.Main) {
            applyAppCompatThemeMode(themeMode)
        }
    }


    suspend fun changeDynamicColor(dynamicTheme: Boolean) {
        updatePreference(PreferencesKeys.DYNAMIC_COLOR, dynamicTheme)
    }

    suspend fun changeFirstLaunch(isFirst: Boolean) {
        updatePreference(PreferencesKeys.FIRST_LAUNCH, isFirst)
    }

    suspend fun changeTaskDefaultPriority(value: TaskDefaultPriority) =
        updatePreference(PreferencesKeys.TASK_DEFAULT_PRIORITY, value.name)

    suspend fun changeTaskDefaultStartDate(value: TaskDefaultDate) =
        updatePreference(PreferencesKeys.TASK_DEFAULT_START_DATE, value.name)

    suspend fun changeTaskDefaultDueDate(value: TaskDefaultDate) =
        updatePreference(PreferencesKeys.TASK_DEFAULT_DUE_DATE, value.name)

    suspend fun changeTaskDefaultReminder(value: TaskDefaultReminder) =
        updatePreference(PreferencesKeys.TASK_DEFAULT_REMINDER, value.name)

    suspend fun changeTaskDefaultRecurrence(value: TaskDefaultRecurrence) =
        updatePreference(PreferencesKeys.TASK_DEFAULT_RECURRENCE, value.name)

    suspend fun changeTaskDefaultTags(value: String) =
        updatePreference(PreferencesKeys.TASK_DEFAULT_TAGS, value)

    suspend fun changeTaskDefaultList(value: String) =
        updatePreference(PreferencesKeys.TASK_DEFAULT_LIST_NAME, value)

    suspend fun changeTaskDefaultAddToCalendar(value: Boolean) =
        updatePreference(PreferencesKeys.TASK_DEFAULT_ADD_TO_CALENDAR, value)

    suspend fun changeTaskShowFullTitle(value: Boolean) =
        updatePreference(PreferencesKeys.TASK_LIST_SHOW_FULL_TITLE, value)

    suspend fun changeTaskShowDescription(value: Boolean) =
        updatePreference(PreferencesKeys.TASK_LIST_SHOW_DESCRIPTION, value)

    suspend fun changeTaskShowFullDescription(value: Boolean) =
        updatePreference(PreferencesKeys.TASK_LIST_SHOW_FULL_DESCRIPTION, value)

    suspend fun changeTaskShowStartDate(value: Boolean) =
        updatePreference(PreferencesKeys.TASK_LIST_SHOW_START_DATE, value)

    suspend fun changeTaskShowDueDate(value: Boolean) =
        updatePreference(PreferencesKeys.TASK_LIST_SHOW_DUE_DATE, value)

    suspend fun changeTaskShowPriority(value: Boolean) =
        updatePreference(PreferencesKeys.TASK_LIST_SHOW_PRIORITY, value)

    suspend fun changeTaskBackButtonSaves(value: Boolean) =
        updatePreference(PreferencesKeys.TASK_EDIT_BACK_SAVES, value)

    suspend fun changeTaskMultilineTitle(value: Boolean) =
        updatePreference(PreferencesKeys.TASK_EDIT_MULTILINE_TITLE, value)

    suspend fun changeTaskAlwaysFullDate(value: Boolean) =
        updatePreference(PreferencesKeys.TASK_ALWAYS_FULL_DATE, value)

    suspend fun changeTaskUse24Hour(value: Boolean) =
        updatePreference(PreferencesKeys.TASK_USE_24_HOUR, value)

    suspend fun changeTaskDrawerShowFilters(value: Boolean) =
        updatePreference(PreferencesKeys.TASK_DRAWER_SHOW_FILTERS, value)

    suspend fun changeTaskDrawerShowDueFilters(value: Boolean) =
        updatePreference(PreferencesKeys.TASK_DRAWER_SHOW_DUE_FILTERS, value)

    suspend fun changeTaskDrawerHideEmptyTags(value: Boolean) =
        updatePreference(PreferencesKeys.TASK_DRAWER_HIDE_EMPTY_TAGS, value)

    suspend fun changeTaskCalendarEndAtDue(value: Boolean) =
        updatePreference(PreferencesKeys.TASK_CALENDAR_END_AT_DUE, value)

    suspend fun resetTaskSettings() {
        themePreferences.edit { preferences ->
            preferences.remove(PreferencesKeys.TASK_DEFAULT_PRIORITY)
            preferences.remove(PreferencesKeys.TASK_DEFAULT_START_DATE)
            preferences.remove(PreferencesKeys.TASK_DEFAULT_DUE_DATE)
            preferences.remove(PreferencesKeys.TASK_DEFAULT_REMINDER)
            preferences.remove(PreferencesKeys.TASK_DEFAULT_RECURRENCE)
            preferences.remove(PreferencesKeys.TASK_DEFAULT_TAGS)
            preferences.remove(PreferencesKeys.TASK_DEFAULT_LIST_NAME)
            preferences.remove(PreferencesKeys.TASK_DEFAULT_ADD_TO_CALENDAR)
            preferences.remove(PreferencesKeys.TASK_LIST_SHOW_FULL_TITLE)
            preferences.remove(PreferencesKeys.TASK_LIST_SHOW_DESCRIPTION)
            preferences.remove(PreferencesKeys.TASK_LIST_SHOW_FULL_DESCRIPTION)
            preferences.remove(PreferencesKeys.TASK_LIST_SHOW_START_DATE)
            preferences.remove(PreferencesKeys.TASK_LIST_SHOW_DUE_DATE)
            preferences.remove(PreferencesKeys.TASK_LIST_SHOW_PRIORITY)
            preferences.remove(PreferencesKeys.TASK_EDIT_BACK_SAVES)
            preferences.remove(PreferencesKeys.TASK_EDIT_MULTILINE_TITLE)
            preferences.remove(PreferencesKeys.TASK_ALWAYS_FULL_DATE)
            preferences.remove(PreferencesKeys.TASK_USE_24_HOUR)
            preferences.remove(PreferencesKeys.TASK_DRAWER_SHOW_FILTERS)
            preferences.remove(PreferencesKeys.TASK_DRAWER_SHOW_DUE_FILTERS)
            preferences.remove(PreferencesKeys.TASK_DRAWER_HIDE_EMPTY_TAGS)
            preferences.remove(PreferencesKeys.TASK_CALENDAR_END_AT_DUE)
        }
    }

    private inline fun <reified T : Enum<T>> Preferences.enumValue(
        key: Preferences.Key<String>,
        defaultValue: T,
    ): T =
        this[key]?.let {
            try {
                enumValueOf<T>(it)
            } catch (e: IllegalArgumentException) {
                defaultValue
            }
        } ?: defaultValue
}


inline fun <reified T : Enum<T>> DataStore<Preferences>.getEnum(key: Preferences.Key<String>, defaultValue: T): Flow<T> {
    return this.data.map { preferences ->
        preferences[key]?.let {
            try {
                enumValueOf<T>(it)
            } catch (e: IllegalArgumentException) {
                defaultValue
            }
        } ?: defaultValue
    }
}


fun DataStore<Preferences>.getBoolean(key: Preferences.Key<Boolean>, defaultValue: Boolean): Flow<Boolean> {
    return this.data.map { preferences ->
        preferences[key] ?: defaultValue
    }
}

fun DataStore<Preferences>.getInt(key: Preferences.Key<Int>, defaultValue: Int): Flow<Int> {
    return this.data.map { preferences ->
        preferences[key] ?: defaultValue
    }
}

fun DataStore<Preferences>.getString(key: Preferences.Key<String>, defaultValue: String?): Flow<String?> {
    return this.data.map { preferences ->
        preferences[key] ?: defaultValue
    }
}
