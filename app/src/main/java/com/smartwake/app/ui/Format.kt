package com.smartwake.app.ui

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val Spanish = Locale.forLanguageTag("es")
private val hourMinute = DateTimeFormatter.ofPattern("h:mm", Locale.US)
private val paddedHourMinute = DateTimeFormatter.ofPattern("hh:mm", Locale.US)

/** "AM"/"PM" as the designs write it (the Spanish locale would give "a. m."). */
val LocalTime.period: String get() = if (hour < 12) "AM" else "PM"

/** 6:45 */
fun LocalTime.formatShort(): String = format(hourMinute)

/** 07:00 */
fun LocalTime.formatPadded(): String = format(paddedHourMinute)

/** 8:30 AM */
fun LocalTime.formatShortWithPeriod(): String = "${formatShort()} $period"

/** 06:30 AM */
fun LocalTime.formatPaddedWithPeriod(): String = "${formatPadded()} $period"

/** Lunes, 24 Octubre */
fun LocalDate.formatSummaryDate(): String {
    val day = dayOfWeek.getDisplayName(TextStyle.FULL, Spanish).replaceFirstChar { it.titlecase(Spanish) }
    val month = month.getDisplayName(TextStyle.FULL, Spanish).replaceFirstChar { it.titlecase(Spanish) }
    return "$day, $dayOfMonth $month"
}

fun DayOfWeek.fullName(): String = getDisplayName(TextStyle.FULL, Spanish)
