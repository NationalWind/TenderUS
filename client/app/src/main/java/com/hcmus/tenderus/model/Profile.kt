package com.hcmus.tenderus.model

data class Profile(
    var displayName: String? = null,
    var avatarIcon: String? = null,
    val pictures: List<String>? = null,
    val description: String? = null,

    val longitude: Float? = null,
    val latitude: Float? = null,

    var identity: String? = null,
    var birthDate: String? = null,
    val interests: List<String>? = null,
    val groups: List<String>? = null,

    val isActive: Boolean? = null
)