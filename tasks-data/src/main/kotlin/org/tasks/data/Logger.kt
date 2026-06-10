package org.tasks.data

import android.util.Log

/**
 * Simple logger that mimics Kermit Logger API for compatibility with original Tasks.org code.
 */
object Logger {
    fun v(tag: String, message: () -> String) {
        Log.v(tag, message())
    }

    fun d(tag: String, message: () -> String) {
        Log.d(tag, message())
    }

    fun i(tag: String, message: () -> String) {
        Log.i(tag, message())
    }

    fun w(tag: String, message: () -> String) {
        Log.w(tag, message())
    }

    fun e(message: String, throwable: Throwable? = null, tag: String = "Tasks") {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }
}
