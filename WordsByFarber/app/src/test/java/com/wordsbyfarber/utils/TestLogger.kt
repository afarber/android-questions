package com.wordsbyfarber.utils

// Test logger that doesn't use Android Log to avoid mocking issues in unit tests
object TestLogger {
    fun d(tag: String, message: String) {
        println("DEBUG $tag: $message")
    }
    
    fun e(tag: String, message: String) {
        println("ERROR $tag: $message")
    }
    
    fun e(tag: String, message: String, throwable: Throwable) {
        println("ERROR $tag: $message")
        throwable.printStackTrace()
    }
}