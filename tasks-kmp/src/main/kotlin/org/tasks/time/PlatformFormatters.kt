package org.tasks.time

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

fun formatNumber(number: Int): String = number.toString()

fun formatDate(timestamp: Long, style: DateStyle): String {
    val dateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(timestamp),
        ZoneId.systemDefault()
    )
    val formatStyle = when (style) {
        DateStyle.SHORT -> FormatStyle.SHORT
        DateStyle.MEDIUM -> FormatStyle.MEDIUM
        DateStyle.LONG -> FormatStyle.LONG
        DateStyle.FULL -> FormatStyle.FULL
    }
    return dateTime.toLocalDate()
        .format(DateTimeFormatter.ofLocalizedDate(formatStyle).withLocale(Locale.getDefault()))
}

fun formatTime(timestamp: Long, is24HourFormat: Boolean): String {
    val dateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(timestamp),
        ZoneId.systemDefault()
    )
    return formatTimeString(dateTime, is24HourFormat)
}

fun formatFullDateTime(
    timestamp: Long,
    is24HourFormat: Boolean,
    dateStyle: DateStyle,
): String {
    val dateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(timestamp),
        ZoneId.systemDefault()
    )
    val formatStyle = when (dateStyle) {
        DateStyle.SHORT -> FormatStyle.SHORT
        DateStyle.MEDIUM -> FormatStyle.MEDIUM
        DateStyle.LONG -> FormatStyle.LONG
        DateStyle.FULL -> FormatStyle.FULL
    }
    return formatFullDateTimeString(dateTime, is24HourFormat, formatStyle)
}

fun formatDayOfWeek(timestamp: Long, style: TextStyle): String {
    val dateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(timestamp),
        ZoneId.systemDefault()
    )
    val javaStyle = when (style) {
        TextStyle.FULL -> JavaTextStyle.FULL
        TextStyle.SHORT -> JavaTextStyle.SHORT
        TextStyle.NARROW -> JavaTextStyle.NARROW
    }
    return dateTime.dayOfWeek.getDisplayName(javaStyle, Locale.getDefault())
}
