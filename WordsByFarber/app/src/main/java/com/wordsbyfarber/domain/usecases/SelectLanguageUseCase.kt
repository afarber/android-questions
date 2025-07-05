package com.wordsbyfarber.domain.usecases

import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import javax.inject.Inject

class SelectLanguageUseCase @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(languageCode: String): SelectLanguageResult {
        val supportedLanguages = dictionaryRepository.getLanguages().map { it.code }
        
        if (!supportedLanguages.contains(languageCode)) {
            return SelectLanguageResult.InvalidLanguage
        }
        
        preferencesRepository.setSelectedLanguage(languageCode)
        
        val hasMinWords = dictionaryRepository.hasMinWords(languageCode)
        val isDownloadActive = preferencesRepository.isDownloadActive(languageCode)
        
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