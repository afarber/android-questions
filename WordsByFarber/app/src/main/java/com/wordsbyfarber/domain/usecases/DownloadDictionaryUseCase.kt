package com.wordsbyfarber.domain.usecases

// Use case for managing dictionary download with state tracking and error handling
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.DictionaryDownloadState
import com.wordsbyfarber.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

class DownloadDictionaryUseCase(
    private val dictionaryRepository: DictionaryRepository,
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(languageCode: String): Flow<DictionaryDownloadState> {
        return dictionaryRepository.downloadAndParseDictionary(languageCode)
            .onStart {
                preferencesRepository.setDownloadState(languageCode, "active")
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
        preferencesRepository.clearLanguage()
    }
    
    suspend fun handleFailure(languageCode: String) {
        preferencesRepository.clearDownloadState(languageCode)
        dictionaryRepository.clearWordsTable(languageCode)
        // Don't clear selected language on failure, only on cancellation
    }
}