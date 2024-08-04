package com.hcmus.tenderus.utils

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration


fun getCurrentDateTimeIso(): String {
    val currentDateTime = ZonedDateTime.now()  // Get the current date and time
    return currentDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)  // Format it to ISO 8601 string
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