package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences

class UserPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("labmaster_user_prefs", Context.MODE_PRIVATE)

    fun getGeminiApiKey(): String {
        return prefs.getString("gemini_api_key", "") ?: ""
    }

    fun saveGeminiApiKey(key: String) {
        prefs.edit().putString("gemini_api_key", key.trim()).apply()
    }

    fun clearGeminiApiKey() {
        prefs.edit().remove("gemini_api_key").apply()
    }

    fun isDarkThemeDefault(): Boolean {
        return prefs.getBoolean("is_dark_theme", true) // Dark theme is default
    }

    fun setDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean("is_dark_theme", isDark).apply()
    }
}
