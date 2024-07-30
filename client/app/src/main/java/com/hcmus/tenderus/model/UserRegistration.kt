package com.hcmus.tenderus.model

data class UserRegistration(
    var username: String = "",
    var password: String = "",
    var email: String = "",
    var phone: String = "",
    var token: String = ""
)
