package com.wordsbyfarber.domain.usecases

// Use case for searching words with different criteria and applying hash algorithm
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.domain.models.WordSearchResult
import com.wordsbyfarber.utils.StringUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchWordsUseCase(
    private val dictionaryRepository: DictionaryRepository
) {
    fun searchWords(languageCode: String, query: String): Flow<List<WordSearchResult>> {
        if (query.isBlank()) {
            return kotlinx.coroutines.flow.flowOf(emptyList())
        }
        
        return dictionaryRepository.searchWords(languageCode, query).map { words ->
            words.map { word ->
                WordSearchResult(
                    word = word.word,
                    explanation = word.explanation,
                    isFound = true
                )
            }
        }
    }
    
    fun getWordsByLength(languageCode: String, length: Int): Flow<List<WordSearchResult>> {
        return dictionaryRepository.getWordsByLength(languageCode, length).map { words ->
            words.map { word ->
                WordSearchResult(
                    word = word.word,
                    explanation = word.explanation,
                    isFound = true
                )
            }
        }
    }
    
    fun getWordsByRareLetter(languageCode: String, letter: String): Flow<List<WordSearchResult>> {
        return dictionaryRepository.getWordsByRareLetter(languageCode, letter).map { words ->
            words.map { word ->
                WordSearchResult(
                    word = word.word,
                    explanation = word.explanation,
                    isFound = true
                )
            }
        }
    }
    
    suspend fun findSingleWord(languageCode: String, inputWord: String): WordSearchResult {
        // Apply the same hashing algorithm as used in the database
        val wordToSearch = StringUtils.hashWord(inputWord)
        val foundWord = dictionaryRepository.findWord(languageCode, wordToSearch)
        
        return if (foundWord != null) {
            WordSearchResult(
                word = foundWord.word,
                explanation = foundWord.explanation,
                isFound = true
            )
        } else {
            WordSearchResult(
                word = inputWord,
                explanation = "",
                isFound = false
            )
        }
    }
}