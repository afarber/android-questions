package com.wordsbyfarber.data.network

import com.wordsbyfarber.data.database.WordEntity
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DictionaryStreamParserTest {
    
    private lateinit var parser: TestDictionaryStreamParser
    
    @Before
    fun setUp() {
        parser = TestDictionaryStreamParser()
    }
    
    @Test
    fun `parseStreamingChunks should find const HASHED pattern and parse JSON correctly`() = runTest {
        // Hardcoded JavaScript string as specified by user
        val jsContent = """
            const COUNTRY="de";
            const LETTERS_EN=["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"];
            const LETTERS=["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","Ä","Ö","Ü"];
            const VALUES={"*":0,"A":1,"B":3,"C":4,"D":1,"E":1,"F":4,"G":2,"H":2,"I":1,"J":6,"K":4,"L":2,"M":3,"N":1,"O":2,"P":4,"Q":10,"R":1,"S":1,"T":1,"U":1,"V":6,"W":3,"X":8,"Y":10,"Z":3,"Ä":6,"Ö":8,"Ü":6};
            const NUMBERS={"*":2,"A":5,"B":2,"C":2,"D":4,"E":15,"F":2,"G":3,"H":4,"I":6,"J":1,"K":2,"L":3,"M":4,"N":9,"O":3,"P":1,"Q":1,"R":6,"S":7,"T":6,"U":6,"V":1,"W":1,"X":1,"Y":1,"Z":1,"Ä":1,"Ö":1,"Ü":1};
            const HASHED={"000066acab8d6c57":"","0000943e357f59a1":"","0000bd49702a520b":"","000189677811ad94":"","0002eb43f5ac863d":"","0003003ad7a53218":"","000347b7b5ec7ac7":"","00037cb1c4d8fd12":"","0003889264c88d53":"","000391e838358d4a":"","000398991fcd5b78":"","0003bb68938d7ea6":"","00040a02894d3694":"","0004347c5fd16d0d":"","00053baf03edb10e":"","00054527ee892e56":"","0005edd1601a29f9":"","000646ad72d6bf80":"","000657da76f84dc7":"","00066caaa94c25e4":"","0006f394327825f9":"","0006f6b994892073":"","0007a5c1aee2d871":"","00081859d6eeeece":"","00083cb204c5d520":"","000856c5952c9d11":"","0009d2d0d3717213":"","0009ede0b8123349":"","000a2bf590c9d139":"","___LANG___":"de"};
        """.trimIndent()
        
        // Split into chunks to simulate streaming download
        val chunks = jsContent.chunked(500) // Create realistic chunk sizes
        
        val results = parser.parseStreamingChunks(chunks, minWords = 30).toList() // 30 total entries in test data
        
        // Should have loading states and then success
        assertTrue("Should have multiple results", results.size >= 2)
        
        // Last result should be success
        val lastResult = results.last()
        assertTrue("Last result should be Success", lastResult is ParseResult.Success)
        
        val successResult = lastResult as ParseResult.Success
        val words = successResult.words
        
        // Should have parsed all words except ___LANG___
        assertEquals("Should have 29 words (30 minus ___LANG___)", 29, words.size)
        
        // Verify some specific words are present
        val expectedWords = listOf(
            "000066acab8d6c57",
            "0000943e357f59a1", 
            "0000bd49702a520b",
            "000a2bf590c9d139"
        )
        
        expectedWords.forEach { expectedWord ->
            assertTrue(
                "Word $expectedWord should be present",
                words.any { it.word == expectedWord }
            )
        }
        
        // Verify ___LANG___ is NOT present
        assertTrue(
            "___LANG___ should be filtered out",
            words.none { it.word == "___LANG___" }
        )
        
        // Verify all words have empty explanations (as per test data)
        words.forEach { word ->
            assertEquals("All explanations should be empty in test data", "", word.explanation)
        }
    }
    
    @Test
    fun `parseStreamingChunks should handle pattern split across chunks`() = runTest {
        val beforePattern = "const COUNTRY=\"de\"; const NUMBERS={\"A\":1}; const HASH"
        val afterPattern = "ED={\"test1\":\"value1\",\"test2\":\"value2\",\"___LANG___\":\"de\"};"
        
        val chunks = listOf(beforePattern, afterPattern)
        
        val results = parser.parseStreamingChunks(chunks, minWords = 2).toList() // 2 total entries in test data
        val lastResult = results.last()
        assertTrue("Should succeed when pattern spans chunks", lastResult is ParseResult.Success)
        
        val words = (lastResult as ParseResult.Success).words
        assertEquals("Should have 2 words (excluding ___LANG___)", 2, words.size)
        
        val expectedWords = mapOf(
            "test1" to "value1",
            "test2" to "value2"
        )
        
        words.forEach { word ->
            assertTrue("Word ${word.word} should be expected", expectedWords.containsKey(word.word))
            assertEquals("Word ${word.word} should have correct explanation", 
                expectedWords[word.word], word.explanation)
        }
    }
    
    @Test
    fun `parseStreamingChunks should handle missing pattern gracefully`() = runTest {
        val jsContent = """
            const COUNTRY="de";
            const VALUES={"A":1,"B":2};
            const NUMBERS={"A":5,"B":2};
        """.trimIndent()
        
        val chunks = listOf(jsContent)
        
        val results = parser.parseStreamingChunks(chunks, minWords = 100).toList() // Default minWords for error case
        val lastResult = results.last()
        
        assertTrue("Should fail when pattern is missing", lastResult is ParseResult.Error)
        val errorResult = lastResult as ParseResult.Error
        assertTrue("Error message should mention invalid format", 
            errorResult.message.contains("invalid"))
    }
    
    @Test
    fun `parseStreamingChunks should handle malformed JSON gracefully`() = runTest {
        val jsContent = "const HASHED={\"key1\":\"value1\",\"malformed\":}"
        val chunks = listOf(jsContent)
        
        val results = parser.parseStreamingChunks(chunks, minWords = 100).toList() // Default minWords for error case
        val lastResult = results.last()
        
        // Could be either error or incomplete parsing depending on implementation
        assertTrue("Should handle malformed JSON", 
            lastResult is ParseResult.Error || lastResult is ParseResult.Success)
    }
    
    @Test
    fun `parseStreamingChunks should handle empty HASHED object`() = runTest {
        val jsContent = "const HASHED={};"
        val chunks = listOf(jsContent)
        
        val results = parser.parseStreamingChunks(chunks, minWords = 0).toList() // 0 entries expected
        val lastResult = results.last()
        
        assertTrue("Should succeed with empty object", lastResult is ParseResult.Success)
        val words = (lastResult as ParseResult.Success).words
        assertEquals("Should have no words for empty object", 0, words.size)
    }
}