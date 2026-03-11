package de.afarber.magicapp.data.common

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val transportTimestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun nowTimestamp(): String = LocalDateTime.now().format(transportTimestampFormatter)

fun Throwable.toErrorText(): String = "${javaClass.simpleName}: ${message ?: "Unknown error"}"

fun <T> prependCapped(
    item: T,
    current: List<T>,
    maxItems: Int = 100,
): List<T> {
    if (maxItems <= 0) {
        return emptyList()
    }
    return listOf(item) + current.take(maxItems - 1)
}
