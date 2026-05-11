package com.qingguang.qingnote.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color
import com.qingguang.qingnote.App
import com.qingguang.qingnote.R
import com.qingguang.qingnote.bean.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

fun Boolean?.orFalse(): Boolean = this ?: false

fun randomColor() = Color(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255))

val Int.str: String
    get() = App.instance.getString(this)

fun mainThread(block: () -> Unit) {
    Handler(Looper.getMainLooper()).post {
        block()
    }
}

fun toast(text: String) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(App.instance, text, Toast.LENGTH_SHORT).show()
    }
}

fun shareApp(context: Context) {
    val text = context.getString(R.string.share_app_content)
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.putExtra(Intent.EXTRA_TEXT, text)
    intent.type = "text/plain"
    context.startActivity(Intent.createChooser(intent, R.string.app_name.str))
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

fun Activity.requestAllFileManagerPermission(callback: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (Environment.isExternalStorageManager()) {
            callback()
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:" + App.instance.packageName)
            startActivity(intent)
        }
    } else {
        callback()
    }
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
        intent.putExtra(Intent.EXTRA_SUBJECT, "IdeaMemo Feedback") // 主题
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

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

fun Date.formatName(): String {
    return SimpleDateFormat("yyyyMMddHHmm", Locale.ENGLISH).format(this)
}

val backUpFileName:String
    get() = "Idea" + Date().formatName() + ".zip"


@SuppressLint("Range")
fun Cursor.getStringValue(key: String): String = getString(getColumnIndex(key)) ?: ""


// 判断是否为平板或大屏设备
fun isTabletDevice(context: Context): Boolean {
    val configuration = context.resources.configuration
    val screenWidthDp = configuration.smallestScreenWidthDp

    return screenWidthDp >= 600
}

/**
 * 用于处理宽屏设备布局
 */
fun isWideScreen(context: Context): Boolean {
    val configuration = context.resources.configuration
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp
    return screenWidthDp > screenHeightDp
}