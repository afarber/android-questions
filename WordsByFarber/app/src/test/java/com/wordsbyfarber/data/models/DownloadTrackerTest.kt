package com.wordsbyfarber.data.models

// Unit tests for DownloadTracker singleton
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class DownloadTrackerTest {

    @Before
    fun setup() {
        // Clear any existing state before each test
        DownloadTracker.clearAll()
    }

    @Test
    fun `isDownloadActive should return false when no download active`() {
        // When
        val result = DownloadTracker.isDownloadActive("de")
        
        // Then
        assertFalse(result)
    }

    @Test
    fun `startDownload should mark download as active`() {
        // When
        DownloadTracker.startDownload("de")
        
        // Then
        assertTrue(DownloadTracker.isDownloadActive("de"))
        assertEquals(DownloadState.DOWNLOADING, DownloadTracker.getDownloadState("de"))
    }

    @Test
    fun `finishDownload should remove download tracking`() {
        // Given
        DownloadTracker.startDownload("de")
        assertTrue(DownloadTracker.isDownloadActive("de"))
        
        // When
        DownloadTracker.finishDownload("de")
        
        // Then
        assertFalse(DownloadTracker.isDownloadActive("de"))
        assertEquals(DownloadState.IDLE, DownloadTracker.getDownloadState("de"))
    }

    @Test
    fun `updateDownloadState should change state for active download`() {
        // Given
        DownloadTracker.startDownload("de")
        
        // When
        DownloadTracker.updateDownloadState("de", DownloadState.PARSING)
        
        // Then
        assertTrue(DownloadTracker.isDownloadActive("de"))
        assertEquals(DownloadState.PARSING, DownloadTracker.getDownloadState("de"))
    }

    @Test
    fun `updateDownloadState should not work for inactive download`() {
        // When
        DownloadTracker.updateDownloadState("de", DownloadState.PARSING)
        
        // Then
        assertFalse(DownloadTracker.isDownloadActive("de"))
        assertEquals(DownloadState.IDLE, DownloadTracker.getDownloadState("de"))
    }

    @Test
    fun `cancelDownload should remove tracking and set cancelled state temporarily`() {
        // Given
        DownloadTracker.startDownload("de")
        
        // When
        DownloadTracker.cancelDownload("de")
        
        // Then
        assertFalse(DownloadTracker.isDownloadActive("de"))
        assertEquals(DownloadState.IDLE, DownloadTracker.getDownloadState("de"))
    }

    @Test
    fun `getActiveDownloads should return all active downloads`() {
        // Given
        DownloadTracker.startDownload("de")
        DownloadTracker.startDownload("en")
        DownloadTracker.startDownload("fr")
        
        // When
        val activeDownloads = DownloadTracker.getActiveDownloads()
        
        // Then
        assertEquals(3, activeDownloads.size)
        assertTrue(activeDownloads.contains("de"))
        assertTrue(activeDownloads.contains("en"))
        assertTrue(activeDownloads.contains("fr"))
    }

    @Test
    fun `multiple languages can be tracked independently`() {
        // When
        DownloadTracker.startDownload("de")
        DownloadTracker.startDownload("en")
        DownloadTracker.updateDownloadState("de", DownloadState.PARSING)
        
        // Then
        assertTrue(DownloadTracker.isDownloadActive("de"))
        assertTrue(DownloadTracker.isDownloadActive("en"))
        assertEquals(DownloadState.PARSING, DownloadTracker.getDownloadState("de"))
        assertEquals(DownloadState.DOWNLOADING, DownloadTracker.getDownloadState("en"))
        assertFalse(DownloadTracker.isDownloadActive("fr"))
    }

    @Test
    fun `finishDownload on one language should not affect others`() {
        // Given
        DownloadTracker.startDownload("de")
        DownloadTracker.startDownload("en")
        
        // When
        DownloadTracker.finishDownload("de")
        
        // Then
        assertFalse(DownloadTracker.isDownloadActive("de"))
        assertTrue(DownloadTracker.isDownloadActive("en"))
        assertEquals(1, DownloadTracker.getActiveDownloads().size)
        assertTrue(DownloadTracker.getActiveDownloads().contains("en"))
    }

    @Test
    fun `clearAll should remove all download tracking`() {
        // Given
        DownloadTracker.startDownload("de")
        DownloadTracker.startDownload("en")
        DownloadTracker.startDownload("fr")
        
        // When
        DownloadTracker.clearAll()
        
        // Then
        assertFalse(DownloadTracker.isDownloadActive("de"))
        assertFalse(DownloadTracker.isDownloadActive("en"))
        assertFalse(DownloadTracker.isDownloadActive("fr"))
        assertEquals(0, DownloadTracker.getActiveDownloads().size)
    }

    @Test
    fun `getDownloadState should return IDLE for non-tracked language`() {
        // When
        val result = DownloadTracker.getDownloadState("nonexistent")
        
        // Then
        assertEquals(DownloadState.IDLE, result)
    }
}