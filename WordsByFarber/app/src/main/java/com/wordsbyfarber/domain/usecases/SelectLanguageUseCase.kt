package com.wordsbyfarber.domain.usecases

// Use case for selecting a language and determining next navigation step
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository

class SelectLanguageUseCase(
    private val dictionaryRepository: DictionaryRepository,
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(languageCode: String): SelectLanguageResult {
        val supportedLanguages = dictionaryRepository.getLanguages().map { it.code }
        
        if (!supportedLanguages.contains(languageCode)) {
            return SelectLanguageResult.InvalidLanguage
        }
        
        preferencesRepository.setLanguage(languageCode)
        
        val hasMinWords = dictionaryRepository.hasMinWords(languageCode)
        val isDownloadActive = preferencesRepository.getDownloadState(languageCode) != null
        
        return when {
            hasMinWords -> SelectLanguageResult.NavigateToHome
            isDownloadActive -> SelectLanguageResult.NavigateToLoading
            else -> SelectLanguageResult.StartDownload
        }
    }
}

sealed class SelectLanguageResult {
    object InvalidLanguage : SelectLanguageResult()
    object NavigateToHome : SelectLanguageResult()
    object NavigateToLoading : SelectLanguageResult()
    object StartDownload : SelectLanguageResult()
}