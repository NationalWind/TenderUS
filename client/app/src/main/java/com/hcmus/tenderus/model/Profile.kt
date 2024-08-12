package com.hcmus.tenderus.model

data class Profile(
    var displayName: String = "",
    var avatarIcon: String = "",
    val pictures: List<String> = listOf(),
    val description: String = "",

    val longitude: Float,
    val latitude: Float,

    var identity: String = "",
    var birthDate: String = "",
    val interests: List<String> = listOf(),
    val groups: List<String> = listOf(),

    val isActive: Boolean
)