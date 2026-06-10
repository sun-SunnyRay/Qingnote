package com.qingguang.qingnote.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract


object None

object ChoseFolderContract : ActivityResultContract<None?, Uri?>() {
    override fun createIntent(context: Context, input: None?): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent != null && resultCode == Activity.RESULT_OK) intent.data else null
    }
}

object ExportTextContract : ActivityResultContract<None?, Uri?>() {
    override fun createIntent(context: Context, input: None?): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent != null && resultCode == Activity.RESULT_OK) intent.data else null
    }
}

object ExportHtmlContract : ActivityResultContract<None?, Uri?>() {
    override fun createIntent(context: Context, input: None?): Intent {
        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/zip"
            putExtra(Intent.EXTRA_TITLE, "QingNoteHtml.zip")
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent != null && resultCode == Activity.RESULT_OK) intent.data else null
    }
}

class ExportMarkDownContract(val name: String) : ActivityResultContract<None?, Uri?>() {
    override fun createIntent(context: Context, input: None?): Intent {
        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/markdown"
            putExtra(Intent.EXTRA_TITLE, "$name.md")
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent != null && resultCode == Activity.RESULT_OK) intent.data else null
    }
}

object ImportHtmlZipContract : ActivityResultContract<None?, Uri?>() {
    override fun createIntent(context: Context, input: None?): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/zip"
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent != null && resultCode == Activity.RESULT_OK) intent.data else null
    }
}

