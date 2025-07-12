package com.wordsbyfarber.integration

// Integration test for the complete language selection flow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlinx.coroutines.Dispatchers
import android.content.SharedPreferences
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import com.wordsbyfarber.ui.viewmodels.LanguageSelectionViewModel
import com.wordsbyfarber.data.models.Language
import com.wordsbyfarber.utils.Constants

@ExperimentalCoroutinesApi
class LanguageSelectionIntegrationTest {

    @Mock
    private lateinit var mockDictionaryRepository: DictionaryRepository

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var viewModel: LanguageSelectionViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Setup SharedPreferences mocks
        whenever(mockSharedPreferences.edit()).thenReturn(mockEditor)
        whenever(mockEditor.putString(org.mockito.kotlin.any(), org.mockito.kotlin.any())).thenReturn(mockEditor)
        whenever(mockEditor.remove(org.mockito.kotlin.any())).thenReturn(mockEditor)
        
        // Setup DictionaryRepository mocks
        whenever(mockDictionaryRepository.getLanguages()).thenReturn(Language.getAllLanguages())
        
        // Create real PreferencesRepository with mocked SharedPreferences
        preferencesRepository = PreferencesRepository(mockSharedPreferences)
        
        // Create ViewModel with mocked dependencies
        viewModel = LanguageSelectionViewModel(mockDictionaryRepository, preferencesRepository)
    }

    @Test
    fun `complete German language selection flow with no existing dictionary`() = testScope.runTest {
        // Given
        val germanLanguage = Language.getLanguage("de")!!
        whenever(mockDictionaryRepository.hasMinWords("de")).thenReturn(false)
        whenever(mockDictionaryRepository.getWordCount("de")).thenReturn(0)
        
        // When - User clicks "Deutsch / de" language item
        viewModel.selectLanguage(germanLanguage)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - Verify the complete flow
        
        // 1. Language should be stored in SharedPreferences
        verify(mockEditor).putString(Constants.Preferences.KEY_LANGUAGE, "de")
        verify(mockEditor).apply()
        
        // 2. ViewModel state should be updated correctly
        val state = viewModel.uiState.value
        assertEquals("de", state.selectedLanguage?.code)
        assertEquals("Deutsch", state.selectedLanguage?.name)
        assertEquals("Q", state.selectedLanguage?.rareLetter1)
        assertEquals("Y", state.selectedLanguage?.rareLetter2)
        assertEquals(180_000, state.selectedLanguage?.minWords)
        assertEquals("https://wordsbyfarber.com/Consts-de.js", state.selectedLanguage?.hashedDictionaryUrl)
        
        // 3. Should navigate to loading screen (not home) since no dictionary exists
        assertTrue(state.shouldNavigateToLoading)
        assertFalse(state.shouldNavigateToHome)
        
        // 4. Should not be in loading state anymore
        assertFalse(state.isLoading)
        
        // 5. Should have no errors
        assertNull(state.error)
        
        // 6. Repository methods should be called
        verify(mockDictionaryRepository).hasMinWords("de")
    }

    @Test
    fun `complete English language selection flow with existing dictionary`() = testScope.runTest {
        // Given
        val englishLanguage = Language.getLanguage("en")!!
        whenever(mockDictionaryRepository.hasMinWords("en")).thenReturn(true)
        whenever(mockDictionaryRepository.getWordCount("en")).thenReturn(300_000)
        
        // When - User clicks "English / en" language item
        viewModel.selectLanguage(englishLanguage)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - Verify the complete flow
        
        // 1. Language should be stored in SharedPreferences
        verify(mockEditor).putString(Constants.Preferences.KEY_LANGUAGE, "en")
        verify(mockEditor).apply()
        
        // 2. ViewModel state should be updated correctly
        val state = viewModel.uiState.value
        assertEquals("en", state.selectedLanguage?.code)
        assertEquals("English", state.selectedLanguage?.name)
        assertEquals("Q", state.selectedLanguage?.rareLetter1)
        assertEquals("X", state.selectedLanguage?.rareLetter2)
        assertEquals(270_000, state.selectedLanguage?.minWords)
        
        // 3. Should navigate to home screen (not loading) since dictionary exists
        assertTrue(state.shouldNavigateToHome)
        assertFalse(state.shouldNavigateToLoading)
        
        // 4. Repository methods should be called
        verify(mockDictionaryRepository).hasMinWords("en")
    }

    @Test
    fun `language selection with repository error should handle gracefully`() = testScope.runTest {
        // Given
        val polishLanguage = Language.getLanguage("pl")!!
        whenever(mockDictionaryRepository.hasMinWords("pl"))
            .thenThrow(RuntimeException("Database connection failed"))
        
        // When - User clicks "Polski / pl" language item
        viewModel.selectLanguage(polishLanguage)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - Verify error handling
        
        // 1. Language should still be stored in SharedPreferences (before error occurred)
        verify(mockEditor).putString(Constants.Preferences.KEY_LANGUAGE, "pl")
        
        // 2. Error should be captured in state
        val state = viewModel.uiState.value
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("Database connection failed"))
        
        // 3. Should not navigate anywhere due to error
        assertFalse(state.shouldNavigateToHome)
        assertFalse(state.shouldNavigateToLoading)
        
        // 4. Should not be in loading state
        assertFalse(state.isLoading)
    }

    @Test
    fun `navigation state clearing should reset flags`() = testScope.runTest {
        // Given - Complete a successful language selection first
        val frenchLanguage = Language.getLanguage("fr")!!
        whenever(mockDictionaryRepository.hasMinWords("fr")).thenReturn(false)
        viewModel.selectLanguage(frenchLanguage)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verify navigation flag is set
        assertTrue(viewModel.uiState.value.shouldNavigateToLoading)
        
        // When - Clear navigation state (simulating navigation completion)
        viewModel.clearNavigationState()
        
        // Then - Navigation flags should be reset
        val state = viewModel.uiState.value
        assertFalse(state.shouldNavigateToHome)
        assertFalse(state.shouldNavigateToLoading)
        
        // But other state should remain
        assertEquals("fr", state.selectedLanguage?.code)
    }

    @Test
    fun `verify static language configuration is used correctly`() = testScope.runTest {
        // Given - Load all languages
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - Get the languages from ViewModel
        val state = viewModel.uiState.value
        
        // Then - Verify all 6 languages are loaded with correct static configuration
        assertEquals(6, state.languages.size)
        
        val germanLanguage = state.languages.find { it.code == "de" }!!
        assertEquals("Deutsch", germanLanguage.name)
        assertEquals("Q", germanLanguage.rareLetter1)
        assertEquals("Y", germanLanguage.rareLetter2)
        assertEquals(180_000, germanLanguage.minWords)
        assertEquals("https://wordsbyfarber.com/Consts-de.js", germanLanguage.hashedDictionaryUrl)
        assertEquals("https://wordsbyfarber.com/de/top-all", germanLanguage.topUrl)
        assertEquals(5, germanLanguage.myUid)
        
        val polishLanguage = state.languages.find { it.code == "pl" }!!
        assertEquals("Polski", polishLanguage.name)
        assertEquals("Ń", polishLanguage.rareLetter1)
        assertEquals("Ź", polishLanguage.rareLetter2)
        assertEquals(3_000_000, polishLanguage.minWords)
        
        val russianLanguage = state.languages.find { it.code == "ru" }!!
        assertEquals("Русский", russianLanguage.name)
        assertEquals("Ъ", russianLanguage.rareLetter1)
        assertEquals("Э", russianLanguage.rareLetter2)
        assertEquals(120_000, russianLanguage.minWords)
    }
}