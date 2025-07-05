package com.wordsbyfarber.domain.usecases

import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import com.wordsbyfarber.domain.models.LanguageInfo
import javax.inject.Inject

class GetLanguagesUseCase @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): List<LanguageInfo> {
        val languages = dictionaryRepository.getLanguages()
        val selectedLanguage = preferencesRepository.getSelectedLanguage()
        
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