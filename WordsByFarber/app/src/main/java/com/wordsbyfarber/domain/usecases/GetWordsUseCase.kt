package com.wordsbyfarber.domain.usecases

import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import com.wordsbyfarber.domain.models.WordSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetWordsUseCase @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    private val preferencesRepository: PreferencesRepository
) {
    fun getFilteredWords(
        searchQuery: String,
        wordType: WordType
    ): Flow<List<WordSearchResult>> {
        val languageCode = preferencesRepository.getSelectedLanguage()
            ?: return flowOf(emptyList())
        
        return when (wordType) {
            WordType.ALL -> {
                if (searchQuery.isBlank()) {
                    flowOf(emptyList())
                } else {
                    dictionaryRepository.searchWords(languageCode, searchQuery)
                        .map { words -> words.map { WordSearchResult(it.word, it.explanation, true) } }
                }
            }
            WordType.TWO_LETTER -> {
                val words = dictionaryRepository.getWordsByLength(languageCode, 2)
                if (searchQuery.isBlank()) {
                    words.map { wordList -> wordList.map { WordSearchResult(it.word, it.explanation, true) } }
                } else {
                    words.map { wordList -> 
                        wordList.filter { it.word.contains(searchQuery, ignoreCase = true) }
                            .map { WordSearchResult(it.word, it.explanation, true) }
                    }
                }
            }
            WordType.THREE_LETTER -> {
                val words = dictionaryRepository.getWordsByLength(languageCode, 3)
                if (searchQuery.isBlank()) {
                    words.map { wordList -> wordList.map { WordSearchResult(it.word, it.explanation, true) } }
                } else {
                    words.map { wordList -> 
                        wordList.filter { it.word.contains(searchQuery, ignoreCase = true) }
                            .map { WordSearchResult(it.word, it.explanation, true) }
                    }
                }
            }
            is WordType.RARE_LETTER -> {
                val words = dictionaryRepository.getWordsByRareLetter(languageCode, wordType.letter)
                if (searchQuery.isBlank()) {
                    words.map { wordList -> wordList.map { WordSearchResult(it.word, it.explanation, true) } }
                } else {
                    words.map { wordList -> 
                        wordList.filter { it.word.contains(searchQuery, ignoreCase = true) }
                            .map { WordSearchResult(it.word, it.explanation, true) }
                    }
                }
            }
        }
    }
}

sealed class WordType {
    object ALL : WordType()
    object TWO_LETTER : WordType()
    object THREE_LETTER : WordType()
    data class RARE_LETTER(val letter: String) : WordType()
}