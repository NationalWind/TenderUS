package com.hcmus.tenderus.model

enum class Role { USER, ADMIN }

data class Account(
    val FCMRegToken: String = ""
    val username: String,
    val role: Role,
    val email: String,
    val phone: String,
)
