package co.edu.uniqundio.tupataamiga.ui.theme

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

object LocaleManager {
    var currentLanguage = mutableStateOf("es")

    fun isEnglish(): Boolean = currentLanguage.value == "en"

    fun tr(es: String, en: String): String = if (currentLanguage.value == "en") en else es

    fun setLanguage(context: Context, languageCode: String) {
        currentLanguage.value = languageCode
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val prefs = context.getSharedPreferences("locale_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("language_code", languageCode).apply()

        val resources = context.resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun initLocale(context: Context) {
        val prefs = context.getSharedPreferences("locale_prefs", Context.MODE_PRIVATE)
        val savedLang = prefs.getString("language_code", "es") ?: "es"
        currentLanguage.value = savedLang

        val locale = Locale(savedLang)
        Locale.setDefault(locale)
        val resources = context.resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    @Composable
    fun LocalizedContextWrapper(content: @Composable () -> Unit) {
        val context = LocalContext.current
        val lang = currentLanguage.value

        val localizedConfig = remember(lang) {
            val locale = Locale(lang)
            Locale.setDefault(locale)
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            config
        }

        CompositionLocalProvider(LocalConfiguration provides localizedConfig) {
            content()
        }
    }
}
