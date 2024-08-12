package com.hcmus.tenderus.model

data class Preference (
    val ageMin: Int,
    val ageMax: Int,
    val maxDist: Float,
    val showMe: String,
)