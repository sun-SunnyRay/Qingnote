package com.qingguang.qingnote.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend fun <T> withIO(block: suspend CoroutineScope.() -> T, ): T {
    return withContext(Dispatchers.IO, block)
}

fun lunchIo(runner: suspend CoroutineScope.() -> Unit) = CoroutineScope(Dispatchers.IO).launch { runner.invoke((this)) }
fun lunchMain(runner: suspend CoroutineScope.() -> Unit) = CoroutineScope(Dispatchers.Main).launch { runner.invoke((this)) }
