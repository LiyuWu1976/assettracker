package com.fh.msd.assettracker.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.Locale

class LanguageManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    fun setLanguage(language: String) {
        sharedPreferences.edit {
            putString("app_lang", language)
        }
    }

    fun getLanguage(): String {
        return sharedPreferences.getString("app_lang", "en") ?: "en"
    }

    fun applyLanguage(context: Context): Context {
        val language = getLanguage()
        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
