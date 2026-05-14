package com.example.wtcapp.ui.components

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val chatTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun formatChatTime(sentAt: String?): String {
    val value = sentAt?.trim().orEmpty()
    if (value.isBlank()) return ""

    return runCatching {
        val instant = when {
            value.endsWith("Z", ignoreCase = true) -> Instant.parse(value)
            hasExplicitOffset(value) -> OffsetDateTime.parse(value).toInstant()
            else -> LocalDateTime.parse(value).toInstant(ZoneOffset.UTC)
        }

        instant
            .atZone(ZoneId.systemDefault())
            .format(chatTimeFormatter)
    }.getOrElse {
        value
    }
}

private fun hasExplicitOffset(value: String): Boolean {
    return Regex("""[+-]\d{2}:\d{2}$""").containsMatchIn(value)
}
