package com.nathakusuma.neriva.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Utility functions for date and time formatting
 */
object DateTimeUtils {
    /**
     * Format ISO 8601 timestamp to simple time format (e.g., "09:25 AM")
     *
     * @param isoTimestamp ISO 8601 timestamp string (e.g., "2025-11-01T14:05:50.106826")
     * @return Formatted time string (e.g., "02:05 PM")
     */
    fun formatChatTime(isoTimestamp: String): String {
        return try {
            // Parse ISO 8601 timestamp
            val instant = Instant.parse(
                if (isoTimestamp.contains("Z") || isoTimestamp.contains("+")) {
                    isoTimestamp
                } else {
                    "${isoTimestamp}Z"
                }
            )

            // Format to time only
            val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
                .withZone(ZoneId.systemDefault())

            formatter.format(instant)
        } catch (_: Exception) {
            // Fallback: try simple format
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
                val date = sdf.parse(isoTimestamp)
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                date?.let { timeFormat.format(it) } ?: isoTimestamp
            } catch (_: Exception) {
                isoTimestamp
            }
        }
    }
}

