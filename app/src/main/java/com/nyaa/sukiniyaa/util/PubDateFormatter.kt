package com.nyaa.sukiniyaa.util

import java.text.SimpleDateFormat
import java.util.Locale

object PubDateFormatter {
    private val inputPatterns = arrayOf(
        "EEE, dd MMM yyyy HH:mm:ss Z",
        "EEE, dd MMM yyyy HH:mm:ss z"
    )

    fun format(raw: String): String {
        if (raw.isBlank()) return ""
        val parsed = parse(raw) ?: return raw
        return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(parsed)
    }

    internal fun parse(raw: String): java.util.Date? {
        for (pattern in inputPatterns) {
            try {
                val parsed = SimpleDateFormat(pattern, Locale.US).parse(raw)
                if (parsed != null) return parsed
            } catch (_: Exception) {
            }
        }
        return null
    }
}
