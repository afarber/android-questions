package com.wordsbyfarber.data.models

enum class DownloadState {
    IDLE,
    DOWNLOADING,
    PARSING,
    SUCCESS,
    FAILED,
    CANCELLED
}