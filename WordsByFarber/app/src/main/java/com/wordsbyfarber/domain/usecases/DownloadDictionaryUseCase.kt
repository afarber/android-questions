package com.wordsbyfarber.domain.usecases

import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.DictionaryDownloadState
import com.wordsbyfarber.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class DownloadDictionaryUseCase @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(languageCode: String): Flow<DictionaryDownloadState> {
        return dictionaryRepository.downloadAndParseDictionary(languageCode)
            .onStart {
                preferencesRepository.setDownloadState(languageCode, true)
            }
            .onCompletion { throwable ->
                preferencesRepository.clearDownloadState(languageCode)
                if (throwable != null) {
                    // Handle cancellation by clearing words table
                    dictionaryRepository.clearWordsTable(languageCode)
                }
            }
    }
    
    suspend fun cancelDownload(languageCode: String) {
        preferencesRepository.clearDownloadState(languageCode)
        dictionaryRepository.clearWordsTable(languageCode)
        preferencesRepository.clearSelectedLanguage()
    }
    
    suspend fun handleFailure(languageCode: String) {
        preferencesRepository.clearDownloadState(languageCode)
        dictionaryRepository.clearWordsTable(languageCode)
        // Don't clear selected language on failure, only on cancellation
    }
}