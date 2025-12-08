package com.wordsbyfarber.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordsbyfarber.data.model.Language
import com.wordsbyfarber.data.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class DictionaryViewModel(
    private val repository: DictionaryRepository
) : ViewModel() {

    var selectedLanguage: Language? = null
        private set

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState

    fun selectLanguage(language: Language) {
        selectedLanguage = language
        _downloadState.value = DownloadState.Idle
    }

    fun downloadDictionary() {
        val language = selectedLanguage ?: return
        viewModelScope.launch {
            _downloadState.value = DownloadState.Loading
            repository.downloadAndStoreDictionary(language)
                .onSuccess { _downloadState.value = DownloadState.Success }
                .onFailure { _downloadState.value = DownloadState.Error(it.message ?: "Unknown error") }
        }
    }

    fun getWordCount(length: Int): Flow<Int> {
        val code = selectedLanguage?.code ?: return flowOf(0)
        return repository.getWordCountByLength(code, length)
    }
}

sealed class DownloadState {
    data object Idle : DownloadState()
    data object Loading : DownloadState()
    data object Success : DownloadState()
    data class Error(val message: String) : DownloadState()
}
