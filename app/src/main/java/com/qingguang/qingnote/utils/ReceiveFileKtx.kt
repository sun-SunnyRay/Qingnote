package com.qingguang.qingnote.utils

import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import com.qingguang.qingnote.App
import com.qingguang.qingnote.bean.Attachment
import top.zibin.luban.Luban
import java.io.File

suspend fun handlePickFiles(
    uris: Set<Uri>, callback: (list: List<Attachment>) -> Unit
) {
    val items = mutableListOf<Attachment>()
    withIO {
        uris.forEach { uri ->
            val context = App.instance
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                cursor.moveToFirst()
                var fileName = cursor.getStringValue(OpenableColumns.DISPLAY_NAME)
//                val size = cursor.getLongValue(OpenableColumns.SIZE)
                val type = context.contentResolver.getType(uri) ?: ""
                var extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(type)
                if (extension.isNullOrEmpty()) {
                    extension = fileName.getFilenameExtension()
                }
                if (extension.isNotEmpty()) {
                    fileName = fileName.getFilenameWithoutExtension() + "." + extension
                }
                cursor.close()
                val fileType: Attachment.Type
                try {
                    val dir = when {
                        fileName.isVideoFast() -> {
                            fileType = Attachment.Type.VIDEO
                            Environment.DIRECTORY_MOVIES
                        }

                        fileName.isImageFast() -> {
                            fileType = Attachment.Type.IMAGE
                            Environment.DIRECTORY_PICTURES
                        }

                        fileName.isAudioFast() -> {
                            fileType = Attachment.Type.AUDIO
                            Environment.DIRECTORY_MUSIC
                        }

                        else -> {
                            fileType = Attachment.Type.FILE
                            Environment.DIRECTORY_DOCUMENTS
                        }
                    }
                    val dst = context.getExternalFilesDir(dir)!!.path + "/$fileName"
                    val dstFile = File(dst)
                    if (dstFile.exists()) {
                        copyFile(context, uri, dstFile.newPath())
                    } else {
                        copyFile(context, uri, dst)
                    }
                    Luban.with(context).setTargetDir(context.getExternalFilesDir(dir)!!.path).load(dst).get().forEach {
                        if (it.exists() && it.path != dst) {
                            File(dst).delete()
                        }
                        items.add(Attachment(path = it.path, fileName = it.name, description = it.name, type = fileType))
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
        callback(items)
    }
}