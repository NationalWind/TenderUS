package com.hcmus.tenderus.model

import com.hcmus.tenderus.utils.getCurrentDateTimeIso


data class Match(
    val username: String = "",
    val avatarIcon: String = "",
    val displayName: String = "",
    val createdAt: String = getCurrentDateTimeIso(),
    val isActive: Boolean = false,
    val isRead: Boolean = false,
    val messageArr: List<Message> = listOf()
)