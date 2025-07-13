package com.wordsbyfarber.ui

// UI test for clicking "Deutsch / de" and verifying SharedPreferences storage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import com.wordsbyfarber.ui.screens.LanguageSelectionScreen
import com.wordsbyfarber.ui.viewmodels.LanguageSelectionViewModel
import com.wordsbyfarber.data.network.DictionaryDownloader
import com.wordsbyfarber.data.network.DictionaryParser
import com.wordsbyfarber.utils.Constants
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient

@RunWith(AndroidJUnit4::class)
class LanguageSelectionUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var dictionaryRepository: DictionaryRepository
    private lateinit var viewModel: LanguageSelectionViewModel

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Clear any existing preferences
        sharedPreferences = context.getSharedPreferences("test_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        
        // Create real repositories
        preferencesRepository = PreferencesRepository(sharedPreferences)
        
        // Create DictionaryRepository with real dependencies
        val okHttpClient = OkHttpClient()
        val downloader = DictionaryDownloader(okHttpClient)
        val parser = DictionaryParser()
        dictionaryRepository = DictionaryRepository(context, downloader, parser)
        
        // Create ViewModel
        viewModel = LanguageSelectionViewModel(dictionaryRepository, preferencesRepository)
    }

    @Test
    fun clickDeutschLanguageItem_storesDeInSharedPreferences() = runTest {
        // Given - Set up the Compose UI
        composeTestRule.setContent {
            LanguageSelectionScreen(
                viewModel = viewModel,
                onLanguageSelected = { }
            )
        }

        // When - Click on "Deutsch" language item
        composeTestRule
            .onNodeWithText("Deutsch")
            .assertIsDisplayed()
            .performClick()

        // Wait for the action to complete
        composeTestRule.waitForIdle()

        // Then - Verify "de" is stored in SharedPreferences
        val storedLanguage = sharedPreferences.getString(Constants.Preferences.KEY_LANGUAGE, null)
        assertEquals("de", storedLanguage)
    }

    @Test
    fun clickEnglishLanguageItem_storesEnInSharedPreferences() = runTest {
        // Given - Set up the Compose UI
        composeTestRule.setContent {
            LanguageSelectionScreen(
                viewModel = viewModel,
                onLanguageSelected = { }
            )
        }

        // When - Click on "English" language item
        composeTestRule
            .onNodeWithText("English")
            .assertIsDisplayed()
            .performClick()

        // Wait for the action to complete
        composeTestRule.waitForIdle()

        // Then - Verify "en" is stored in SharedPreferences
        val storedLanguage = sharedPreferences.getString(Constants.Preferences.KEY_LANGUAGE, null)
        assertEquals("en", storedLanguage)
    }

    @Test
    fun clickPolskiLanguageItem_storesPlInSharedPreferences() = runTest {
        // Given - Set up the Compose UI
        composeTestRule.setContent {
            LanguageSelectionScreen(
                viewModel = viewModel,
                onLanguageSelected = { }
            )
        }

        // When - Click on "Polski" language item
        composeTestRule
            .onNodeWithText("Polski")
            .assertIsDisplayed()
            .performClick()

        // Wait for the action to complete
        composeTestRule.waitForIdle()

        // Then - Verify "pl" is stored in SharedPreferences
        val storedLanguage = sharedPreferences.getString(Constants.Preferences.KEY_LANGUAGE, null)
        assertEquals("pl", storedLanguage)
    }

    @Test
    fun verifyAllLanguagesAreDisplayed() {
        // Given - Set up the Compose UI
        composeTestRule.setContent {
            LanguageSelectionScreen(
                viewModel = viewModel,
                onLanguageSelected = { }
            )
        }

        // Then - Verify all 6 languages are displayed
        composeTestRule.onNodeWithText("Deutsch").assertIsDisplayed()
        composeTestRule.onNodeWithText("English").assertIsDisplayed()
        composeTestRule.onNodeWithText("Français").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nederlands").assertIsDisplayed()
        composeTestRule.onNodeWithText("Polski").assertIsDisplayed()
        composeTestRule.onNodeWithText("Русский").assertIsDisplayed()
    }

    @Test
    fun verifyLanguageCodesAreDisplayed() {
        // Given - Set up the Compose UI
        composeTestRule.setContent {
            LanguageSelectionScreen(
                viewModel = viewModel,
                onLanguageSelected = { }
            )
        }

        // Then - Verify all language codes are displayed
        composeTestRule.onNodeWithText("de").assertIsDisplayed()
        composeTestRule.onNodeWithText("en").assertIsDisplayed()
        composeTestRule.onNodeWithText("fr").assertIsDisplayed()
        composeTestRule.onNodeWithText("nl").assertIsDisplayed()
        composeTestRule.onNodeWithText("pl").assertIsDisplayed()
        composeTestRule.onNodeWithText("ru").assertIsDisplayed()
    }

    @Test
    fun clickDeutschMultipleTimes_onlyStoresLatestSelection() = runTest {
        // Given - Set up the Compose UI
        composeTestRule.setContent {
            LanguageSelectionScreen(
                viewModel = viewModel,
                onLanguageSelected = { }
            )
        }

        // When - Click Deutsch multiple times
        composeTestRule.onNodeWithText("Deutsch").performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Deutsch").performClick()
        composeTestRule.waitForIdle()

        // Then - Verify final selection is stored
        val storedLanguage = sharedPreferences.getString(Constants.Preferences.KEY_LANGUAGE, null)
        assertEquals("de", storedLanguage)
    }

    @Test
    fun verifyPreferencesRepositoryGetLanguageReturnsStoredValue() = runTest {
        // Given - Set up the Compose UI and click a language
        composeTestRule.setContent {
            LanguageSelectionScreen(
                viewModel = viewModel,
                onLanguageSelected = { }
            )
        }

        composeTestRule.onNodeWithText("Français").performClick()
        composeTestRule.waitForIdle()

        // Then - Verify PreferencesRepository can retrieve the value
        val retrievedLanguage = preferencesRepository.getLanguage()
        assertEquals("fr", retrievedLanguage)
        
        val currentLanguage = preferencesRepository.getCurrentLanguage()
        assertEquals("fr", currentLanguage)
    }
}