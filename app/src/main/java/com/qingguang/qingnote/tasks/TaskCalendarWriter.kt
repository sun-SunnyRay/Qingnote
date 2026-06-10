package com.qingguang.qingnote.tasks

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tasks.data.entity.Task
import java.util.TimeZone
import javax.inject.Inject

class TaskCalendarWriter @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun canWriteCalendar(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED

    fun sync(
        task: Task,
        addToCalendar: Boolean,
        startDate: Long,
        dueDate: Long,
        endAtDueTime: Boolean,
    ): String? {
        if (!canWriteCalendar()) return task.calendarURI
        if (!addToCalendar) {
            delete(task.calendarURI)
            return null
        }

        val calendarId = firstWritableCalendarId() ?: return task.calendarURI
        val start = when {
            startDate > 0 -> startDate
            dueDate > 0 && endAtDueTime -> dueDate - 60 * 60 * 1000L
            dueDate > 0 -> dueDate
            else -> System.currentTimeMillis()
        }.coerceAtLeast(0L)
        val end = when {
            dueDate > start && (startDate > 0 || endAtDueTime) -> dueDate
            else -> start + 60 * 60 * 1000L
        }
        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.TITLE, task.title.orEmpty())
            put(CalendarContract.Events.DESCRIPTION, task.notes.orEmpty())
            put(CalendarContract.Events.DTSTART, start)
            put(CalendarContract.Events.DTEND, end)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        val existingUri = task.calendarURI?.let { runCatching { android.net.Uri.parse(it) }.getOrNull() }
        if (existingUri != null) {
            val updated = runCatching {
                context.contentResolver.update(existingUri, values, null, null)
            }.getOrDefault(0)
            if (updated > 0) return existingUri.toString()
        }

        return runCatching {
            context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)?.toString()
        }.getOrNull()
    }

    private fun delete(uri: String?) {
        if (uri.isNullOrBlank()) return
        runCatching {
            context.contentResolver.delete(android.net.Uri.parse(uri), null, null)
        }
    }

    private fun firstWritableCalendarId(): Long? {
        val projection = arrayOf(CalendarContract.Calendars._ID)
        val selection = "${CalendarContract.Calendars.VISIBLE}=1 AND ${CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL}>=?"
        val args = arrayOf(CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR.toString())
        return context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            selection,
            args,
            "${CalendarContract.Calendars.IS_PRIMARY} DESC"
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getLong(0)
            } else {
                null
            }
        }
    }
}
