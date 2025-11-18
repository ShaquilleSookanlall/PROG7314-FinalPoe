package com.st10140587.prog7314_poe

import android.content.Context
import java.util.Locale

object LocaleUtils {

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_LANG_CODE = "lang_code"

    // Get the saved language code, default "en"
    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANG_CODE, "en") ?: "en"
    }

    // Save the selected language code
    fun saveLanguage(context: Context, langCode: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANG_CODE, langCode).apply()
    }

    // Wrap a context so that its resources use the saved language
    fun wrapContext(base: Context): Context {
        val code = getSavedLanguage(base)
        return updateBaseContext(base, code)
    }

    // Create a configuration context for a specific language
    fun updateBaseContext(base: Context, langCode: String): Context {
        val locale = Locale(langCode)
        Locale.setDefault(locale)

        val config = base.resources.configuration
        config.setLocale(locale)
        // If you want RTL support later, you could also setLayoutDirection(locale)

        return base.createConfigurationContext(config)
    }
}
