package com.qingguang.qingnote.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toMinute(): String {
    val dateTime = Date(this)
    val format = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    return format.format(dateTime)
}

fun Long.toDD(): String {
    val dateTime = Date(this)
    val format = SimpleDateFormat("dd", Locale.ENGLISH)
    return format.format(dateTime)
}

fun Long.toMM(): String {
    val dateTime = Date(this)
    val format = SimpleDateFormat("MM", Locale.ENGLISH)
    return format.format(dateTime)
}

fun Long.toYYMMDD(): String {
    val dateTime = Date(this)
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    return format.format(dateTime)
}

fun Long.toDate(): String {
    val dateTime = Date(this)
    val format = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)
    return format.format(dateTime)
}

fun Long.toMYYMM(): String {
    val dateTime = Date(this)
    val format = SimpleDateFormat("yyyy/MM", Locale.ENGLISH)
    return format.format(dateTime)
}

fun Long.toBackUpFileName(): String {
    val dateTime = Date(this)
    val format = SimpleDateFormat("yyyyMMddHHmm", Locale.ENGLISH)
    return format.format(dateTime)
}

fun Long.toTime(): String {
    val dateTime = Date(this)
    val format = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH)
    return format.format(dateTime)
}
