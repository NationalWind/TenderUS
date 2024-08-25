package com.hcmus.tenderus.model

data class Preference (
    val ageMin: Int = 0,
    val ageMax: Int = 100,
    val maxDist: Float = 10000f,
    val showMe: String = "",
)