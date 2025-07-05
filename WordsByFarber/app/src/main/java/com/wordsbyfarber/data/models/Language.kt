package com.wordsbyfarber.data.models

data class Language(
    val code: String,
    val name: String,
    val rareLetter1: String,
    val rareLetter2: String,
    val hashedDictionaryUrl: String,
    val topUrl: String,
    val minWords: Int,
    val myUid: Int
)