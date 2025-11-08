package com.chucklingkoala.stagehand.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateFormatter {

    fun formatTimeAgo(isoTimestamp: String): String {
        return try {
            val instant = Instant.parse(isoTimestamp)
            val now = Instant.now()

            val minutes = ChronoUnit.MINUTES.between(instant, now)
            val hours = ChronoUnit.HOURS.between(instant, now)
            val days = ChronoUnit.DAYS.between(instant, now)

            when {
                minutes < 1 -> "Just now"
                minutes < 60 -> "${minutes}m ago"
                hours < 24 -> "${hours}h ago"
                days < 7 -> "${days}d ago"
                else -> {
                    val formatter = DateTimeFormatter.ofPattern("MMM d")
                        .withZone(ZoneId.systemDefault())
                    formatter.format(instant)
                }
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    fun formatFullDate(isoTimestamp: String): String {
        return try {
            val instant = Instant.parse(isoTimestamp)
            val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a")
                .withZone(ZoneId.systemDefault())
            formatter.format(instant)
        } catch (e: Exception) {
            isoTimestamp
        }
    }
}
