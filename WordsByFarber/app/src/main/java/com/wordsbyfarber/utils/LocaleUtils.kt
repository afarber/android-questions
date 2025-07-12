package com.wordsbyfarber.utils

// Utility class for handling locale switching for UI language changes
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleUtils {
    
    /**
     * Updates the app's locale to the specified language code and returns a new context
     * with the updated configuration.
     * 
     * @param context The current context
     * @param languageCode The 2-letter language code (e.g., "de", "en", "fr")
     * @return A new context with the updated locale configuration
     */
    fun updateLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        
        return context.createConfigurationContext(configuration)
    }
    
    /**
     * Gets the current locale's language code
     * 
     * @param context The current context
     * @return The 2-letter language code of the current locale
     */
    fun getCurrentLanguageCode(context: Context): String {
        return context.resources.configuration.locales[0].language
    }
    
    /**
     * Checks if the given language code is supported by the app
     * 
     * @param languageCode The language code to check
     * @return True if the language is supported, false otherwise
     */
    fun isSupportedLanguage(languageCode: String): Boolean {
        return com.wordsbyfarber.data.models.Language.SUPPORTED_LANGUAGE_CODES.contains(languageCode)
    }
}