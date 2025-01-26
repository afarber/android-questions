package com.wordsbyfarber.utils

fun getFlagEmoji(countryCode: String): String {
    // Convert country code to uppercase to match Unicode regional indicator symbols
    val uppercasedCountryCode = if (countryCode.equals("en", ignoreCase = true)) "GB" else countryCode.uppercase()

    // Return chequered flag emoji for invalid country codes
    if (uppercasedCountryCode.length != 2) {
        return "\uD83C\uDFC1"
    }

    val firstChar = Character.codePointAt(uppercasedCountryCode, 0) - 0x41 + 0x1F1E6
    val secondChar = Character.codePointAt(uppercasedCountryCode, 1) - 0x41 + 0x1F1E6

    return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
}
