# A word game with partly hashed game dictionaries for 6 languages

Supported languages:

- de
- en
- fr
- nl
- pl
- ru

The dictionary of each language should be stored in a separate Room database

A simple Kotlin app, where most screens have the following UI structure:

- a screen title and a button on the top
- a search input text field is displayed below, but only in some screens
- the rest of the screen is occupied by a (filtered, if search field is non-empty) list

Initially the Room is empty and a list of 6 languages is displayed (Screen 1):

| language | rare_letter_1 | rare_letter_2 | hashed_dictionary_url                  | min_words |
| -------- | ------------- | ------------- | -------------------------------------- | --------- |
| de       | Q             | Y             | https://wordsbyfarber.com/Consts-de.js | 180_000   |
| en       | Q             | X             | https://wordsbyfarber.com/Consts-en.js | 270_000   |
| fr       | K             | W             | https://wordsbyfarber.com/Consts-fr.js | 370_000   |
| nl       | Q             | X             | https://wordsbyfarber.com/Consts-nl.js | 130_000   |
| pl       | Ń             | Ź             | https://wordsbyfarber.com/Consts-pl.js | 3_000_000 |
| ru       | Ъ             | Э             | https://wordsbyfarber.com/Consts-ru.js | 120_000   |

- Store the rare letter 1 in pl/res/values/strings.xml as <string name="rare_letter_1">Ń</string>
- Store the rare letter 2 in pl/res/values/strings.xml as <string name="rare_letter_2">Ź</string>
- Follow the same pattern for the other 5 languages: de, en, fr, nl, ru

When the user selects a language in the Screen 1, the selected language is stored as `language` in shared preferences and download and parsing of Consts-{de,en,fr,nl,pl,ru}.js begins and Screen 2 is displayed

# Dictionaries are obfuscated to prevent other developers from copying them

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

The `ru` dictionary is permanently stored at https://wordsbyfarber.com/Consts-ru.js as `HASHED` variable (same for other languages):

```javascript
const HASHED={"e41cb5fe71388d82":"Лещина, лесной орешник","a9fcf410c2b5d832":"","9875fce1352a0c6b":"Растение семейства миртовые","5f90b2bcd00059ba":"","d59e22f04156cd76":"","70e159c45a494e2a":"Разновидность домашней одежды","fa219f71cb145b14":"","07b57c456007538f":"Изготовление основы ткани","ИНЪЕКЦИРОВАТЬ":"","81a0de947f06765a":"","3095c690d9786f37":"","e3251210636b413a":"Плоды лаврового дерева", ...... ,"77475d3632716519":"","cab1ab6c407e900b":"","___LANG___":"ru"};
```

- The Consts-{de,en,fr,nl,pl,ru}.js files are quite big, so they should be downloaded and parsed in chunks to save work memory
- I do not think there is a JSON parsing library which could parse the file being downloaded chunk by chunk, so maybe a regex approach should be used?
- Parsing the dictionary should start when the substring `const HASHED={` has been found
- The key/value pairs (that is word/explanation) should be stored into Room database
- Parsing the dictionary should stop when the closing bracket `};` has been found
- The key entry `___LANG___` should be discarded, it should not be stored into Room

## UI flow

# Screen 1

- Screen 1 is displayed as the very first screen to the user, if there is no valid `language` 2-letters value found in shared preferences
- If there is `language` in shared preferences, but it is not one of the valid values , then `language` is deleted from shared preferences and Screen 1 is displayed
- Also, if the user is at Screen 3 and presses the "Select language" button at the top, then Screen 1 is displayed
- There is no title and no button at the top
- The whole screen estate is occupied by the list with 6 entries
- Each list item consists of text and colorful icon
- The text is human readable language name (written in that language!) followed by the 2-letters language code in brackets: "English (en)", "German (de)", etc
- The icon is the simplified national flag, drawn as SVG icon

When the user touches one of the language list:

- The 2-letter language code is stored as `language` in shared preferences
- The Room database for that language is opened
- If the `words` table has less than `min_words` records and there is no active download for the selected language then that table is cleared and a new download and parsing is started from `hashed_dictionary_url`
