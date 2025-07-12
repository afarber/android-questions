package com.wordsbyfarber.ui.viewmodels

// Unit tests for LanguageSelectionViewModel to verify language selection and SharedPreferences storage
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
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import com.wordsbyfarber.data.models.Language

@ExperimentalCoroutinesApi
class LanguageSelectionViewModelTest {

    @Mock
    private lateinit var mockDictionaryRepository: DictionaryRepository

    @Mock
    private lateinit var mockPreferencesRepository: PreferencesRepository

    private lateinit var viewModel: LanguageSelectionViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Setup mock repository responses
        whenever(mockDictionaryRepository.getLanguages()).thenReturn(
            listOf(
                Language.getLanguage("de")!!,
                Language.getLanguage("en")!!,
                Language.getLanguage("fr")!!
            )
        )
        
        viewModel = LanguageSelectionViewModel(mockDictionaryRepository, mockPreferencesRepository)
    }

    @Test
    fun `loadLanguages should populate languages list`() = testScope.runTest {
        // When the ViewModel is initialized, languages should be loaded
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(3, state.languages.size)
        assertTrue(state.languages.any { it.code == "de" })
        assertTrue(state.languages.any { it.code == "en" })
        assertTrue(state.languages.any { it.code == "fr" })
    }

    @Test
    fun `selectLanguage should store language in SharedPreferences`() = testScope.runTest {
        // Given
        val germanLanguage = Language.getLanguage("de")!!
        whenever(mockDictionaryRepository.hasMinWords("de")).thenReturn(false)
        
        // When
        viewModel.selectLanguage(germanLanguage)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        verify(mockPreferencesRepository).setLanguage("de")
        
        val state = viewModel.uiState.value
        assertEquals(germanLanguage, state.selectedLanguage)
        assertFalse(state.isLoading)
    }

    @Test
    fun `selectLanguage with no dictionary should navigate to loading`() = testScope.runTest {
        // Given
        val germanLanguage = Language.getLanguage("de")!!
        whenever(mockDictionaryRepository.hasMinWords("de")).thenReturn(false)
        
        // When
        viewModel.selectLanguage(germanLanguage)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertTrue(state.shouldNavigateToLoading)
        assertFalse(state.shouldNavigateToHome)
        verify(mockPreferencesRepository).setLanguage("de")
    }

    @Test
    fun `selectLanguage with existing dictionary should navigate to home`() = testScope.runTest {
        // Given
        val englishLanguage = Language.getLanguage("en")!!
        whenever(mockDictionaryRepository.hasMinWords("en")).thenReturn(true)
        
        // When
        viewModel.selectLanguage(englishLanguage)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertTrue(state.shouldNavigateToHome)
        assertFalse(state.shouldNavigateToLoading)
        verify(mockPreferencesRepository).setLanguage("en")
    }

    @Test
    fun `clearNavigationState should reset navigation flags`() = testScope.runTest {
        // Given - setup a state with navigation flags set
        val germanLanguage = Language.getLanguage("de")!!
        whenever(mockDictionaryRepository.hasMinWords("de")).thenReturn(false)
        viewModel.selectLanguage(germanLanguage)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.clearNavigationState()
        
        // Then
        val state = viewModel.uiState.value
        assertFalse(state.shouldNavigateToHome)
        assertFalse(state.shouldNavigateToLoading)
    }

    @Test
    fun `selectLanguage should handle repository errors gracefully`() = testScope.runTest {
        // Given
        val germanLanguage = Language.getLanguage("de")!!
        whenever(mockDictionaryRepository.hasMinWords("de")).thenThrow(RuntimeException("Database error"))
        
        // When
        viewModel.selectLanguage(germanLanguage)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("Database error"))
        assertFalse(state.isLoading)
    }

    @Test
    fun `clearError should reset error state`() = testScope.runTest {
        // Given - create an error state
        val germanLanguage = Language.getLanguage("de")!!
        whenever(mockDictionaryRepository.hasMinWords("de")).thenThrow(RuntimeException("Test error"))
        viewModel.selectLanguage(germanLanguage)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.clearError()
        
        // Then
        val state = viewModel.uiState.value
        assertNull(state.error)
    }
}