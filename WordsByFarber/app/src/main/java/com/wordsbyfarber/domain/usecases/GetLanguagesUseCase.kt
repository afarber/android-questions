package com.wordsbyfarber.domain.usecases

// Use case for retrieving supported languages with selection state
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import com.wordsbyfarber.domain.models.LanguageInfo

class GetLanguagesUseCase(
    private val dictionaryRepository: DictionaryRepository,
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): List<LanguageInfo> {
        val languages = dictionaryRepository.getLanguages()
        val selectedLanguage = preferencesRepository.getLanguage()
        
        return languages.map { language ->
            LanguageInfo(
                code = language.code,
                displayName = language.name,
                flagIcon = "${language.code}_flag", // Resource identifier
                isSelected = language.code == selectedLanguage
            )
        }
    }
}