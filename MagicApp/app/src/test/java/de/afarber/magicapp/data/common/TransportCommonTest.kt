package de.afarber.magicapp.data.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TransportCommonTest {
    @Test
    fun `prependCapped puts newest item first and caps size`() {
        val initial = listOf("b", "c", "d")
        val updated = prependCapped(item = "a", current = initial, maxItems = 3)
        assertEquals(listOf("a", "b", "c"), updated)
    }

    @Test
    fun `toErrorText uses class and message`() {
        val throwable = IllegalStateException("boom")
        assertEquals("IllegalStateException: boom", throwable.toErrorText())
    }

    @Test
    fun `nowTimestamp matches expected format`() {
        val timestamp = nowTimestamp()
        assertTrue(timestamp.matches(Regex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")))
    }
}
