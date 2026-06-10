package org.tasks.time

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeUtils2 {
    fun currentTimeMillis(): Long {
        return MILLIS_PROVIDER.millis
    }

    private val SYSTEM_MILLIS_PROVIDER = SystemMillisProvider()

    @kotlin.concurrent.Volatile
    private var MILLIS_PROVIDER: MillisProvider = SYSTEM_MILLIS_PROVIDER

    fun setCurrentMillisFixed(millis: Long) {
        MILLIS_PROVIDER = FixedMillisProvider(millis)
    }

    fun setCurrentMillisSystem() {
        MILLIS_PROVIDER = SYSTEM_MILLIS_PROVIDER
    }
}

fun printTimestamp(timestamp: Long): String {
    return if (timestamp == 0L) {
        "0"
    } else {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    }
}

@JvmName("printTimestampExt")
fun Long.printTimestamp(): String = printTimestamp(this)
