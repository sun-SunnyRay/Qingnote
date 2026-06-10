package com.qingguang.qingnote.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LanguageUtils {

    fun setLanguage(context: Context, locale: Locale) {
        val localeList = LocaleListCompat.create(locale)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    fun getAppLocale(context: Context): Locale {
        return AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault()
    }
}
