package com.hcmus.tenderus.model

data class ReportAction(
    val penalty: String,
    val deleteContent: Boolean,
)

data class AccountAction(
    val penaltyDeleted: List<String>
)
