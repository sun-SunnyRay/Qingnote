package org.tasks

import android.util.Log

/**
 * Logger compatibility object that delegates to android.util.Log.
 * Replaces co.touchlab.kermit.Logger from KMP.
 */
object Logger {
    fun d(tag: String = "Tasks", message: () -> String) {
        Log.d(tag, message())
    }

    fun v(tag: String = "Tasks", message: () -> String) {
        Log.v(tag, message())
    }

    fun w(tag: String = "Tasks", message: () -> String) {
        Log.w(tag, message())
    }

    fun e(tag: String = "Tasks", message: () -> String) {
        Log.e(tag, message())
    }

    fun e(e: Throwable, tag: String = "Tasks", message: () -> String) {
        Log.e(tag, message(), e)
    }
}
