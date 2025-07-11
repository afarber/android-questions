package com.wordsbyfarber.data.repository

// Repository handling dictionary operations including download, parsing, and database access
import android.content.Context
import com.wordsbyfarber.data.database.PlayerEntity
import com.wordsbyfarber.data.database.WordEntity
import com.wordsbyfarber.data.database.WordsDatabase
import com.wordsbyfarber.data.models.Language
import com.wordsbyfarber.data.network.DictionaryDownloader
import com.wordsbyfarber.data.network.DictionaryParser
import com.wordsbyfarber.data.network.DownloadResult
import com.wordsbyfarber.data.network.ParseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DictionaryRepository(
    private val context: Context,
    private val downloader: DictionaryDownloader,
    private val parser: DictionaryParser
) {
    
    private fun getDatabase(languageCode: String): WordsDatabase {
        return WordsDatabase.getDatabase(context, languageCode)
    }
    
    fun getLanguages(): List<Language> {
        return Language.getAllLanguages()
    }
    
    fun getLanguage(languageCode: String): Language? {
        return Language.getLanguage(languageCode)
    }
    
    suspend fun getWordCount(languageCode: String): Int {
        return getDatabase(languageCode).wordDao().getWordCount()
    }
    
    suspend fun hasMinWords(languageCode: String): Boolean {
        val language = getLanguage(languageCode) ?: return false
        val wordCount = getWordCount(languageCode)
        return wordCount >= language.minWords
    }
    
    suspend fun clearWordsTable(languageCode: String) {
        getDatabase(languageCode).wordDao().deleteAllWords()
    }
    
    fun downloadAndParseDictionary(languageCode: String): Flow<DictionaryDownloadState> = flow {
        val language = getLanguage(languageCode)
        if (language == null) {
            emit(DictionaryDownloadState.Error("Language not supported: $languageCode"))
            return@flow
        }
        
        emit(DictionaryDownloadState.Downloading(0))
        
        downloader.downloadDictionary(language.hashedDictionaryUrl).collect { downloadResult ->
            when (downloadResult) {
                is DownloadResult.Loading -> {
                    emit(DictionaryDownloadState.Downloading(downloadResult.progress))
                }
                is DownloadResult.Success -> {
                    emit(DictionaryDownloadState.Parsing(0))
                    
                    parser.parseJavaScriptDictionary(downloadResult.content) { progress ->
                        // Progress callback handled in flow
                    }.collect { parseResult ->
                        when (parseResult) {
                            is ParseResult.Loading -> {
                                emit(DictionaryDownloadState.Parsing(parseResult.progress))
                            }
                            is ParseResult.Success -> {
                                // Store words in database
                                getDatabase(languageCode).wordDao().insertWords(parseResult.words)
                                emit(DictionaryDownloadState.Success(parseResult.words.size))
                            }
                            is ParseResult.Error -> {
                                emit(DictionaryDownloadState.Error(parseResult.message))
                            }
                        }
                    }
                }
                is DownloadResult.Error -> {
                    emit(DictionaryDownloadState.Error(downloadResult.message))
                }
            }
        }
    }
    
    fun searchWords(languageCode: String, query: String): Flow<List<WordEntity>> {
        return getDatabase(languageCode).wordDao().searchWords(query)
    }
    
    fun getWordsByLength(languageCode: String, length: Int): Flow<List<WordEntity>> {
        return getDatabase(languageCode).wordDao().getWordsByLength(length)
    }
    
    fun getWordsByRareLetter(languageCode: String, letter: String): Flow<List<WordEntity>> {
        return getDatabase(languageCode).wordDao().getWordsByRareLetter(letter)
    }
    
    suspend fun findWord(languageCode: String, word: String): WordEntity? {
        return getDatabase(languageCode).wordDao().findWord(word)
    }
    
    // Player-related methods
    suspend fun insertPlayers(languageCode: String, players: List<PlayerEntity>) {
        getDatabase(languageCode).playerDao().insertPlayers(players)
    }
    
    fun getAllPlayers(languageCode: String): Flow<List<PlayerEntity>> {
        return getDatabase(languageCode).playerDao().getAllPlayers()
    }
    
    fun searchPlayers(languageCode: String, query: String): Flow<List<PlayerEntity>> {
        return getDatabase(languageCode).playerDao().searchPlayers(query)
    }
    
    suspend fun getPlayerById(languageCode: String, uid: Int): PlayerEntity? {
        return getDatabase(languageCode).playerDao().getPlayerById(uid)
    }
    
    suspend fun clearPlayersTable(languageCode: String) {
        getDatabase(languageCode).playerDao().deleteAllPlayers()
    }
}

sealed class DictionaryDownloadState {
    data class Downloading(val progress: Int) : DictionaryDownloadState()
    data class Parsing(val progress: Int) : DictionaryDownloadState()
    data class Success(val wordCount: Int) : DictionaryDownloadState()
    data class Error(val message: String) : DictionaryDownloadState()
}