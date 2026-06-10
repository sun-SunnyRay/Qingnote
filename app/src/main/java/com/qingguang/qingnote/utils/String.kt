package com.qingguang.qingnote.utils

fun String.getFilenameWithoutExtension() = substringBeforeLast(".")

fun String.getFilenameExtension() = substring(lastIndexOf(".") + 1)

fun String.isVideoFast() = Constant.VIDEO_EXTENSIONS.any { endsWith(it, true) }
fun String.isImageFast() = Constant.PHOTO_EXTENSIONS.any { endsWith(it, true) }
fun String.isAudioFast() = Constant.AUDIO_EXTENSIONS.any { endsWith(it, true) }