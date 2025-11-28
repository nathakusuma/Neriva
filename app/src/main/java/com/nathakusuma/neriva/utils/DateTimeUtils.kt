package com.nathakusuma.neriva.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.Period
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

    /**
     * Calculate age from birth date string
     *
     * @param birthDateString Birth date in format "yyyy-MM-dd"
     * @return Formatted age string (e.g., "2 Years", "1 Year", "5 Months", "1 Month", "15 Days", "1 Day")
     */
    fun calculateAge(birthDateString: String): String {
        return try {
            // Parse the birth date
            val birthDate = LocalDate.parse(birthDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val currentDate = LocalDate.now()

            // Calculate the period between birth date and current date
            val period = Period.between(birthDate, currentDate)

            val years = period.years
            val months = period.months
            val days = period.days

            // Round: if there are both years and months, only show years
            when {
                years > 0 -> if (years == 1) "1 Year" else "$years Years"
                months > 0 -> if (months == 1) "1 Month" else "$months Months"
                days > 0 -> if (days == 1) "1 Day" else "$days Days"
                else -> "0 Day"
            }
        } catch (_: Exception) {
            // Fallback: return the original string
            birthDateString
        }
    }
}
