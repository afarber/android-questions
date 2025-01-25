package com.wordsbyfarber.utils

import android.content.Context

fun saveLanguage(context: Context, language: String) {
    val sharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString("language", language)
        apply()
    }
}

fun getSavedLanguage(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    return sharedPreferences.getString("language", null)
}
