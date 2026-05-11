package com.qingguang.qingnote.utils

fun String.getFilenameWithoutExtension() = substringBeforeLast(".")

fun String.getFilenameExtension() = substring(lastIndexOf(".") + 1)

operator fun String.times(x: Int): String {
    val stringBuilder = StringBuilder()
    for (i in 1..x) {
        stringBuilder.append(this)
    }
    return stringBuilder.toString()
}

fun String.isVideoFast() = Constant.VIDEO_EXTENSIONS.any { endsWith(it, true) }
fun String.isImageFast() = Constant.PHOTO_EXTENSIONS.any { endsWith(it, true) }
fun String.isAudioFast() = Constant.AUDIO_EXTENSIONS.any { endsWith(it, true) }