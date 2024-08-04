package com.hcmus.tenderus.model



data class Match(
    val username: String = "",
    val avatarIcon: String = "",
    val displayName: String = "",
    val createdAt: String = "",
    val isActive: Boolean = false,
    val messageArr: List<Message> = listOf()
)