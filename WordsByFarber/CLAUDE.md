# A word game stub for 6 languages

This app is only a general word game stub, without any gameplay implemented.

Instead of real games it will display 2 different static grids to symbolize 2 types of word games, which I will implement later.

## Supported languages

- de
- en
- fr
- nl
- pl
- ru

The dictionary of each language should be stored in a separate Room database

A simple Kotlin app with Jetpack Compose, where most screens have the following UI structure:

- a screen title and a button on the top
- a search input text field is displayed below, but only in some screens
- the rest of the screen is occupied by a (filtered, if search field is non-empty) list

Initially the Room is empty and a list of 6 languages is displayed (Screen 1):

| `language` | `rare_letter_1` | `rare_letter_2` | `hashed_dictionary_url`                | `min_words` |
| ---------- | --------------- | --------------- | -------------------------------------- | ----------- |
| de         | Q               | Y               | https://wordsbyfarber.com/Consts-de.js | 180_000     |
| en         | Q               | X               | https://wordsbyfarber.com/Consts-en.js | 270_000     |
| fr         | K               | W               | https://wordsbyfarber.com/Consts-fr.js | 370_000     |
| nl         | Q               | X               | https://wordsbyfarber.com/Consts-nl.js | 130_000     |
| pl         | Ń               | Ź               | https://wordsbyfarber.com/Consts-pl.js | 3_000_000   |
| ru         | Ъ               | Э               | https://wordsbyfarber.com/Consts-ru.js | 120_000     |

When the user selects a language in Screen 1, the selected language is stored as `language` in shared preferences and download and parsing of Consts-{de,en,fr,nl,pl,ru}.js begins and Screen 2 is displayed.

# Dictionaries

- Dictionaries are obfuscated to prevent other developers from copying them
- The dictionaries are maps, with a word (min 2 letters; in clear text or hashed) being the dictionary key and an optional word explanation in clear text (could be an empty string too) being the dictionary value
- Words containing rare letter 1 or rare letter 2 are in clear text
- Short words consisting only of 2 or 3 letters are in clear text
- All other words are hashed using `ECHO` as salt and a substring of MD5 as shown below

```sql
CREATE OR REPLACE FUNCTION words_hash(in_word text)
        RETURNS text AS
$func$
        SELECT CASE WHEN
                LENGTH(in_word) > 3 AND
                in_word !~ 'Ń' AND
                in_word !~ 'Ź'
                -- prepend salt and only take the first 16 chars of MD5
                THEN LEFT(MD5('ECHO' || in_word), 16)
                ELSE in_word
                END;
$func$ LANGUAGE sql IMMUTABLE;
```

