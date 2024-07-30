package com.hcmus.tenderus.model

data class UserLogin (
    var username: String = "",
    var password: String = "",
    var email: String = "",
    var phone: String = "",
    var FCMRegToken: String = ""
)
