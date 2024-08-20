package com.hcmus.tenderus.model

enum class Role { USER, ADMIN }

data class Account(
    val id: String,
    val username: String,
    val role: Role,
    val avatar: String?,
    val email: String?,
    val phone: String?,
    val penalty: List<String> = listOf(),
    val FCMRegToken: String = "",
)