The `ru` dictionary is permanently stored at [https://wordsbyfarber.com/Consts-ru.js](https://wordsbyfarber.com/Consts-ru.js) as `HASHED` variable (same for other languages):

```javascript
const HASHED = {
  "e41cb5fe71388d82": "Лещина, лесной орешник",
  "a9fcf410c2b5d832": "",
  "9875fce1352a0c6b": "Растение семейства миртовые",
  "5f90b2bcd00059ba": "",
  "d59e22f04156cd76": "",
  "70e159c45a494e2a": "Разновидность домашней одежды",
  "fa219f71cb145b14": "",
  "07b57c456007538f": "Изготовление основы ткани",
  "...": "...",
  "___LANG___": "ru"
};
```

# Actions to perform when downloading or parsing dictionary (DownloadDictionary)

- The Consts-{de,en,fr,nl,pl,ru}.js files are quite big, so they should be downloaded and parsed in chunks to save work memory
- I do not think there is a JSON parsing library which could parse the file being downloaded chunk by chunk, so maybe a regex approach should be used?
- Parsing the dictionary should start when the substring `const HASHED={` has been found
- The key/value pairs (that is word/explanation) should be stored into Room database
- Parsing the dictionary should stop when the closing bracket `};` has been found
- The key entry `___LANG___` should be discarded, it should not be stored into Room
- If possible without wasting too much CPU, the actual number of downloaded and parsed key/value pairs should be available for display in Screen 2

# Actions to perform when dictionary download or parsing fails or user cancels the download (FailureActions)

- Stop any running download
- Delete records from the `words` table
- If the user has canceled, then delete `language` from shared preferences
- If download or parsing have failed, then do not delete `language` from shared preferences

# UI flow

## Screen 1 (Select language)

- Screen 1 is displayed as the very first screen to the user, if there is no valid `language` 2-letter value found in shared preferences
- If there is `language` in shared preferences, but it is not one of the valid values, then `language` is deleted from shared preferences and Screen 1 is displayed
- Also, if the user is at Screen 4 and presses the "Select language" button at the top, then Screen 1 is displayed
- There is no title and no button at the top
- The whole screen estate is occupied by the list with 6 entries
- Each list item consists of text and colorful icon
- The text is human readable language name (written in that language!) followed by the 2-letter language code in brackets: "English (en)", "Deutsch (de)", etc
- The icon is the simplified national flag, drawn as SVG icon

When the user touches one of the language list items:

- The 2-letter language code is stored as `language` in shared preferences
- The Room database for that language is opened
- If the `words` table has less than `min_words` records and there is no active download for the selected language, then that table is cleared and a new download and parsing is started from `hashed_dictionary_url` and Screen 2 is displayed (maybe the download and parsing code should be part of Screen 2?). Otherwise Screen 4 is displayed

## Screen 2 (Loading dictionary)

- The title displays localized "Loading dictionary" and a close button "X"
- Pressing "X" runs FailureActions, then displays Screen 1
- Failed download or failed parsing of `hashed_dictionary_url` runs FailureActions, then displays Screen 3
- The rest of the screen is occupied by round loading indicator. If possible, the loading indicator max value is `min_words` and the current value is set by the low work memory parser. Do not get the total value of words from the Room if it costs too much CPU. As a fallback just display infinite round loading indicator

## Screen 3 (Download failed)

- The title displays localized "Download failed" and a close button "X"
- Pressing "X" runs FailureActions, then displays Screen 1
- The rest of screen is occupied by a localized message asking the user to check the internet connection and a retry button
- Pressing the retry button displays Screen 2 and starts the download again

## Screen 4 (Home)

- Title says localized "Words by Farber", a button displaying 2-letter language code and the flag of the selected language is displayed near it
- Pressing the flag button displays Screen 1 allowing the user to switch to another language
- The rest of the screen is occupied by the following list

### Home list

- Game 1 (will display a static 15x15 letter grid)
- Game 2 (will display a static 5x5 letter grid)
- Top players (using /ru/top-all /en/top-all etc)
- Your profile
- Find a word
- 2-letter words
- 3-letter words
- Words with `rare_letter_1`
- Words with `rare_letter_2`
- Preferences
- Help
- Privacy policy
- Terms of service

## Screen 5 (Game 1 - Static Grid 15x15)

- The title displays localized "Game 1"
- A close button "X" is displayed at the top, pressing it navigates back to Screen 4
- The rest of the screen is occupied by a static 15x15 letter grid

## Screen 6 (Game 2 - Static Grid 5x5)

- The title displays localized "Game 2"
- A close button "X" is displayed at the top, pressing it navigates back to Screen 4
- The rest of the screen is occupied by a static 5x5 letter grid

## Screen 7 (Top Players)

- Look at the https://wordsbyfarber.com/ru/top-all to understand the available user data format!
- The title displays localized "Top Players"
- A close button "X" is displayed at the top, pressing it navigates back to Screen 4
- A search input text field is displayed below the title and button
- The rest of the screen is occupied by a filtered list of top players (if search field is non-empty)

## Screen 8 (Your Profile)

- The title displays localized "Your Profile"
- A close button "X" is displayed at the top, pressing it navigates back to Screen 4
- The rest of the screen is occupied by user profile details (currently a dummy user is displayed)

## Screen 9 (Find a Word)

- The title displays localized "Find a Word"
- A close button "X" is displayed at the top, pressing it navigates back to Screen 4
- A search input text field is displayed below the title and button
- The rest of the screen is occupied by a filtered list of words (if search field is non-empty)

## Screen 10 (2-letter words)

- The title displays localized "2-letter words"
- A close button "X" is displayed at the top, pressing it navigates back to Screen 4
- A search input text field is displayed below the title and button
- The rest of the screen is occupied by a filtered list of 2-letter words (if search field is non-empty)

## Screen 11 (3-letter words)

- The title displays localized "3-letter words"
- A close button "X" is displayed at the top, pressing it navigates back to Screen 4
- A search input text field is displayed below the title and button
- The rest of the screen is occupied by a filtered list of 3-letter words (if search field is non-empty)

## Screen 12 (Words with rare_letter_1)

- The title displays localized "Words with [rare\_letter\_1]" (e.g., "Words with Q")
- A close button "X" is displayed at the top, pressing it navigates back to Screen 4
- A search input text field is displayed below the title and button
- The rest of the screen is occupied by a filtered list of words containing `rare_letter_1` (if search field is non-empty)

## Screen 13 (Words with rare_letter_2)

- The title displays localized "Words with [rare\_letter\_2]" (e.g., "Words with Y")
- A close button "X" is displayed at the top, pressing it navigates back to Screen 4
- A search input text field is displayed below the title and button
- The rest of the screen is occupied by a filtered list of words containing `rare_letter_2` (if search field is non-empty)

## Screen 14 (Preferences)

- The title displays localized "Preferences"
- A close button "X" is displayed at the top, pressing it navigates back to Screen 4
- The rest of the screen is occupied by a list of preference options

## Screen 15 (Help)

- The title displays localized "Help"
- A close button "X" is displayed at the top, pressing it navigates back to Screen 4
- The rest of the screen is occupied by help content

## Screen 16 (Privacy Policy)

- The title displays localized "Privacy Policy"
- A close button "X" is displayed at the top, pressing it navigates back to Screen 4
- The rest of the screen is occupied by the privacy policy text

## Screen 17 (Terms of Service)

- The title displays localized "Terms of Service"
- A close button "X" is displayed at the top, pressing it navigates back to Screen 4
- The rest of the screen is occupied by the terms of service text

## Text UI Flow Diagram

```
Start App
    |
    v
Check SharedPrefs
    |
    v
[Language Valid?] ─NO─> Screen 1: Select Language
    |                        |
   YES                       v
    |                  [User Selects Language]
    |                        |
    v                        v
Check Room DB          Store Language in SharedPrefs
    |                        |
    v                        v
[Words >= min_words?] ─NO─> Screen 2: Loading Dictionary
    |                        |
   YES                       v
    |                  [Download Success?]
    |                        |
    v                       NO
Screen 4: Home               |
    |                        v
    v                  Screen 3: Download Failed
[User Selects Item]          |
    |                        v
    v                  [User Retries?] ─YES─> Screen 2
Game 1 ─> Screen 5           |
Game 2 ─> Screen 6          NO
Top Players ─> Screen 7      |
Your Profile ─> Screen 8     v
Find Word ─> Screen 9        Screen 1
2-letter ─> Screen 10
3-letter ─> Screen 11
Rare Letter 1 ─> Screen 12
Rare Letter 2 ─> Screen 13
Preferences ─> Screen 14
Help ─> Screen 15
Privacy ─> Screen 16
Terms ─> Screen 17
    |
    v
[Press X] ─> Screen 4
```

# Key Classes Structure

## Package: com.wordsbyfarber

### Data Layer

```
com.wordsbyfarber.data
├── database/
│   ├── WordsDatabase.kt (Room Database)
│   ├── WordEntity.kt (Room Entity)
│   └── WordDao.kt (Room DAO)
├── repository/
│   ├── DictionaryRepository.kt (Data operations)
│   └── PreferencesRepository.kt (SharedPreferences wrapper)
├── network/
│   ├── DictionaryDownloader.kt (HTTP download service)
│   └── DictionaryParser.kt (JavaScript parsing logic)
└── models/
    ├── Language.kt (Language data class)
    ├── DownloadState.kt (Download state enum)
    └── WordItem.kt (Word display model)
```

### Domain Layer

```
com.wordsbyfarber.domain
├── usecases/
│   ├── GetLanguagesUseCase.kt
│   ├── SelectLanguageUseCase.kt
│   ├── DownloadDictionaryUseCase.kt
│   ├── SearchWordsUseCase.kt
│   └── GetWordsUseCase.kt
└── models/
    ├── LanguageInfo.kt
    └── WordSearchResult.kt
```

### UI Layer (Jetpack Compose)

```
com.wordsbyfarber.ui
├── MainActivity.kt (Single Activity with Compose)
├── navigation/
│   ├── AppNavigation.kt (Compose Navigation)
│   └── Screen.kt (Screen route definitions)
├── screens/
│   ├── LanguageSelectionScreen.kt (Screen 1)
│   ├── LoadingDictionaryScreen.kt (Screen 2)
│   ├── DownloadFailedScreen.kt (Screen 3)
│   ├── HomeScreen.kt (Screen 4)
│   ├── Game1Screen.kt (Screen 5)
│   ├── Game2Screen.kt (Screen 6)
│   ├── TopPlayersScreen.kt (Screen 7)
│   ├── ProfileScreen.kt (Screen 8)
│   ├── FindWordScreen.kt (Screen 9)
│   ├── TwoLetterWordsScreen.kt (Screen 10)
│   ├── ThreeLetterWordsScreen.kt (Screen 11)
│   ├── RareLetter1Screen.kt (Screen 12)
│   ├── RareLetter2Screen.kt (Screen 13)
│   ├── PreferencesScreen.kt (Screen 14)
│   ├── HelpScreen.kt (Screen 15)
│   ├── PrivacyPolicyScreen.kt (Screen 16)
│   └── TermsOfServiceScreen.kt (Screen 17)
├── components/
│   ├── TopAppBar.kt (Reusable top bar with title and close button)
│   ├── SearchField.kt (Reusable search input field)
│   ├── LanguageListItem.kt (Language selection item with flag)
│   ├── WordListItem.kt (Word display item)
│   ├── HomeMenuItem.kt (Home screen menu item)
│   ├── LetterGrid.kt (Composable letter grid for games)
│   ├── LoadingIndicator.kt (Progress indicator component)
│   └── FlagIcon.kt (SVG flag renderer composable)
├── viewmodels/
│   ├── LanguageSelectionViewModel.kt
│   ├── LoadingDictionaryViewModel.kt
│   ├── HomeViewModel.kt
│   ├── WordSearchViewModel.kt
│   └── GameViewModel.kt
└── theme/
    ├── Color.kt (App color palette)
    ├── Type.kt (Typography definitions)
    ├── Theme.kt (App theme configuration)
    └── Shapes.kt (Shape definitions)
```

### Utils Layer

```
com.wordsbyfarber.utils
├── Constants.kt (App constants)
├── Extensions.kt (Kotlin extensions)
├── NetworkUtils.kt (Network connectivity)
└── StringUtils.kt (String manipulation)
```

# Happy Path and Edge Cases

## Happy Path

1.  **First Launch**: User opens the app for the first time.

    - App checks shared preferences, finds no valid language.
    - **Screen 1** is displayed with a list of 6 languages.
    - User selects "English (en)".
    - App stores "en" in shared preferences.
    - App opens the Room database for English.
    - App checks `words` table, finds it empty (less than `min_words`).
    - App clears the table and starts downloading `Consts-en.js`.
    - **Screen 2** is displayed, showing a loading indicator and progress.
    - Download completes successfully.
    - Parsing completes successfully, storing words in the Room database.
    - **Screen 4** (Home) is displayed with "Words by Farber" title, English flag, and the home list.
    - User selects "Game 1".
    - **Screen 5** is displayed with a static 15x15 grid.
    - User presses "X" button.
    - **Screen 4** is displayed again.

2.  **Returning User (Dictionary Available)**: User opens the app after a successful download.

    - App checks shared preferences, finds "en".
    - App opens the Room database for English.
    - App checks `words` table, finds more than `min_words` records.
    - **Screen 4** (Home) is displayed directly.

## Edge Cases

1.  **No Internet Connection on First Download**:

    - User opens the app, selects a language in **Screen 1**.
    - App starts download in **Screen 2**.
    - Download fails due to no internet connection.
    - FailureActions are executed (stop download, delete words table records, _do not_ delete language from shared preferences).
    - **Screen 3** (Download failed) is displayed.
    - User restores internet connection and presses "Retry".
    - App re-attempts download and parsing in **Screen 2**.
    - If successful, proceeds to **Screen 4**.

2.  **User Cancels Download**:

    - User selects a language in **Screen 1**.
    - **Screen 2** is displayed, showing download in progress.
    - User presses the "X" button on **Screen 2**.
    - FailureActions are executed (stop download, delete words table records, _delete_ language from shared preferences).
    - **Screen 1** is displayed again.

3.  **Corrupted Dictionary File Download**:

    - User selects a language in **Screen 1**.
    - App starts download in **Screen 2**.
    - Download completes, but the `Consts-{lang}.js` file is corrupted or malformed, leading to parsing failure.
    - FailureActions are executed (stop download, delete words table records, _do not_ delete language from shared preferences).
    - **Screen 3** (Download failed) is displayed.

4.  **Shared Preferences Language Invalid**:

    - User previously selected a language, but the value in shared preferences is "xx" (not a supported 2-letter code).
    - App starts.
    - App checks shared preferences, finds "xx", deletes it.
    - **Screen 1** is displayed.

5.  **Room Database Corruption/Under `min_words` with Active Download**:

    - User has a language selected, and a download was previously initiated (or partially completed), but the app crashed.
    - App starts, finds language in shared preferences.
    - App opens Room DB.
    - App checks `words` table and finds it under `min_words`.
    - App also detects an "active download" flag for this language.
    - Instead of clearing the table and restarting, the app should resume the existing download or display **Screen 2** to indicate ongoing process. (Clarification: The current spec says "if there is no active download", implying a new download _only_ if no active one. The system should handle resuming or re-initiating depending on the state of the active download.)

6.  **Switching Language After Full Download**:

    - User has successfully downloaded English dictionary and is on **Screen 4**.
    - User presses the flag button on **Screen 4**.
    - **Screen 1** is displayed.
    - User selects "German (de)".
    - App stores "de" in shared preferences.
    - App checks German `words` table, finds it empty.
    - Download and parsing for German begins, **Screen 2** is displayed.

# Unit Tests

## Data Layer Tests

### DictionaryRepository Tests

- `test_getLanguages_returnsAllSupportedLanguages()`
- `test_selectLanguage_storesLanguageInPreferences()`
- `test_getCurrentLanguage_returnsStoredLanguage()`
- `test_getCurrentLanguage_returnsNullWhenNoLanguageStored()`
- `test_clearWordsTable_removesAllWords()`
- `test_getWordCount_returnsCorrectCount()`
- `test_hasMinWords_returnsTrueWhenEnoughWords()`
- `test_hasMinWords_returnsFalseWhenInsufficientWords()`

### DictionaryDownloader Tests

- `test_downloadDictionary_successfulDownload()`
- `test_downloadDictionary_networkError()`
- `test_downloadDictionary_invalidUrl()`
- `test_downloadDictionary_cancelDownload()`
- `test_downloadDictionary_progressCallback()`

### DictionaryParser Tests

- `test_parseDictionary_validJavaScriptFile()`
- `test_parseDictionary_invalidJavaScriptFile()`
- `test_parseDictionary_emptyFile()`
- `test_parseDictionary_skipLangKey()`
- `test_parseDictionary_handleSpecialCharacters()`
- `test_parseDictionary_progressCallback()`

### WordDao Tests

- `test_insertWords_storesWordsCorrectly()`
- `test_getWordCount_returnsCorrectCount()`
- `test_searchWords_returnsFilteredResults()`
- `test_getWordsByLength_returnsCorrectWords()`
- `test_getWordsByRareLetter_returnsCorrectWords()`
- `test_deleteAllWords_removesAllWords()`

## Domain Layer Tests

### GetLanguagesUseCase Tests

- `test_execute_returnsAllSupportedLanguages()`
- `test_execute_includesCorrectLanguageInfo()`

### SelectLanguageUseCase Tests

- `test_execute_validLanguage_storesLanguage()`
- `test_execute_invalidLanguage_throwsException()`

### DownloadDictionaryUseCase Tests

- `test_execute_successfulDownload_storesWords()`
- `test_execute_networkError_throwsException()`
- `test_execute_parsingError_throwsException()`
- `test_execute_cancelDownload_stopsProcess()`

### SearchWordsUseCase Tests

- `test_execute_emptyQuery_returnsEmptyList()`
- `test_execute_validQuery_returnsFilteredWords()`
- `test_execute_caseInsensitiveSearch()`

## UI Layer Tests (Jetpack Compose)

### Screen Tests (Compose UI Tests)

- `test_LanguageSelectionScreen_displaysAllLanguages()`
- `test_LanguageSelectionScreen_clickLanguage_navigatesToLoading()`
- `test_LoadingDictionaryScreen_displaysProgress()`
- `test_LoadingDictionaryScreen_clickClose_cancelsDownload()`
- `test_HomeScreen_displaysMenuItems()`
- `test_HomeScreen_clickMenuItem_navigatesToCorrectScreen()`
- `test_SearchableScreens_filterResultsCorrectly()`

### ViewModel Tests

- `test_LanguageSelectionViewModel_loadLanguages_populatesLanguageList()`
- `test_LanguageSelectionViewModel_selectLanguage_updatesSelectedLanguage()`
- `test_LanguageSelectionViewModel_selectLanguage_triggersNavigation()`

### LoadingDictionaryViewModel Tests

- `test_startDownload_updatesProgressState()`
- `test_downloadSuccess_navigatesToHome()`
- `test_downloadFailure_navigatesToErrorScreen()`
- `test_cancelDownload_stopsProcess()`

### HomeViewModel Tests

- `test_loadHomeItems_populatesMenuList()`
- `test_getCurrentLanguage_returnsSelectedLanguage()`
- `test_switchLanguage_navigatesToLanguageSelection()`

### WordSearchViewModel Tests

- `test_searchWords_updatesResultsList()`
- `test_searchWords_emptyQuery_showsEmptyResults()`
- `test_searchWords_validQuery_showsFilteredResults()`
- `test_clearSearch_resetsResults()`

### Component Tests

- `test_TopAppBar_displaysCorrectTitle()`
- `test_TopAppBar_clickClose_triggersCallback()`
- `test_SearchField_textChange_triggersSearch()`
- `test_LetterGrid_displaysCorrectLayout()`
- `test_FlagIcon_displaysCorrectFlag()`

## Integration Tests

### End-to-End Flow Tests

- `test_firstLaunch_selectLanguage_downloadDictionary_showHome()`
- `test_returningUser_showHomeDirectly()`
- `test_switchLanguage_downloadNewDictionary()`
- `test_downloadFailure_showErrorScreen_retrySuccess()`
- `test_cancelDownload_returnToLanguageSelection()`

### Database Integration Tests

- `test_fullDictionaryDownload_storesAllWords()`
- `test_wordSearch_acrossAllWordTypes()`
- `test_languageSwitch_clearsOldData()`
