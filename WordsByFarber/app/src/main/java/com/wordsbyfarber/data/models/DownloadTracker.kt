package com.wordsbyfarber.data.models

// Singleton to track active downloads using DownloadState enum
object DownloadTracker {
    private val activeDownloads = mutableSetOf<String>()
    private val downloadStates = mutableMapOf<String, DownloadState>()
    
    /**
     * Check if a download is currently active for the given language
     * @param languageCode The 2-letter language code
     * @return True if download is active, false otherwise
     */
    fun isDownloadActive(languageCode: String): Boolean {
        return activeDownloads.contains(languageCode)
    }
    
    /**
     * Start tracking a download for the given language
     * @param languageCode The 2-letter language code
     */
    fun startDownload(languageCode: String) {
        activeDownloads.add(languageCode)
        downloadStates[languageCode] = DownloadState.DOWNLOADING
    }
    
    /**
     * Update the download state for a language
     * @param languageCode The 2-letter language code
     * @param state The new download state
     */
    fun updateDownloadState(languageCode: String, state: DownloadState) {
        if (activeDownloads.contains(languageCode)) {
            downloadStates[languageCode] = state
        }
    }
    
    /**
     * Finish and remove tracking for a download
     * @param languageCode The 2-letter language code
     */
    fun finishDownload(languageCode: String) {
        activeDownloads.remove(languageCode)
        downloadStates.remove(languageCode)
    }
    
    /**
     * Get the current download state for a language
     * @param languageCode The 2-letter language code
     * @return The current download state or IDLE if not downloading
     */
    fun getDownloadState(languageCode: String): DownloadState {
        return downloadStates[languageCode] ?: DownloadState.IDLE
    }
    
    /**
     * Cancel a download and clean up tracking
     * @param languageCode The 2-letter language code
     */
    fun cancelDownload(languageCode: String) {
        downloadStates[languageCode] = DownloadState.CANCELLED
        activeDownloads.remove(languageCode)
        downloadStates.remove(languageCode)
    }
    
    /**
     * Get all currently active downloads
     * @return Set of language codes with active downloads
     */
    fun getActiveDownloads(): Set<String> {
        return activeDownloads.toSet()
    }
    
    /**
     * Clear all download tracking (useful for testing)
     */
    fun clearAll() {
        activeDownloads.clear()
        downloadStates.clear()
    }
}