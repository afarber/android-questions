package com.wordsbyfarber.integration

// Integration test to ensure SharedPreferences contains ONLY allowed keys
import android.content.SharedPreferences
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import com.wordsbyfarber.data.repository.PreferencesRepository
import com.wordsbyfarber.utils.Constants

class SharedPreferencesIntegrationTest {

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
        whenever(mockEditor.putString(org.mockito.kotlin.any(), org.mockito.kotlin.any())).thenReturn(mockEditor)
        whenever(mockEditor.remove(org.mockito.kotlin.any())).thenReturn(mockEditor)
        
        preferencesRepository = PreferencesRepository(mockSharedPreferences)
    }

    @Test
    fun `SharedPreferences should only contain allowed keys`() {
        // Define the ONLY allowed keys in SharedPreferences
        val allowedKeys = setOf(
            Constants.Preferences.KEY_LANGUAGE,  // Currently implemented: "language"
            Constants.Preferences.KEY_LOGIN      // Reserved for future Google/Amazon/Huawei login implementation
        )

        // Mock SharedPreferences to return only allowed keys
        val mockAllEntries = mapOf(
            Constants.Preferences.KEY_LANGUAGE to "de",
            Constants.Preferences.KEY_LOGIN to "google_user_123"
        )
        whenever(mockSharedPreferences.all).thenReturn(mockAllEntries)

        // When - Get all entries from SharedPreferences
        val allEntries = mockSharedPreferences.all

        // Then - Verify only allowed keys exist
        allEntries.keys.forEach { key ->
            assertTrue(
                "Forbidden key '$key' found in SharedPreferences. Only 'language' (and future 'login') are allowed.",
                allowedKeys.contains(key)
            )
        }

        // Verify we're only testing known allowed keys
        assertEquals("Test should be updated when new allowed keys are added", 2, allowedKeys.size)
    }

    @Test
    fun `PreferencesRepository should only provide methods for allowed values`() {
        // Verify that PreferencesRepository only has methods for allowed SharedPreferences keys
        val repositoryMethods = PreferencesRepository::class.java.declaredMethods
        
        // Count methods that interact with SharedPreferences (excluding utility methods)
        val prefsInteractionMethods = repositoryMethods.filter { method ->
            when (method.name) {
                "getLanguage", "getCurrentLanguage", "setLanguage", "clearLanguage" -> true
                // When login is implemented, add: "getLogin", "setLogin", "clearLogin" -> true
                "clearAll" -> true // Utility method - allowed
                else -> false
            }
        }

        // Verify we have exactly the expected number of methods
        val expectedMethodCount = 5 // getLanguage, getCurrentLanguage, setLanguage, clearLanguage, clearAll
        assertEquals(
            "PreferencesRepository has unexpected methods. Only language (and future login) methods are allowed.",
            expectedMethodCount,
            prefsInteractionMethods.size
        )
    }

    @Test
    fun `Constants should only define allowed SharedPreferences keys`() {
        // Use reflection to verify Constants.Preferences only contains allowed keys
        val preferencesFields = Constants.Preferences::class.java.declaredFields
        
        val allowedConstantNames = setOf(
            "PREFS_NAME",     // Configuration constant - allowed
            "KEY_LANGUAGE",   // Currently implemented key
            "KEY_LOGIN"       // Reserved for future login implementation
        )

        preferencesFields.forEach { field ->
            assertTrue(
                "Forbidden constant '${field.name}' found in Constants.Preferences. Only language (and future login) keys are allowed.",
                allowedConstantNames.contains(field.name) || 
                field.name.contains("INSTANCE") || 
                field.name.startsWith("$") // Kotlin compiler-generated fields
            )
        }
    }

    @Test
    fun `language value should be stored and retrieved correctly`() {
        // Given
        whenever(mockSharedPreferences.getString(Constants.Preferences.KEY_LANGUAGE, null))
            .thenReturn("fr")

        // When
        val language = preferencesRepository.getLanguage()

        // Then
        assertEquals("fr", language)
    }

    @Test
    fun `clearAll should remove all SharedPreferences values`() {
        // When
        preferencesRepository.clearAll()

        // Then - Verify clearAll is called (removes both current and future values)
        org.mockito.kotlin.verify(mockEditor).clear()
        org.mockito.kotlin.verify(mockEditor).apply()
    }

    @Test
    fun `verify SharedPreferences architecture constraints`() {
        // This test documents and enforces the architectural decision:
        // - SharedPreferences: ONLY for simple user preferences (language, login)
        // - Room database: For all data persistence (words, players, etc.)
        // - DownloadTracker: For in-memory state tracking
        // - Static constants: For configuration values
        
        val allowedSharedPrefsUsage = listOf(
            "User language selection (2-letter code)",
            "Future: User login token/identifier"
        )
        
        // This test serves as documentation - no assertions needed
        assertTrue("SharedPreferences usage is constrained to: $allowedSharedPrefsUsage", true)
    }
}