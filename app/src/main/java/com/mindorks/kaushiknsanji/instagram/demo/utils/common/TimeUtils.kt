package com.mindorks.kaushiknsanji.instagram.demo.utils.common

import java.util.*

/**
 * Utility object for all things related to Time.
 *
 * @author Kaushik N Sanji
 */
object TimeUtils {

    //Constants for Time metrics
    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = 24 * HOUR_MILLIS

    /**
     * Returns the time difference of the given [date] with the current time in words.
     */
    fun getTimeAgo(date: Date): String {
        // Get Time for the Date passed
        val time = date.time
        // Get Current Time
        val now = System.currentTimeMillis()
        // Return Empty string if the conditions are invalid
        if (time > now || time <= 0) return ""

        // Take the difference
        val diff = now - time

        // Evaluate the time difference and return the corresponding String
        return when {
            diff < MINUTE_MILLIS -> "just now"
            diff < 2 * MINUTE_MILLIS -> "a minute ago"
            diff < 50 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} minutes ago"
            diff < 90 * MINUTE_MILLIS -> "an hour ago"
            diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} hours ago"
            diff < 48 * HOUR_MILLIS -> "yesterday"
            else -> "${diff / DAY_MILLIS} days ago"
        }

    }

}