package com.wordsbyfarber

import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import com.wordsbyfarber.utils.NetworkUtils
import com.wordsbyfarber.utils.StringUtils
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

/**
 * This class validates that Hilt dependency injection is working correctly.
 * It injects all the main dependencies to verify the modules are properly configured.
 */
@ActivityScoped
class HiltDependencyValidation @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    private val preferencesRepository: PreferencesRepository,
    private val networkUtils: NetworkUtils,
    private val stringUtils: StringUtils
) {

    fun validateDependencies(): HiltValidationResult {
        return try {
            // Test DictionaryRepository
            val languages = dictionaryRepository.getLanguages()
            val hasLanguages = languages.isNotEmpty()

            // Test PreferencesRepository
            val currentLanguage = preferencesRepository.getLanguage()
            val prefsWorking = true // If no exception thrown, it's working

            // Test NetworkUtils
            val networkAvailable = networkUtils.isNetworkAvailable()
            val networkWorking = true // If no exception thrown, it's working

            // Test StringUtils
            val hashedWord = stringUtils.hashWord("TEST")
            val stringUtilsWorking = hashedWord.isNotEmpty()

            HiltValidationResult(
                success = true,
                dictionaryRepositoryWorking = hasLanguages,
                preferencesRepositoryWorking = prefsWorking,
                networkUtilsWorking = networkWorking,
                stringUtilsWorking = stringUtilsWorking,
                details = "All dependencies injected successfully. " +
                        "Languages: ${languages.size}, " +
                        "Network: $networkAvailable, " +
                        "Current language: $currentLanguage"
            )
        } catch (e: Exception) {
            HiltValidationResult(
                success = false,
                dictionaryRepositoryWorking = false,
                preferencesRepositoryWorking = false,
                networkUtilsWorking = false,
                stringUtilsWorking = false,
                details = "Dependency injection failed: ${e.message}"
            )
        }
    }
}

data class HiltValidationResult(
    val success: Boolean,
    val dictionaryRepositoryWorking: Boolean,
    val preferencesRepositoryWorking: Boolean,
    val networkUtilsWorking: Boolean,
    val stringUtilsWorking: Boolean,
    val details: String
)