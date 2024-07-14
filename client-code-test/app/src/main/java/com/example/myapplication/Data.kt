package com.example.myapplication
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.util.concurrent.TimeUnit

@Serializable
data class User(val username: String, val password: String, val email: String, val phone: String, val token: String) {}
