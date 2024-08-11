package com.hcmus.tenderus.model

import java.time.LocalDateTime

enum class Status { PENDING, REVIEWED }

data class Report(
    val id: String,
    val username: String,
    val date: LocalDateTime,
    val status: Status
)
