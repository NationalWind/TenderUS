package com.hcmus.tenderus.utils

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration
import java.time.ZoneId
import java.util.Locale


fun getCurrentDateTimeIso(): String {
    val currentDateTime = ZonedDateTime.now()  // Get the current date and time
    return currentDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)  // Format it to ISO 8601 string
}

fun convertIsoToHumanReadableDate(isoDate: String): String {
    val zonedDateTime = ZonedDateTime.parse(isoDate)
    val hanoiTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Bangkok"))
    val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH)

    return hanoiTime.format(formatter)
}

fun convertIsoToHumanReadableDateTime(isoDate: String): String {
    val zonedDateTime = ZonedDateTime.parse(isoDate)
    val hanoiTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Bangkok"))
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a", Locale.ENGLISH)

    return hanoiTime.format(formatter)
}

fun convertIsoToHanoiTime(isoDate: String): String {
    val zonedDateTime = ZonedDateTime.parse(isoDate)

    val hanoiTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Bangkok"))

    val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

    return hanoiTime.format(formatter)
}

fun subtractInMinutes(dateTimeStr1: String, dateTimeStr2: String = getCurrentDateTimeIso()): Long {
    // Parse the date-time strings into ZonedDateTime objects
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val dateTime1 = ZonedDateTime.parse(dateTimeStr1, formatter)
    val dateTime2 = ZonedDateTime.parse(dateTimeStr2, formatter)

    // Calculate the difference in minutes
    val duration = Duration.between(dateTime1, dateTime2)
    return duration.toMinutes()
}