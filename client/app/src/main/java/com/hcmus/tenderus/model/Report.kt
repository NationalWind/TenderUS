package com.hcmus.tenderus.model

enum class Status { PENDING, REVIEWED }

data class Report(
    val id: String,
    val reporter: String,
    val reporterAvatar: String = "",
    val reported: String,
    val reportedAvatar: String = "",
    val date: String,
    val status: Status = Status.PENDING,
    val message: String
)

data class ReportData(
    val reporter: String,
    val reported: String,
    val message: String
)
