package com.qingguang.qingnote.tasks

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

data class TaskSettings(
    val defaultPriority: TaskDefaultPriority = TaskDefaultPriority.NONE,
    val defaultStartDate: TaskDefaultDate = TaskDefaultDate.NONE,
    val defaultDueDate: TaskDefaultDate = TaskDefaultDate.NONE,
    val defaultReminder: TaskDefaultReminder = TaskDefaultReminder.NONE,
    val defaultRecurrence: TaskDefaultRecurrence = TaskDefaultRecurrence.NONE,
    val defaultTags: String = "",
    val defaultListName: String = "",
    val defaultAddToCalendar: Boolean = false,
    val showFullTaskTitle: Boolean = false,
    val showDescription: Boolean = true,
    val showFullDescription: Boolean = false,
    val showStartDate: Boolean = true,
    val showDueDate: Boolean = true,
    val showPriorityIndicator: Boolean = true,
    val backButtonSavesTask: Boolean = false,
    val multilineTaskTitle: Boolean = false,
    val alwaysDisplayFullDate: Boolean = false,
    val use24HourTime: Boolean = true,
    val drawerShowFilters: Boolean = true,
    val drawerShowDueFilters: Boolean = true,
    val drawerHideEmptyTags: Boolean = true,
    val calendarEndAtDueTime: Boolean = true,
)

enum class TaskDefaultPriority {
    NONE,
    LOW,
    MEDIUM,
    HIGH,
}

enum class TaskDefaultDate {
    NONE,
    TODAY,
    TOMORROW,
    NEXT_WEEK,
}

enum class TaskDefaultReminder {
    NONE,
    AT_DUE_TIME,
    TEN_MINUTES_BEFORE,
    TOMORROW_MORNING,
}

enum class TaskDefaultRecurrence {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
}

fun TaskDefaultDate.toTaskMillis(endOfDay: Boolean): Long {
    val date = when (this) {
        TaskDefaultDate.NONE -> return 0L
        TaskDefaultDate.TODAY -> LocalDate.now()
        TaskDefaultDate.TOMORROW -> LocalDate.now().plusDays(1)
        TaskDefaultDate.NEXT_WEEK -> LocalDate.now().plusWeeks(1)
    }
    val time = if (endOfDay) LocalTime.of(23, 59) else LocalTime.of(9, 0)
    return date.atTime(time).toMillis()
}

fun TaskDefaultReminder.toTaskMillis(dueDate: Long): Long =
    when (this) {
        TaskDefaultReminder.NONE -> 0L
        TaskDefaultReminder.AT_DUE_TIME -> dueDate
        TaskDefaultReminder.TEN_MINUTES_BEFORE -> if (dueDate > 0) dueDate - 10 * 60 * 1000 else 0L
        TaskDefaultReminder.TOMORROW_MORNING -> LocalDate.now().plusDays(1).atTime(9, 0).toMillis()
    }.coerceAtLeast(0L)

fun TaskDefaultRecurrence.toRRule(): String? =
    when (this) {
        TaskDefaultRecurrence.NONE -> null
        TaskDefaultRecurrence.DAILY -> "RRULE:FREQ=DAILY"
        TaskDefaultRecurrence.WEEKLY -> "RRULE:FREQ=WEEKLY"
        TaskDefaultRecurrence.MONTHLY -> "RRULE:FREQ=MONTHLY"
    }

private fun LocalDateTime.toMillis(): Long =
    atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
