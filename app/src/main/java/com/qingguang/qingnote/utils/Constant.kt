package com.qingguang.qingnote.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object Constant {

    val USER_AGREEMENT = "https://www.freeprivacypolicy.com/live/77fb28fd-1c21-4a6c-8c8b-1464c314d629"
    val PRIVACY_POLICY = "https://www.freeprivacypolicy.com/live/07870fcd-c545-4b1c-9490-4d6de2d8bb5c"
    val GITHUB_RELEASE = "https://github.com/ldlywt/QingNote/releases"
    val GITHUB_URL = "https://github.com/ldlywt/QingNote"
    val GITHUB_FEEDBACK_URL = "https://github.com/ldlywt/QingNote/issues/2"

    const val JIANGUOYUN_URL = "https://dav.jianguoyun.com/dav/"

    val PHOTO_EXTENSIONS = arrayOf(".jpg", ".png", ".jpeg", ".bmp", ".webp", ".heic", ".heif", ".apng", ".avif", ".gif")
    val VIDEO_EXTENSIONS = arrayOf(".mp4", ".mkv", ".webm", ".avi", ".3gp", ".mov", ".m4v", ".3gpp")
    val AUDIO_EXTENSIONS = arrayOf(".mp3", ".wav", ".wma", ".ogg", ".m4a", ".opus", ".flac", ".aac")

    fun startUserAgreeUrl(context: Context) {
        val uri: Uri = Uri.parse(USER_AGREEMENT)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent)
    }

    fun startPrivacyUrl(context: Context) {
        val uri: Uri = Uri.parse(PRIVACY_POLICY)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent)
    }

    fun startGithubReleaseUrl(context: Context) {
        val uri: Uri = Uri.parse(GITHUB_RELEASE)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent)
    }

    fun startGithubUrl(context: Context) {
        val uri: Uri = Uri.parse(GITHUB_URL)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent)
    }
}

fun Context.openUrl(url: String) {
    val uri: Uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent)

}