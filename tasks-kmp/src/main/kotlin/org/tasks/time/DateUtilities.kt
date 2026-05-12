package org.tasks.time

import org.tasks.data.entity.Task.Companion.hasDueTime
import org.tasks.time.DateTimeUtils2.currentTimeMillis
import kotlin.math.abs

fun getRelativeDateTime(
    date: Long,
    is24HourFormat: Boolean,
    style: DateStyle = DateStyle.MEDIUM,
    alwaysDisplayFullDate: Boolean = false,
    lowercase: Boolean = false
): String {
    if (alwaysDisplayFullDate || !isWithinSixDays(date)) {
        return if (hasDueTime(date))
            getFullDateTime(date, is24HourFormat, style)
        else
            getFullDate(date, style)
    }

    val day = getRelativeDay(date, isAbbreviated(style), lowercase)
    return if (hasDueTime(date)) {
        val time = formatTime(date, is24HourFormat)
        if (currentTimeMillis().startOfDay() == date.startOfDay())
            time
        else
            String.format("%s %s", day, time)
    } else {
        day
    }
}

fun getRelativeDay(
    date: Long,
    style: DateStyle = DateStyle.MEDIUM,
    alwaysDisplayFullDate: Boolean = false,
    lowercase: Boolean = false,
): String =
    if (alwaysDisplayFullDate || !isWithinSixDays(date)) {
        getFullDate(date, style)
    } else {
        getRelativeDay(date, isAbbreviated(style), lowercase)
    }

fun getFullDate(
    date: Long,
    style: DateStyle = DateStyle.LONG,
): String = stripYear(formatDate(date, style), currentTimeMillis().year)

fun getFullDateTime(
    date: Long,
    is24HourFormat: Boolean,
    style: DateStyle = DateStyle.LONG,
): String = stripYear(formatFullDateTime(date, is24HourFormat, style), currentTimeMillis().year)

private fun isAbbreviated(style: DateStyle): Boolean =
    style == DateStyle.SHORT || style == DateStyle.MEDIUM

private fun stripYear(date: String, year: Int): String =
    date.replace("(?: de |, |/| |\\u00a0)?$year(?:年|년 |[\\s\\u00a0]г\\.)?".toRegex(), "")

private fun getRelativeDay(
    date: Long,
    abbreviated: Boolean,
    lowercase: Boolean
): String {
    val startOfToday = currentTimeMillis().startOfDay()
    val startOfDate = date.startOfDay()

    if (startOfToday == startOfDate) {
        return if (lowercase) "today" else "Today"
    }

    if (startOfToday.plusDays(1) == startOfDate) {
        return if (abbreviated) {
            if (lowercase) "tmrw" else "Tmrw"
        } else {
            if (lowercase) "tomorrow" else "Tomorrow"
        }
    }

    if (startOfDate.plusDays(1) == startOfToday) {
        return when {
            abbreviated -> if (lowercase) "yest" else "Yest"
            lowercase -> "yesterday"
            else -> "Yesterday"
        }
    }

    return formatDayOfWeek(
        timestamp = date,
        style = if (abbreviated) TextStyle.SHORT else TextStyle.FULL
    )
}

private fun isWithinSixDays(date: Long): Boolean {
    val startOfToday = currentTimeMillis().startOfDay()
    val startOfDate = date.startOfDay()
    return abs((startOfToday - startOfDate).toDouble()) <= ONE_DAY * 6
}
