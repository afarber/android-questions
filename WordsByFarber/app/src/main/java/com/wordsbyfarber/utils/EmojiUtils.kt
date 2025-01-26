package com.wordsbyfarber.utils

fun getFlagEmoji(countryCode: String): String {
    if (countryCode.length != 2) {
        return "\uD83C\uDFC1" // Return chequered flag emoji for invalid country codes
    }

    val firstChar = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
    val secondChar = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6

    return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
}
