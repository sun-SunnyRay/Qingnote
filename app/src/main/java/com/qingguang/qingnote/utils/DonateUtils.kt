package com.qingguang.qingnote.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.qingguang.qingnote.App
import com.qingguang.qingnote.R


object DonateUtils {

    /***
     * 支付宝转账
     */
    fun openALiPay(activity: Context) {
        val url1 =
            "intent://platformapi/startapp?saId=10000007&" + "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2Fa6x01470fhxkoehbej8yj77%3F_s" + "%3Dweb-other&_t=1472443966571#Intent;" + "scheme=alipayqr;package=com.eg.android.AlipayGphone;end"
        val intent: Intent?
        try {
            intent = Intent.parseUri(url1, Intent.URI_INTENT_SCHEME)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(App.instance, activity.getString(R.string.failed), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 跳转google play
     */
    fun openGooglePlay(context: Context) {
        val playPackage = "com.android.vending"
        try {
            val currentPackageName = context.packageName
            if (currentPackageName != null) {
                val currentPackageUri = Uri.parse("market://details?id=" + context.packageName)
                val intent = Intent(Intent.ACTION_VIEW, currentPackageUri)
                intent.setPackage(playPackage)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            val currentPackageUri = Uri.parse("https://play.google.com/store/apps/details?id=" + context.packageName)
            val intent = Intent(Intent.ACTION_VIEW, currentPackageUri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}