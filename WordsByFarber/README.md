# A word game with game dictionaries for 6 languages

A simple Kotlin app, where most pages have the following UI: a title, a search field and a filtered list.

Initially a list of 6 languages is displayed

| Language | Rare Letter 1 | Rare Letter 2 | Dictionary URL                         |
| -------- | ------------- | ------------- | -------------------------------------- |
| de       | Q             | Y             | https://wordsbyfarber.com/Consts-de.js |
| en       | Q             | X             | https://wordsbyfarber.com/Consts-en.js |
| fr       | K             | W             | https://wordsbyfarber.com/Consts-fr.js |
| nl       | Q             | X             | https://wordsbyfarber.com/Consts-nl.js |
| pl       | Ń             | Ź             | https://wordsbyfarber.com/Consts-pl.js |
| ru       | Ъ             | Э             | https://wordsbyfarber.com/Consts-ru.js |

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
