package com.cross19xx.filmnest.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.cross19xx.filmnest.data.model.AppLanguage

object LocaleUtils {

    fun setLanguage(language: AppLanguage) {
        val locales = if (language.tag == null) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(language.tag)
        }

        AppCompatDelegate.setApplicationLocales(locales)
    }

    fun getCurrentLanguage(): AppLanguage {
        val tag = AppCompatDelegate.getApplicationLocales()[0]?.language
        return AppLanguage.entries.find { it.tag == tag } ?: AppLanguage.SYSTEM
    }
}
