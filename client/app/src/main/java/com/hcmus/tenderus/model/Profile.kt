package com.hcmus.tenderus.model

data class Profile(
    val username: String = "",
    val role: String = "",
    val displayName: String = "",
    val avatarIcon: String = "",
    val pictures: List<String> = listOf(),
    val description: String = "",

    val longitude: Float,
    val latitude: Float,

    val identity: String = "",
    val age: Int,
    val interests: List<String> = listOf(),
    val groups: List<String> = listOf(),

    val isActive: Boolean
)