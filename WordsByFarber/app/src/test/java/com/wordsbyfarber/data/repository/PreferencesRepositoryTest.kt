package com.wordsbyfarber.data.repository

// Unit tests for PreferencesRepository to verify SharedPreferences operations
import android.content.SharedPreferences
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any
import com.wordsbyfarber.utils.Constants

class PreferencesRepositoryTest {

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var preferencesRepository: PreferencesRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        
        // Setup editor mock
        whenever(mockSharedPreferences.edit()).thenReturn(mockEditor)
        whenever(mockEditor.putString(any(), any())).thenReturn(mockEditor)
        whenever(mockEditor.remove(any())).thenReturn(mockEditor)
        
        preferencesRepository = PreferencesRepository(mockSharedPreferences)
    }

    @Test
    fun `setLanguage should store language code in SharedPreferences`() {
        // When
        preferencesRepository.setLanguage("de")
        
        // Then
        verify(mockEditor).putString(Constants.Preferences.KEY_LANGUAGE, "de")
        verify(mockEditor).apply()
    }

    @Test
    fun `getLanguage should return stored language code`() {
        // Given
        whenever(mockSharedPreferences.getString(Constants.Preferences.KEY_LANGUAGE, null))
            .thenReturn("de")
        
        // When
        val result = preferencesRepository.getLanguage()
        
        // Then
        assertEquals("de", result)
        verify(mockSharedPreferences).getString(Constants.Preferences.KEY_LANGUAGE, null)
    }

    @Test
    fun `getLanguage should return null when no language stored`() {
        // Given
        whenever(mockSharedPreferences.getString(Constants.Preferences.KEY_LANGUAGE, null))
            .thenReturn(null)
        
        // When
        val result = preferencesRepository.getLanguage()
        
        // Then
        assertNull(result)
    }

    @Test
    fun `getCurrentLanguage should return same as getLanguage`() {
        // Given
        whenever(mockSharedPreferences.getString(Constants.Preferences.KEY_LANGUAGE, null))
            .thenReturn("en")
        
        // When
        val result = preferencesRepository.getCurrentLanguage()
        
        // Then
        assertEquals("en", result)
    }

    @Test
    fun `clearLanguage should remove language from SharedPreferences`() {
        // When
        preferencesRepository.clearLanguage()
        
        // Then
        verify(mockEditor).remove(Constants.Preferences.KEY_LANGUAGE)
        verify(mockEditor).apply()
    }


    @Test
    fun `setBooleanPreference should store boolean value`() {
        // When
        preferencesRepository.setBooleanPreference("test_key", true)
        
        // Then
        verify(mockEditor).putBoolean("test_key", true)
        verify(mockEditor).apply()
    }

    @Test
    fun `getBooleanPreference should return stored boolean value`() {
        // Given
        whenever(mockSharedPreferences.getBoolean("test_key", false)).thenReturn(true)
        
        // When
        val result = preferencesRepository.getBooleanPreference("test_key")
        
        // Then
        assertTrue(result)
    }

    @Test
    fun `getBooleanPreference should return default value when not found`() {
        // Given
        whenever(mockSharedPreferences.getBoolean("missing_key", false)).thenReturn(false)
        
        // When
        val result = preferencesRepository.getBooleanPreference("missing_key")
        
        // Then
        assertFalse(result)
    }

    @Test
    fun `clearAll should clear all preferences`() {
        // When
        preferencesRepository.clearAll()
        
        // Then
        verify(mockEditor).clear()
        verify(mockEditor).apply()
    }
}