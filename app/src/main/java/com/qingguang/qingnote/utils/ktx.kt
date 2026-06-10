package com.qingguang.qingnote.utils

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.qingguang.qingnote.App
import com.qingguang.qingnote.R
import com.qingguang.qingnote.bean.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val Int.str: String
    get() = App.instance.getString(this)

fun toast(text: String) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(App.instance, text, Toast.LENGTH_SHORT).show()
    }
}

fun copy(note: Note) {
    val text = if (note.noteTitle.isNullOrEmpty()) {
        note.content
    } else {
        (note.noteTitle ?: "") + "\n\n" + note.content
    }
    //获取剪切板管理器
    val cm: ClipboardManager = App.instance.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    //设置内容到剪切板
    cm.setPrimaryClip(ClipData.newPlainText(null, text))
    toast(App.instance.getString(com.qingguang.qingnote.R.string.excute_success))
}

fun Context.isSystemLanguageEnglish(): Boolean {
    // 获取系统的语言设置
    val systemLanguage = Locale.getDefault().language

    // 检查语言设置是否为英文
    return systemLanguage.startsWith("en")
}

fun openMail(context: Context) {
    val uri = Uri.parse("mailto:" + "ldlywt@gmail.com")
    val packageInfos: List<ResolveInfo> = context.packageManager.queryIntentActivities(Intent(Intent.ACTION_SENDTO, uri), 0)
    val tempPkgNameList: MutableList<String> = ArrayList()
    val emailIntents: MutableList<Intent> = ArrayList()
    for (info in packageInfos) {
        val pkgName = info.activityInfo.packageName
        if (!tempPkgNameList.contains(pkgName)) {
            tempPkgNameList.add(pkgName)
            val intent: Intent = context.packageManager.getLaunchIntentForPackage(pkgName) ?: return
            emailIntents.add(intent)
        }
    }
    if (!emailIntents.isEmpty()) {
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra(Intent.EXTRA_SUBJECT, "QingNote Feedback") // 主题
        intent.putExtra(Intent.EXTRA_TEXT, "") // 正文
        val chooserIntent = Intent.createChooser(intent, context.getString(com.qingguang.qingnote.R.string.select_mail_tips))
        if (chooserIntent != null) {
            context.startActivity(chooserIntent)
        } else {
            toast(context.getString(com.qingguang.qingnote.R.string.no_mail_app_tips))
        }
    } else {
        toast(context.getString(com.qingguang.qingnote.R.string.no_mail_app_tips))
    }
}

fun Date.formatName(): String {
    return SimpleDateFormat("yyyyMMddHHmm", Locale.ENGLISH).format(this)
}

val backUpFileName:String
    get() = "QingNote" + Date().formatName() + ".zip"


@SuppressLint("Range")
fun Cursor.getStringValue(key: String): String = getString(getColumnIndex(key)) ?: ""


/**
 * 用于处理宽屏设备布局
 */
fun isWideScreen(context: Context): Boolean {
    val configuration = context.resources.configuration
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp
    return screenWidthDp > screenHeightDp
}