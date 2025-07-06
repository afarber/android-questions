package com.wordsbyfarber.data.models

// Enum representing the current state of dictionary download and parsing
enum class DownloadState {
    IDLE,
    DOWNLOADING,
    PARSING,
    SUCCESS,
    FAILED,
    CANCELLED
}