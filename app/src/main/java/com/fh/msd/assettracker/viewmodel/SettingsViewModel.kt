package com.fh.msd.assettracker.viewmodel

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage = _currentLanguage.asStateFlow()

    init {
        // Load persisted values
        val themeMode = sharedPreferences.getString("PREF_THEME_MODE", "light") ?: "light"
        _isDarkMode.value = themeMode == "dark"

        val lang = sharedPreferences.getString("PREF_LANGUAGE", "en") ?: "en"
        _currentLanguage.value = lang
    }

    fun toggleTheme() {
        val newMode = if (_isDarkMode.value) "light" else "dark"
        _isDarkMode.value = !_isDarkMode.value
        sharedPreferences.edit().putString("PREF_THEME_MODE", newMode).apply()
    }

    fun changeLanguage(languageCode: String) {
        if (_currentLanguage.value != languageCode) {
            _currentLanguage.value = languageCode
            sharedPreferences.edit().putString("PREF_LANGUAGE", languageCode).apply()
            
            // Apply language globally using AppCompatDelegate
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }
}
